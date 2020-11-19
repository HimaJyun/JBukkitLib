package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.EmptyVariable;
import jp.jyn.jbukkitlib.config.parser.template.variable.TemplateVariable;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Simple template parser (Thread-Safe)</p>
 * <p>Available format:</p>
 * <ul>
 * <li>{variable} -&gt; variable</li>
 * <li>&amp;(char) -&gt; ColorCode</li>
 * <li>#ffffff -&gt hex color</li>
 * <li>\ -&gt; escape</li>
 * </ul>
 */
public class StringParser implements TemplateParser {
    private final static ThreadLocal<StringBuilder> LOCAL_BUILDER = ThreadLocal.withInitial(StringBuilder::new);
    private final List<Node> nodes;

    private StringParser(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Parses a string.
     *
     * @param str input value.
     * @return Parsed value.
     */
    public static TemplateParser parse(String str) {
        List<Node> nodes = new ArrayList<>();

        StringBuilder sb = LOCAL_BUILDER.get();
        sb.setLength(0);
        for (Parser.Node node : Parser.parse(str)) {
            switch (node.type) {
                case STRING:
                    sb.append(node.getValue());
                    break;
                case MC_COLOR:
                    sb.append(ChatColor.COLOR_CHAR).append(node.getValue());
                    break;
                case HEX_COLOR:
                    sb.append(ChatColor.COLOR_CHAR).append('x');
                    String v = node.getValue();
                    for (int i = 0; i < v.length(); i++) {
                        sb.append(ChatColor.COLOR_CHAR).append(v.charAt(i));
                    }
                    break;
                case VARIABLE:
                    if (sb.length() != 0) {
                        nodes.add(new StringNode(sb.toString()));
                        sb.setLength(0);
                    }
                    nodes.add(new VariableNode(node.getValue()));
                    break;
            }
        }
        if (sb.length() != 0) {
            nodes.add(new StringNode(sb.toString()));
        }

        // string only
        if (nodes.size() == 0) {
            return new RawStringParser("");
        } else if (nodes.size() == 1 && (nodes.get(0) instanceof StringNode)) {
            return new RawStringParser(nodes.get(0).toString());
        }

        return new StringParser(nodes);
    }

    @Override
    public String toString(TemplateVariable variable) {
        StringBuilder builder = LOCAL_BUILDER.get();
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
