package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.ComponentFunction;
import jp.jyn.jbukkitlib.config.parser.template.variable.ComponentVariable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>TextComponent template parser (Thread-safe)</p>
 * <p>Available format:</p>
 * <ul>
 * <li>{function(arg1,arg2)} -&gt; function</li>
 * <li>{variable} -&gt; variable</li>
 * <li>&amp; -&gt; escape</li>
 * <li>&amp;(char) -&gt; ColorCode</li>
 * <li>&amp;&amp; -&gt; &amp;</li>
 * </ul>
 */
public class ComponentParser extends AbstractParser {
    private final static ComponentVariable EMPTY_VARIABLE = ComponentVariable.init();
    private final static ComponentFunction EMPTY_FUNCTION = ComponentFunction.init();

    private final Node[] node;

    private ComponentParser(Collection<Node> node) {
        this.node = node.toArray(new Node[0]);
    }

    /**
     * Getting TextComponent
     *
     * @param variable Variable
     * @param function Function
     * @return TextComponent array
     */
    public TextComponent[] getComponents(ComponentVariable variable, ComponentFunction function) {
        TextComponent[] components = new TextComponent[node.length];
        for (int i = 0; i < node.length; i++) {
            components[i] = node[i].apply(variable, function);
        }
        return components;
    }

    /**
     * Getting TextComponent
     *
     * @param variable Variable
     * @return TextComponent array
     */
    public TextComponent[] getComponents(ComponentVariable variable) {
        return getComponents(variable, EMPTY_FUNCTION);
    }

    /**
     * Getting TextComponent
     *
     * @param function Function
     * @return TextComponent array
     */
    public TextComponent[] getComponents(ComponentFunction function) {
        return getComponents(EMPTY_VARIABLE, function);
    }

    /**
     * Getting TextComponent
     *
     * @return TextComponent array
     */
    public TextComponent[] getComponents() {
        return getComponents(EMPTY_VARIABLE, EMPTY_FUNCTION);
    }

    /**
     * Sending action bar
     *
     * @param player   target player
     * @param variable Variable
     * @param function Function
     * @return for method chain
     */
    public ComponentParser actionbar(Player player, ComponentVariable variable, ComponentFunction function) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, getComponents(variable, function));
        return this;
    }

    /**
     * Sending action bar
     *
     * @param player   target player
     * @param variable Variable
     * @return for method chain
     */
    public ComponentParser actionbar(Player player, ComponentVariable variable) {
        return actionbar(player, variable, EMPTY_FUNCTION);
    }

    /**
     * Sending action bar
     *
     * @param player   target player
     * @param function Function
     * @return for method chain
     */
    public ComponentParser actionbar(Player player, ComponentFunction function) {
        return actionbar(player, EMPTY_VARIABLE, function);
    }

    /**
     * Sending action bar
     *
     * @param player target player
     * @return for method chain
     */
    public ComponentParser actionbar(Player player) {
        return actionbar(player, EMPTY_VARIABLE, EMPTY_FUNCTION);
    }

    /**
     * Sending system message
     *
     * @param player   target player
     * @param variable Variable
     * @param function Function
     * @return for method chain
     */
    public ComponentParser send(Player player, ComponentVariable variable, ComponentFunction function) {
        player.spigot().sendMessage(ChatMessageType.SYSTEM, getComponents(variable, function));
        return this;
    }

    /**
     * Sending system message
     *
     * @param player   target player
     * @param variable Variable
     * @return for method chain
     */
    public ComponentParser send(Player player, ComponentVariable variable) {
        return send(player, variable, EMPTY_FUNCTION);
    }

    /**
     * Sending system message
     *
     * @param player   target player
     * @param function Function
     * @return for method chain
     */
    public ComponentParser send(Player player, ComponentFunction function) {
        return send(player, EMPTY_VARIABLE, function);
    }

    /**
     * Sending system message
     *
     * @param player target player
     * @return for method chain
     */
    public ComponentParser send(Player player) {
        return send(player, EMPTY_VARIABLE, EMPTY_FUNCTION);
    }

    /**
     * Sending broadcast message
     *
     * @param variable Variable
     * @param function Function
     * @return for method chain
     */
    public ComponentParser broadcast(ComponentVariable variable, ComponentFunction function) {
        Bukkit.spigot().broadcast(getComponents(variable, function));
        return this;
    }

    /**
     * Sending broadcast message
     *
     * @param variable Variable
     * @return for method chain
     */
    public ComponentParser broadcast(ComponentVariable variable) {
        return broadcast(variable, EMPTY_FUNCTION);
    }

    /**
     * Sending broadcast message
     *
     * @param function Function
     * @return for method chain
     */
    public ComponentParser broadcast(ComponentFunction function) {
        return broadcast(EMPTY_VARIABLE, function);
    }

    /**
     * Sending broadcast message
     *
     * @return for method chain
     */
    public ComponentParser broadcast() {
        return broadcast(EMPTY_VARIABLE, EMPTY_FUNCTION);
    }

    @Override
    public String toString() {
        return "ComponentParser{" + Arrays.toString(node) + '}';
    }

    /**
     * Parses a string.
     *
     * @param sequence Char sequence
     * @return Parsed value.
     */
    public static ComponentParser parse(CharSequence sequence) {
        Queue<String> expr = exprQueue(sequence);
        List<Node> node = new LinkedList<>();

        StringBuilder buf = new StringBuilder();
        TextComponent last = new TextComponent();
        while (!expr.isEmpty()) {
            String fragment = expr.remove();
            if (fragment.isEmpty()) {
                continue;
            }
            char first = fragment.charAt(0);

            // { first && } last -> variable or function
            if (first == '{' && fragment.charAt(fragment.length() - 1) == '}') {
                if (buf.length() != 0) {
                    BaseComponent[] components = TextComponent.fromLegacyText(buf.toString());
                    buf.setLength(0);

                    components[0].copyFormatting(last, ComponentBuilder.FormatRetention.FORMATTING, false);
                    last = (TextComponent) components[components.length - 1];

                    for (BaseComponent component : components) {
                        // fromLegacyText is always TextComponents
                        node.add(new TextNode((TextComponent) component));
                    }
                }

                String name = fragment.substring(1, fragment.length() - 1);
                TextComponent component = new TextComponent(last);

                // contains ( -> function()
                if (name.indexOf('(') >= 0) {
                    Map.Entry<String, String[]> func = parseFunction(name);
                    node.add(new FunctionNode(component, func.getKey(), func.getValue()));
                } else {
                    node.add(new VariableNode(component, name));
                }

                last = component;
                continue;
            }

            // &{ &1 &z...
            if (first == '&') {
                char second = fragment.charAt(1);
                switch (second) { // escape
                    case '{':
                    case '}':
                    case '&':
                        buf.append(second);
                        continue;
                }

                ChatColor color = ChatColor.getByChar(second);
                if (color == null) {
                    // &z -> &z
                    buf.append(first).append(second);
                } else {
                    // &1 &2 -> ChatColor
                    buf.append(color.toString());
                }
                continue;
            }

            // others...
            buf.append(fragment);
        }

        if (buf.length() != 0) {
            BaseComponent[] components = TextComponent.fromLegacyText(buf.toString());
            buf.setLength(0);

            components[0].copyFormatting(last, ComponentBuilder.FormatRetention.FORMATTING, false);

            for (BaseComponent component : components) {
                // fromLegacyText is always TextComponents
                node.add(new TextNode((TextComponent) component));
            }
        }

        return new ComponentParser(node);
    }

    private interface Node {
        TextComponent apply(ComponentVariable variable, ComponentFunction function);
    }

    private static class TextNode implements Node {
        private final TextComponent component;

        private TextNode(TextComponent component) {
            this.component = component;
        }

        @Override
        public TextComponent apply(ComponentVariable variable, ComponentFunction function) {
            return component;
        }

        @Override
        public String toString() {
            return "TextNode{" + "component=" + component + '}';
        }
    }

    private static class VariableNode implements Node {
        private final TextComponent component;
        private final String name;

        private VariableNode(TextComponent component, String name) {
            this.component = component;
            this.name = name;
        }

        @Override
        public TextComponent apply(ComponentVariable variable, ComponentFunction function) {
            TextComponent newComponent = component.duplicate();

            Consumer<TextComponent> consumer = variable.get(name);
            if (consumer == null) {
                consumer = c -> c.setText("{" + name + "}"); // unknown variable(typo... etc)
            }
            consumer.accept(newComponent);

            return newComponent;
        }

        @Override
        public String toString() {
            return "VariableNode{" +
                "name='" + name + '\'' +
                ", component=" + component +
                '}';
        }
    }

    private static class FunctionNode implements Node {
        private final TextComponent component;
        private final String name;
        private final String[] args;

        private FunctionNode(TextComponent component, String name, String[] args) {
            this.name = name;
            this.args = args;
            this.component = component;
        }

        @Override
        public TextComponent apply(ComponentVariable variable, ComponentFunction function) {
            TextComponent newComponent = component.duplicate();

            BiConsumer<TextComponent, String[]> consumer = function.get(name);
            if (consumer == null) {
                consumer = (c, a) -> c.setText("{" + name + "(" + String.join(",", a) + ")" + "}"); // unknown function
            }
            consumer.accept(newComponent, args);

            return newComponent;
        }

        @Override
        public String toString() {
            return "FunctionNode{" +
                "name='" + name + '\'' +
                ", args=" + Arrays.toString(args) +
                ", component=" + component +
                '}';
        }
    }
}
