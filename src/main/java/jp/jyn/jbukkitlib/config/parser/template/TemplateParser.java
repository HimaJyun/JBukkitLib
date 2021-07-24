package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.MinecraftParser;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>Simple template parser (Thread-Safe)</p>
 * <p>Valid format:</p>
 * <ul>
 * <li>{variable} -&gt; variable</li>
 * <li>&amp;(char) -&gt; ColorCode</li>
 * <li>#ffffff -&gt; hex color</li>
 * <li>&amp; -&gt; escape</li>
 * </ul>
 */
@FunctionalInterface
public interface TemplateParser {
    /**
     * applying variable.
     *
     * @param variable variable
     * @return variable applied String
     */
    String apply(TemplateVariable variable);

    /**
     * applying empty variable.
     *
     * @return variable applied String
     */
    default String apply() {
        return apply(TemplateVariable.EMPTY_VARIABLE);
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, String value) {
        return apply(StringVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, Supplier<String> value) {
        return apply(SupplierVariable.init().put(key, value));
    }

    /**
     * <p>applying single variable.</p>
     *
     * <p>{@link Object#toString()} is execute at the time it is used.
     * That is, the thread and timing to be executed is undefined.</p>
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, Object value) {
        return apply(SupplierVariable.init().put(key, value));
    }

    /**
     * <p>applying multiple variable.</p>
     *
     * <p>Arguments should be key and value alternately.
     * If not, the extra key will be ignored.</p>
     *
     * @param values key value pair (eg: {@code ["key1", "value2", "key2", "value2"]})
     * @return variable applied String
     */
    default String apply(String... values) {
        return apply(StringVariable.init().put(values));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, int value) {
        return apply(StringVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, long value) {
        return apply(StringVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, double value) {
        return apply(StringVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, char value) {
        return apply(StringVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied String
     */
    default String apply(String key, boolean value) {
        return apply(StringVariable.init().put(key, value));
    }

    /**
     * <p>Parse string.</p>
     * <p>Valid format:</p>
     * <ul>
     * <li>{variable} -&gt; variable</li>
     * <li>&amp;(char) -&gt; ColorCode</li>
     * <li>#ffffff -&gt; hex color</li>
     * <li>&amp; -&gt; escape</li>
     * </ul>
     *
     * @param str input value
     * @return parsed value
     */
    static TemplateParser parse(String str) {
        List<Node> nodes = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (var node : MinecraftParser.parse(str)) {
            switch (node.type) {
                case URL, STRING:
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
                        nodes.add(new Node.StringNode(sb.toString()));
                        sb.setLength(0);
                    }
                    nodes.add(new Node.VariableNode(node.getValue()));
                    break;
            }
        }
        if (sb.length() != 0) {
            nodes.add(new Node.StringNode(sb.toString()));
        }

        return Node.build(nodes);
    }
}
