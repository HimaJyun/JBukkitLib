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

    private final CharSequence sequence;

    // 理論上はLinkedListの方が高速だが、要素数が少ないと予想されるためプリフェッチなどでArrayListの方が高速な可能性がある。
    private final List<Node> nodes = new ArrayList<>();
    private final StringBuilder sb = new StringBuilder();
    private int cursor = 0;

    private Parser(CharSequence sequence) {
        this.sequence = sequence;
    }

    @PackagePrivate
    static List<Node> parse(CharSequence sequence) {
        Parser p = new Parser(sequence);

        int pos;
        while ((pos = p.find()) != -1) {
            p.text(p.cursor, pos);
            switch (sequence.charAt(pos)) {
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
        p.text(p.cursor, sequence.length());

        return p.nodes;
    }

    private int find() {
        for (int i = cursor; i < sequence.length(); i++) {
            switch (sequence.charAt(i)) {
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
            addString(sequence.subSequence(start, end).toString());
        }
    }

    private void escape(int pos) {
        if (range(pos, 1)) {
            addString("\\");
            this.cursor = pos + 1;
            return;
        }
        char c = sequence.charAt(pos + 1);
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
        for (int i = pos + 1; i < sequence.length(); i++) {
            char c = sequence.charAt(i);

            // 入れ子レベル変動
            int j = c == '{' ? +1 : c == '}' ? -1 : 0;
            if (j != 0) {
                if (sequence.charAt(i - 1) == '\\') { // \{
                    sb.deleteCharAt(sb.length() - 1); // 前の \ を消す -> "\"なら"{"や"}"に、"\\"なら"\{"や"\}"になるようにする。
                    if (sequence.charAt(i - 2) == '\\') { // \\{
                        nest += j;
                    }
                } else {
                    nest += j;
                }
            }

            if (nest == 0) {
                nodes.add(new Node(Type.VARIABLE, sb.toString()));
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

        ChatColor c = ChatColor.getByChar(sequence.charAt(pos + 1));
        if (c == null) {
            addString("&");
            this.cursor = pos + 1;
        } else {
            nodes.add(new Node(Type.MC_COLOR, String.valueOf(c.getChar())));
            this.cursor = pos + 2;
        }
    }

    private void hex(int pos) {
        if (range(pos, 3)) {
            addString("#");
            this.cursor = pos + 1;
            return;
        }

        int lim = Math.min(pos + 7, sequence.length());
        for (int i = pos + 1; i < lim; i++) {
            char c = sequence.charAt(i);
            if (isHex(c)) {
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

    private boolean isHex(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        } else return c >= 'a' && c <= 'f';
    }

    private boolean range(int pos, int length) {
        return pos + length >= sequence.length();
    }

    // Node結合(SBで前の値を持っておいて、後から入れ替える？)
    @PackagePrivate
    static class Node {
        @PackagePrivate
        final Type type;
        private CharSequence value;

        Node(Type type, CharSequence value) {
            this.type = type;
            this.value = value;
        }

        @PackagePrivate
        CharSequence getValue() {
            return value;
        }
    }
}
