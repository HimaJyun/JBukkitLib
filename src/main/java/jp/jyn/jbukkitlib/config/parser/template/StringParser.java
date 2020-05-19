package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.EmptyVariable;
import jp.jyn.jbukkitlib.config.parser.template.variable.TemplateVariable;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
        Queue<String> expr = exprQueue(sequence);
        List<Node> nodes = new ArrayList<>(expr.size());
        while (!expr.isEmpty()) {
            String value = expr.remove();
            if (value.isEmpty()) {
                continue;
            }
            char c = value.charAt(0);

            if (c == '{' && value.charAt(value.length() - 1) == '}') { // variable
                nodes.add(new VariableNode(value.substring(1, value.length() - 1)));
                continue;
            }

            if (c == '&') {
                char c2 = value.charAt(1);
                switch (c2) { // escape
                    case '{':
                    case '}':
                    case '&':
                        nodes.add(new StringNode(Character.toString(c2)));
                        continue;
                }

                ChatColor color = ChatColor.getByChar(c2);
                if (color != null) {
                    // &1 &2 -> ChatColor
                    nodes.add(new StringNode(color.toString()));
                } else {
                    // &z -> &z
                    nodes.add(new StringNode(value));
                }
                continue;
            }

            // string
            nodes.add(new StringNode(value));
        }

        // concat string nodes.
        List<Node> newNodes = new ArrayList<>(nodes.size());
        StringBuilder buf = localBuilder.get();
        buf.setLength(0);

        for (Node node : nodes) {
            if (node.isImmutable()) {
                buf.append(node.toString());
            } else {
                if (buf.length() != 0) {
                    newNodes.add(new StringNode(buf.toString()));
                    buf.setLength(0);
                }
                newNodes.add(node);
            }
        }
        if (buf.length() != 0) {
            newNodes.add(new StringNode(buf.toString()));
        }

        // string only
        if (newNodes.size() == 0) {
            return new RawStringParser("");
        } else if (newNodes.size() == 1 && newNodes.get(0).isImmutable()) {
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

        boolean isImmutable();
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

        @Override
        public boolean isImmutable() {
            return true;
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

        @Override
        public boolean isImmutable() {
            return false;
        }
    }
}
