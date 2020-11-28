package jp.jyn.jbukkitlib.config.parser.component;

import jp.jyn.jbukkitlib.config.parser.MinecraftParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>TextComponent template parser (Thread-safe)</p>
 * <p>Valid format:</p>
 * <ul>
 * <li>{function(arg1,arg2)} -&gt; function</li>
 * <li>{variable} -&gt; variable</li>
 * <li>&amp;(char) -&gt; ColorCode</li>
 * <li>#ffffff -&gt; hex color</li>
 * <li>&amp; -&gt; escape</li>
 * </ul>
 */
@FunctionalInterface
public interface ComponentParser {
    /**
     * applying variable.
     *
     * @param variable variable
     * @return variable applied component
     */
    Component apply(ComponentVariable variable);

    /**
     * applying empty variable.
     *
     * @return variable applied component
     */
    default Component apply() {
        return apply(ComponentVariable.EMPTY_VARIABLE);
    }

    /**
     * applying single variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default Component apply(String key, Consumer<TextComponent> value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * applying single function.
     *
     * @param key   function name
     * @param value function value
     * @return function applied component
     */
    default Component apply(String key, BiConsumer<TextComponent, List<String>> value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, String value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * <p>applying single variable.</p>
     *
     * <p>{@link Object#toString()} is execute at the time it is used.
     * That is, the thread and timing to be executed is undefined.</p>
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, Object value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, Supplier<String> value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * <p>applying multiple variable.</p>
     *
     * <p>Arguments should be key and value alternately.
     * If not, the extra key will be ignored.</p>
     *
     * @param values key value pair (eg: {@code ["key1", "value2", "key2", "value2"]})
     * @return variable applied component
     */
    default Component apply(String... values) {
        return apply(ComponentVariable.init().put(values));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, int value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, long value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, double value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, char value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * applying single variable.
     *
     * @param key   variable key
     * @param value variable value
     * @return variable applied component
     */
    default Component apply(String key, boolean value) {
        return apply(ComponentVariable.init().put(key, value));
    }

    /**
     * <p>Parse string.</p>
     * <p>Valid format:</p>
     * <ul>
     * <li>{function(arg1,arg2)} -&gt; function</li>
     * <li>{variable} -&gt; variable</li>
     * <li>&amp;(char) -&gt; ColorCode</li>
     * <li>#ffffff -&gt; hex color</li>
     * <li>&amp; -&gt; escape</li>
     * </ul>
     *
     * @param str input value
     * @return parsed value
     */
    static ComponentParser parse(String str) {
        // see net.md_5.bungee.api.chat.TextComponent#fromLegacyText
        final ChatColor DEFAULT_COLOR = ChatColor.WHITE;

        List<Node> nodes = new ArrayList<>();
        TextComponent component = new TextComponent();
        for (MinecraftParser.Node node : MinecraftParser.parse(str)) {
            switch (node.type) {
                case HEX_COLOR:
                    // &k#aaa みたいにすると前の&kは消されてしまうが、Spigotのコードがそうなっているのでここではその挙動を真似る
                    component = new TextComponent();
                    component.setColor(ChatColor.of('#' + node.getValue()));
                    break;

                case MC_COLOR:
                    ChatColor c = ChatColor.getByChar(node.getValue().charAt(0));
                    if (c == null) { // たぶん到達不能
                        throw new IllegalArgumentException("Invalid color format: " + node.getValue());
                    }
                    if (c == ChatColor.BOLD) {
                        component.setBold(true);
                    } else if (c == ChatColor.ITALIC) {
                        component.setItalic(true);
                    } else if (c == ChatColor.UNDERLINE) {
                        component.setUnderlined(true);
                    } else if (c == ChatColor.STRIKETHROUGH) {
                        component.setStrikethrough(true);
                    } else if (c == ChatColor.MAGIC) {
                        component.setObfuscated(true);
                    } else if (c == ChatColor.RESET) {
                        component = new TextComponent();
                        component.setColor(DEFAULT_COLOR);
                    } else {
                        // やはり&k&a みたいに色指定が装飾より後ろにあると打ち消されるが、Spigotに合わせる
                        component = new TextComponent();
                        component.setColor(c);
                    }
                    break;

                case STRING:
                    TextComponent text = component;
                    component = new TextComponent(text);
                    text.setText(node.getValue());
                    nodes.add(new Node.TextNode(text));
                    break;

                case URL:
                    TextComponent url = component;
                    component = new TextComponent(url);
                    component.setText(node.getValue());
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, node.getValue()));
                    nodes.add(new Node.TextNode(component));
                    component = url;
                    break;

                case VARIABLE:
                    TextComponent variable = component;
                    component = new TextComponent(variable);

                    if (node.getValue().indexOf('(') != -1) {
                        Map.Entry<String, List<String>> func = MinecraftParser.parseFunction(node.getValue());
                        nodes.add(new Node.FunctionNode(variable, func.getKey(), func.getValue()));
                    } else {
                        nodes.add(new Node.VariableNode(variable, node.getValue()));
                    }
                    break;
            }
        }

        return Node.build(nodes);
    }
}
