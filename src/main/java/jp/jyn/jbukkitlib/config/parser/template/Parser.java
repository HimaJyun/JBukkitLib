package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.util.PackagePrivate;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@PackagePrivate
class Parser {
    @PackagePrivate
    enum Type {
        STRING,
        VARIABLE,
        HEX_COLOR,
        MC_COLOR,
    }

    private final String str;

    // 理論上はLinkedListの方が高速だが、要素数が少ないと予想されるためプリフェッチなどでArrayListの方が高速な可能性がある。
    private final List<Node> nodes = new ArrayList<>();
    private final StringBuilder sb = new StringBuilder();
    private int cursor = 0;

    private Parser(String str) {
        this.str = str;
    }

    @PackagePrivate
    static List<Node> parse(String str) {
        Parser p = new Parser(str);

        int pos;
        while ((pos = p.find()) != -1) {
            p.text(p.cursor, pos);
            switch (str.charAt(pos)) {
                case '\\':
                    p.escape(pos);
                    break;
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
        p.text(p.cursor, str.length());

        return p.nodes;
    }

    private int find() {
        for (int i = cursor; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '\\':
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

    private void escape(int pos) {
        if (range(pos, 1)) {
            addString("\\");
            this.cursor = pos + 1;
            return;
        }
        char c = str.charAt(pos + 1);
        switch (c) {
            case '\\':
            case '{':
            case '&':
            case '#':
                addString(String.valueOf(c));
                this.cursor = pos + 2;
                return;
        }
        addString(sb.append('\\').append(c).toString());
        this.cursor = pos + 2;
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
                if (str.charAt(i - 1) == '\\') { // \{
                    // sb.deleteCharAt(sb.length() - 1); // System.arraycopyを発生させる
                    sb.setLength(sb.length() - 1); // 前の \ を消す -> "\"なら"{"や"}"に、"\\"なら"\{"や"\}"になるようにする。
                    if (str.charAt(i - 2) == '\\') { // \\{
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

    @PackagePrivate
    static class Node {
        @PackagePrivate
        final Type type;
        private String value;

        Node(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        @PackagePrivate
        String getValue() {
            return value;
        }
    }
}
