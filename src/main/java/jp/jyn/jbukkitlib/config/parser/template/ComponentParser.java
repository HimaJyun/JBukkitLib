package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.ComponentVariable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>TextComponent template parser (Thread-safe)</p>
 * <p>Available format:</p>
 * <ul>
 * <li>{function(arg1,arg2)} -&gt; function</li>
 * <li>{variable} -&gt; variable</li>
 * <li>&amp;(char) -&gt; ColorCode</li>
 * <li>#ffffff -&gt; hex color</li>
 * <li>\ -&gt; escape</li>
 * </ul>
 */
public class ComponentParser {
    private final static ComponentVariable EMPTY_VARIABLE = ComponentVariable.init();

    private final Node[] node;

    private ComponentParser(Collection<Node> node) {
        this.node = node.toArray(new Node[0]);
    }

    /**
     * Getting TextComponent
     *
     * @param variable Variable
     * @return TextComponent array
     */
    public TextComponent[] getComponents(ComponentVariable variable) {
        TextComponent[] components = new TextComponent[node.length];
        for (int i = 0; i < node.length; i++) {
            components[i] = node[i].apply(variable);
        }
        return components;
    }

    /**
     * Getting TextComponent
     *
     * @return TextComponent array
     */
    public TextComponent[] getComponents() {
        return getComponents(EMPTY_VARIABLE);
    }

    /**
     * Sending action bar
     *
     * @param player   target player
     * @param variable Variable
     * @return for method chain
     */
    public ComponentParser actionbar(Player player, ComponentVariable variable) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, getComponents(variable));
        return this;
    }

    /**
     * Sending action bar
     *
     * @param player target player
     * @return for method chain
     */
    public ComponentParser actionbar(Player player) {
        return actionbar(player, EMPTY_VARIABLE);
    }

    /**
     * Sending system message
     *
     * @param player   target player
     * @param variable Variable
     * @return for method chain
     */
    public ComponentParser send(Player player, ComponentVariable variable) {
        player.spigot().sendMessage(ChatMessageType.SYSTEM, getComponents(variable));
        return this;
    }

    /**
     * Sending system message
     *
     * @param player target player
     * @return for method chain
     */
    public ComponentParser send(Player player) {
        return send(player, EMPTY_VARIABLE);
    }

    /**
     * Sending broadcast message
     *
     * @param variable Variable
     * @return for method chain
     */
    public ComponentParser broadcast(ComponentVariable variable) {
        Bukkit.spigot().broadcast(getComponents(variable));
        return this;
    }

    /**
     * Sending broadcast message
     *
     * @return for method chain
     */
    public ComponentParser broadcast() {
        return broadcast(EMPTY_VARIABLE);
    }

    @Override
    public String toString() {
        return "ComponentParser{" + Arrays.toString(node) + '}';
    }

    /**
     * Parses a string.
     *
     * @param str input value.
     * @return Parsed value.
     */
    public static ComponentParser parse(String str) {
        // see net.md_5.bungee.api.chat.TextComponent#fromLegacyText
        final ChatColor DEFAULT_COLOR = ChatColor.WHITE;

        List<Node> nodes = new ArrayList<>();
        TextComponent component = new TextComponent();
        for (Parser.Node node : Parser.parse(str)) {
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
                    nodes.add(new TextNode(text));
                    break;

                case URL:
                    TextComponent url = component;
                    component = new TextComponent(url);
                    component.setText(node.getValue());
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, node.getValue()));
                    nodes.add(new TextNode(component));
                    component = url;
                    break;

                case VARIABLE:
                    TextComponent variable = component;
                    component = new TextComponent(variable);

                    if (node.getValue().indexOf('(') != -1) {
                        Map.Entry<String, String[]> func = Parser.parseFunction(node.getValue());
                        nodes.add(new FunctionNode(variable, func.getKey(), func.getValue()));
                    } else {
                        nodes.add(new VariableNode(variable, node.getValue()));
                    }
                    break;
            }
        }

        return new ComponentParser(nodes);
    }

    private interface Node {
        TextComponent apply(ComponentVariable variable);
    }

    private static class TextNode implements Node {
        private final TextComponent component;

        private TextNode(TextComponent component) {
            this.component = component;
        }

        @Override
        public TextComponent apply(ComponentVariable variable) {
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
        public TextComponent apply(ComponentVariable variable) {
            TextComponent newComponent = component.duplicate();

            Consumer<TextComponent> consumer = variable.getVariable(name);
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
        public TextComponent apply(ComponentVariable variable) {
            TextComponent newComponent = component.duplicate();

            BiConsumer<TextComponent, String[]> consumer = variable.getFunction(name);
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
