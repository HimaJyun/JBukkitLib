package jp.jyn.jbukkitlib.config.parser;

import org.bukkit.ChatColor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MinecraftParser {
    public enum Type {
        STRING,
        VARIABLE,
        HEX_COLOR,
        MC_COLOR,
        URL,
    }

    // 理論上はLinkedListの方が高速だが、要素数が少ないと予想されるためプリフェッチなどでArrayListの方が高速な可能性がある。
    private final List<Node> nodes = new ArrayList<>();
    private final StringBuilder sb = new StringBuilder();
    private int cursor = 0;
    private String str;

    private MinecraftParser() { }

    /**
     * Valid format
     * <ul>
     *     <li>aaa{aaa}aaa   -> [String:aaa] [Variable:aaa] [String:aaa]   (Variable enclosed by "{ }")</li>
     *     <li>aaa{a{a}a}aaa -> [String:aaa] [Variable:a{a}a] [String:aaa] (Variables can be nest)</li>
     *     <li>aaa{aa&}a}aaa -> [String:aaa] [Variable:aa}a] [String:aaa]  ("}" escaped by "&")</li>
     *     <li>aaa{a&{aa}aaa -> [String:aaa] [Variable:a{aa] [String:aaa]  ("{" escaped by "&")</li>
     *     <li>aaa{aaa&&}aaa -> [String:aaa] [Variable:aaa&] [String:aaa]  ("&" escaped by "&")</li>
     *     <li>aaa{ aaa }aaa -> [String:aaa] [Variable:aaa] [String:aaa]   (leading and trailing whitespace trimmed)</li>
     *     <li>aaa{a a a}aaa -> [String:aaa] [Variable:a a a] [String:aaa] (trim only for leading and trailing)</li>
     *     <li>aaa&aaaaaa -> [String:aaa] [MC_COLOR:a] [String:aaaaa] (Minecraft color code uses "&")</li>
     *     <li>aaa&Aaaaaa -> [String:aaa] [MC_COLOR:a] [String:aaaaa] (Uppercase color code convert to lowercase)</li>
     *     <li>aaa#aaaaaa -> [String:aaa] [HEX_COLOR:aaaaaa]              (Web color code uses "#")</li>
     *     <li>aaa#aaazzz -> [String:aaa] [HEX_COLOR:aaaaaa] [String:zzz] (3-digit color code convert to 6-digit)</li>
     *     <li>aaa#AAAAAA -> [String:aaa] [HEX_COLOR:aaaaaa]              (Uppercase color code convert to lowercase)</li>
     *     <li>&{a&}&#aaa&&a -> [String:{a}#aaa&a]     ("{", "}", "#" or "&" escaped by "&")</li>
     *     <li>&/&$&%&|&!& -> [String:&/&$&%&|&!&] (escape only for "{", "}", "#" or "&")</li>
     *     <li>https://example.com/ http://example.com/ -> [URL:https://example.com/] [String: ] [URL:http://example.com/]
     *         (URLs start with "http://" or "https://" and end at the end of a line or blank)</li>
     *     <li>https://example.com/?a&aaa#fff aaa       -> [URL:https://example.com/?a&aaa#fff] [String: aaa]
     *         (URLs are parsed in preference to other special characters.)</li>
     *     <li>aaa example.com aaa -> [String:aaa example.com aaa] (Domain-only URLs are treated as String)</li>
     * </ul>
     *
     * @param str input string
     * @return parsed value
     */
    public static List<Node> parse(String str) {
        MinecraftParser p = new MinecraftParser();

        for (Map.Entry<Boolean, String> entry : split(str)) {
            if (entry.getKey()) {
                p.nodes.add(new Node(Type.URL, entry.getValue()));
                continue;
            }

            p.str = entry.getValue();
            p.cursor = 0;
            int pos;
            while ((pos = p.find()) != -1) {
                p.text(p.cursor, pos);
                switch (p.str.charAt(pos)) {
                    case '{':
                        p.variable(pos);
                        break;
                    case '&':
                        p.color(pos);
                        break;
                    case '#':
                        p.hex(pos);
                        break;
                }
            }
            p.text(p.cursor, p.str.length());
        }

        return p.nodes;
    }

    private static List<Map.Entry<Boolean, String>> split(String str) {
        // URLには#や&が含まれており誤パースの可能性が高いから先に分割する
        // Spigotの挙動と異なる点あり
        List<Map.Entry<Boolean, String>> list = new ArrayList<>();

        int last = 0;
        int pos = 0;
        while ((pos = str.indexOf("http", pos)) != -1) {
            list.add(new AbstractMap.SimpleImmutableEntry<>(false, str.substring(last, pos)));

            if (str.startsWith("https://", pos) || str.startsWith("http://", pos)) {
                int start = pos;
                if ((pos = str.indexOf(' ', start)) == -1) {
                    pos = str.length();
                }
                list.add(new AbstractMap.SimpleImmutableEntry<>(true, str.substring(start, pos)));
            }

            last = pos;
        }

        list.add(new AbstractMap.SimpleImmutableEntry<>(false, str.substring(last)));
        return list;
    }

    private int find() {
        for (int i = cursor; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '{':
                case '&':
                case '#':
                    return i;
            }
        }
        return -1;
    }

    private void addString(String str) {
        if (nodes.isEmpty()) {
            nodes.add(new Node(Type.STRING, str));
            return;
        }

        Node n = nodes.get(nodes.size() - 1);
        if (n.type != Type.STRING) {
            nodes.add(new Node(Type.STRING, str));
            return;
        }

        // 2つのNodeを結合
        sb.setLength(0);
        n.value = sb.append(n.value).append(str).toString();
        sb.setLength(0);
    }

    private void text(int start, int end) {
        if (start != end) {
            addString(str.substring(start, end));
        }
    }

    private void variable(int pos) {
        if (range(pos, 1)) {
            addString("{");
            this.cursor = pos + 1;
            return;
        }

        int nest = 1;
        for (int i = pos + 1; i < str.length(); i++) {
            char c = str.charAt(i);

            // 入れ子レベル変動
            int j = c == '{' ? +1 : c == '}' ? -1 : 0;
            if (j != 0) {
                if (str.charAt(i - 1) == '&') { // &{
                    sb.setLength(sb.length() - 1); // 前の & を消す -> "&"なら"{"や"}"に、"&&"なら"&{"や"&}"になるようにする。
                    if (str.charAt(i - 2) == '&') { // &&{
                        nest += j;
                    }
                } else {
                    nest += j;
                }
            }

            if (nest == 0) {
                // { aaa } こういうパターンの時は使いやすさのために{aaa}として扱う。
                // { a a } ただし、こういうパターンの時は{a a}として扱う。(そうしないと関数に含まれる文字列で空白を扱えない)
                // 要するに、最初と最後の空白を削除する -> trim()
                // trim()はStringBuilderを操作するよりStringでやる方が速い -> https://stackoverflow.com/questions/5212928/how-to-trim-a-Java-stringbuilder
                nodes.add(new Node(Type.VARIABLE, sb.toString().trim()));
                sb.setLength(0);
                this.cursor = i + 1;
                return;
            }
            sb.append(c);
        }

        // ここまで来た == 閉じられていない == 変数を意図していない
        addString("{");
        this.cursor = pos + 1;
    }

    private void color(int pos) {
        if (range(pos, 1)) {
            addString("&");
            this.cursor = pos + 1;
            return;
        }

        char c = str.charAt(pos + 1);
        switch (c) { // escape
            case '{':
            case '}':
            case '&':
            case '#':
                addString(String.valueOf(c));
                this.cursor = pos + 2;
                return;
        }

        if (c >= 'A' && c <= 'Z') {
            c += 32; // 小文字化
        }

        ChatColor color = ChatColor.getByChar(c);
        if (color == null) {
            addString("&");
            this.cursor = pos + 1;
        } else {
            nodes.add(new Node(Type.MC_COLOR, String.valueOf(color.getChar())));
            this.cursor = pos + 2;
        }
    }

    private void hex(int pos) {
        if (range(pos, 3)) {
            addString("#");
            this.cursor = pos + 1;
            return;
        }

        int lim = Math.min(pos + 7, str.length());
        for (int i = pos + 1; i < lim; i++) {
            char c = str.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')) {
                sb.append(c);
            } else if (c >= 'A' && c <= 'F') {
                c += 32; // 小文字化
                sb.append(c);
            } else {
                break;
            }
        }

        if (sb.length() == 6) {
            this.cursor = pos + 7;
        } else if (sb.length() >= 3) {
            char r = sb.charAt(0);
            char g = sb.charAt(1);
            char b = sb.charAt(2);
            sb.setLength(0);
            sb.append(r).append(r).append(g).append(g).append(b).append(b);
            this.cursor = pos + 4;
        } else {
            addString("#");
            this.cursor = pos + 1;
            return;
        }

        nodes.add(new Node(Type.HEX_COLOR, sb.toString()));
        sb.setLength(0);
    }

    private boolean range(int pos, int length) {
        return pos + length >= str.length();
    }

    public static class Node {
        public final Type type;
        private String value;

        private Node(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Valid format
     * <ul>
     *     <li>aaa()            -> aaa []              (allowed empty arguments)</li>
     *     <li>aaa(bbb)         -> aaa ["bbb"]         (arguments enclosed by "( )")</li>
     *     <li>aaa(bbb,ccc)     -> aaa ["bbb","ccc"]   (arguments delimited by ",")</li>
     *     <li>aaa(b&,bb,ccc)   -> aaa ["b,bb","ccc"]  ("," escaped by "&")</li>
     *     <li>aaa(b&"bb,ccc)   -> aaa ["b\"bb","ccc"] ("\"" escaped by "&")</li>
     *     <li>aaa(b&(bb,ccc)   -> aaa ["b)bb","ccc"]  ("(" escaped by "&")</li>
     *     <li>aaa(b&)bb,ccc)   -> aaa ["b)bb","ccc"]  (")" escaped by "&")</li>
     *     <li>aaa(b&&bb,ccc)   -> aaa ["b&bb","ccc"]  ("&" escaped by "&")</li>
     *     <li>aaa(b&bb,ccc)    -> aaa ["b&bb","ccc"]  (escape only for "\"", ",","(", ")" or "&")</li>
     *     <li>aaa(b b b,ccc)   -> aaa ["bbb","ccc"]   (white-space ignored)</li>
     *     <li>aaa("b b b",ccc) -> aaa ["b b b","ccc"] (white-space allowed only in "\"")</li>
     *     <li>aaa("b,)b",ccc)  -> aaa ["b,)b","ccc"]  (escape for "," and ")" can be omit in "\"")</li>
     * </ul>
     * function name is allowed  in any characters except parentheses. (even if it empty)<br>
     * but, common characters ([a-zA-Z0-9_]) are recommended.
     *
     * @param str input string
     * @return parsed value (key=name, value=args)
     */
    public static Map.Entry<String, List<String>> parseFunction(String str) {
        String name;
        List<String> args = new ArrayList<>(); // 初期容量(=10)を超えさえしなければ、LinkedListより速い -> 引数の個数が10を超える可能性は低い

        int argsIndex = str.indexOf('(');
        if (argsIndex == -1) {
            throw new IllegalArgumentException("Not a function");
        }
        name = str.substring(0, argsIndex);

        String rawArgs = str.substring(argsIndex + 1);
        if (rawArgs.length() == 1 && rawArgs.charAt(0) == ')') {
            // empty args
            return new AbstractMap.SimpleImmutableEntry<>(name, Collections.emptyList());
        }

        StringBuilder buf = new StringBuilder();
        boolean escape = false;
        boolean quote = false;

        // OUT: for...IDEA settings does not support this style.
        OUT:
        for (int i = 0; i < rawArgs.length(); i++) {
            char c = rawArgs.charAt(i);

            if (escape) {
                switch (c) {
                    case '"':
                    case '&':
                    case ',':
                    case '(':
                    case ')': // &" & &, &(, &) -> " & , ( )
                        buf.append(c);
                        break;
                    default: // &a -> &a
                        buf.append('&').append(c);
                        break;
                }
                escape = false;
                continue;
            }

            if (quote) {
                switch (c) {
                    case '&':
                        escape = true;
                        break;
                    case '"':
                        quote = false;
                        break;
                    default:
                        buf.append(c);
                        break;
                }
                continue;
            }

            switch (c) {
                case '"':
                    quote = true;
                    continue;
                case '&':
                    escape = true;
                    continue;
                case ' ':
                    continue;
                case ',':
                    args.add(buf.toString());
                    buf.setLength(0);
                    continue;
                case ')':
                    break OUT;
                default:
                    buf.append(c);
                    break;
            }
        }
        args.add(buf.toString());

        return new AbstractMap.SimpleImmutableEntry<>(name, args);
    }
}
