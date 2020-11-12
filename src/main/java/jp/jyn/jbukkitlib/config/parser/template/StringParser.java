package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.EmptyVariable;
import jp.jyn.jbukkitlib.config.parser.template.variable.TemplateVariable;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Simple template parser</p>
 * <p>Available format:</p>
 * <ul>
 * <li>{variable} -&gt; variable</li>
 * <li>&amp; -&gt; escape</li>
 * <li>&amp;(char) -&gt; ColorCode</li>
 * <li>&amp;&amp; -&gt; &amp;</li>
 * </ul>
 */
public class StringParser extends AbstractParser implements TemplateParser {
    // | で先にあるものが先にヒットする仕様に依存しているので注意
    // \\[\\{}&#]|\{.+?\}|&[0-9a-fk-or]|#[0-9a-f]{6}|#[0-9a-f]{3}
    // \\ud[8-b][0-9a-f]{2}\\u[c-f][0-9a-f]{2}|\\u[0-9a-f]{4}
    private final static Pattern pattern = Pattern.compile("\\\\[\\\\{}&#]|\\{.+?\\}|&[0-9a-fk-or]|#[0-9a-f]{6}|#[0-9a-f]{3}");

    protected final static ThreadLocal<StringBuilder> localBuilder = ThreadLocal.withInitial(StringBuilder::new);
    private final List<Node> nodes;

    private StringParser(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Parses a string.
     *
     * @param sequence Char sequence
     * @return Parsed value.
     */
    public static TemplateParser parse(CharSequence sequence) {
        StringBuilder sb = new StringBuilder();
        List<Node> nodes = new LinkedList<>();

        int end = 0;
        Matcher m = pattern.matcher(sequence);
        while (m.find()) {
            // 空パターンが大量に入るが、後で消すので問題ない (その辺を判定すればsubSequenceが減らせるので効率は上がる)
            nodes.add(new StringNode(sequence.subSequence(end, m.start()).toString()));
            end = m.end();

            // エスケープ
            if (sequence.charAt(m.start()) == '\\') {
                nodes.add(new StringNode(String.valueOf(sequence.charAt(m.start() + 1))));
                continue;
            }

            // 変数
            if (sequence.charAt(m.start()) == '{') {
                nodes.add(new VariableNode(sequence.subSequence(m.start() + 1, m.end() - 1).toString()));
                continue;
            }

            // カラーコード
            if (sequence.charAt(m.start()) == '&') {
                nodes.add(new StringNode(ChatColor.getByChar(sequence.charAt(m.end() - 1)).toString()));
                continue;
            }

            // 16進数色指定
            if (sequence.charAt(m.start()) == '#') {
                sb.append(ChatColor.COLOR_CHAR).append('x');
                boolean reuse = (m.end() - m.start()) == 4;
                for (int i = m.start() + 1; i < m.end(); i++) {
                    char c = sequence.charAt(i);
                    sb.append(ChatColor.COLOR_CHAR).append(c);
                    if (reuse) {
                        sb.append(ChatColor.COLOR_CHAR).append(c);
                    }
                }
                nodes.add(new StringNode(sb.toString()));
                sb.setLength(0);
                continue;
            }

            // 誤ヒット？
            throw new IllegalArgumentException("Invalid input: " + sequence.subSequence(m.start(), m.end()));
        }
        nodes.add(new StringNode(sequence.subSequence(end, sequence.length()).toString()));

        // ノード結合
        List<Node> newNodes = new ArrayList<>();
        for (Node node : nodes) {
            if (node instanceof StringNode) {
                sb.append(node.toString());
            } else {
                if (sb.length() != 0) {
                    newNodes.add(new StringNode(sb.toString()));
                    sb.setLength(0);
                }
                newNodes.add(node);
            }
        }
        if (sb.length() != 0) {
            newNodes.add(new StringNode(sb.toString()));
        }

        // string only
        if (newNodes.size() == 0) {
            return new RawStringParser("");
        } else if (newNodes.size() == 1 && (newNodes.get(0) instanceof StringNode)) {
            return new RawStringParser(newNodes.get(0).toString());
        }

        return new StringParser(newNodes);
    }

    @Override
    public String toString(TemplateVariable variable) {
        StringBuilder builder = localBuilder.get();
        builder.setLength(0);

        for (Node node : nodes) {
            node.toString(builder, variable);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return toString(EmptyVariable.getInstance());
    }

    private interface Node {
        void toString(StringBuilder builder, TemplateVariable variable);
    }

    private static class StringNode implements Node {
        private final String value;

        private StringNode(String value) {
            this.value = value;
        }

        @Override
        public void toString(StringBuilder builder, TemplateVariable variable) {
            builder.append(value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static class VariableNode implements Node {
        private final String key;

        private VariableNode(String key) {
            this.key = key;
        }

        @Override
        public void toString(StringBuilder builder, TemplateVariable variable) {
            String value = variable.get(key);
            if (value == null) {
                // unknown variable(typo... etc)
                builder.append('{').append(key).append('}');
            } else {
                builder.append(value);
            }
        }

        @Override
        public String toString() {
            return "{" + key + "}";
        }
    }
}
