package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.EmptyVariable;
import jp.jyn.jbukkitlib.config.parser.template.variable.TemplateVariable;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Simple template parser</p>
 * <p>Available format:</p>
 * <ul>
 * <li>{variable} -&gt; variable</li>
 * <li>&amp;(char) -&gt; ColorCode</li>
 * <li>#ffffff -&gt hex color</li>
 * <li>\ -&gt; escape</li>
 * </ul>
 */
public class StringParser implements TemplateParser {
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
        List<Parser.Node> nodes = Parser.parse(sequence);
        List<Node> newNodes = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (Parser.Node node : nodes) {
            switch (node.type) {
                case STRING:
                    sb.append(node.getValue());
                    break;
                case MC_COLOR:
                    sb.append(ChatColor.COLOR_CHAR).append(node.getValue());
                    break;
                case HEX_COLOR:
                    sb.append(ChatColor.COLOR_CHAR).append('x');
                    CharSequence seq = node.getValue();
                    for (int i = 0; i < seq.length(); i++) {
                        sb.append(ChatColor.COLOR_CHAR).append(seq.charAt(i));
                    }
                    break;
                case VARIABLE:
                    if (sb.length() != 0) {
                        newNodes.add(new StringNode(sb.toString()));
                        sb.setLength(0);
                    }
                    newNodes.add(new VariableNode(node.getValue().toString()));
                    break;
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
