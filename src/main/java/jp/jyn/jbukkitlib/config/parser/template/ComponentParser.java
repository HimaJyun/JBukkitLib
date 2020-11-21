package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.ComponentVariable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * @param variable variable
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
     * @param variable variable
     * @param player   target player
     * @return for method chain
     */
    public ComponentParser actionbar(ComponentVariable variable, Player player) {
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
        return actionbar(EMPTY_VARIABLE, player);
    }

    /**
     * Sending action bar
     *
     * @param variable variable
     * @param players  target players
     * @return for method chain
     */
    public ComponentParser actionbar(ComponentVariable variable, Iterable<Player> players) {
        TextComponent[] components = getComponents(variable);
        for (Player player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
        }
        return this;
    }

    /**
     * Sending action bar
     *
     * @param players target players
     * @return for method chain
     */
    public ComponentParser actionbar(Iterable<Player> players) {
        return actionbar(EMPTY_VARIABLE, players);
    }

    /**
     * Sending action bar
     *
     * @param variable variable
     * @param players  target players
     * @return for method chain
     */
    public ComponentParser actionbar(ComponentVariable variable, Player... players) {
        TextComponent[] components = getComponents(variable);
        for (Player player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
        }
        return this;
    }

    /**
     * Sending action bar
     *
     * @param players target players
     * @return for method chain
     */
    public ComponentParser actionbar(Player... players) {
        return actionbar(EMPTY_VARIABLE, players);
    }

    /**
     * Sending message
     *
     * @param variable variable
     * @param sender   target sender
     * @return for method chain
     */
    public ComponentParser send(ComponentVariable variable, CommandSender sender) {
        sender.spigot().sendMessage(getComponents(variable));
        return this;
    }

    /**
     * Sending message
     *
     * @param sender target sender
     * @return for method chain
     */
    public ComponentParser send(CommandSender sender) {
        return send(EMPTY_VARIABLE, sender);
    }

    /**
     * Sending message
     *
     * @param variable variable
     * @param senders  target senders
     * @return for method chain
     */
    public ComponentParser send(ComponentVariable variable, Iterable<CommandSender> senders) {
        TextComponent[] components = getComponents(variable);
        for (CommandSender sender : senders) {
            sender.spigot().sendMessage(components);
        }
        return this;
    }

    /**
     * Sending message
     *
     * @param senders target senders
     * @return for method chain
     */
    public ComponentParser send(Iterable<CommandSender> senders) {
        return send(EMPTY_VARIABLE, senders);
    }

    /**
     * Sending message
     *
     * @param variable variable
     * @param senders  target senders
     * @return for method chain
     */
    public ComponentParser send(ComponentVariable variable, CommandSender... senders) {
        TextComponent[] components = getComponents(variable);
        for (CommandSender sender : senders) {
            sender.spigot().sendMessage(components);
        }
        return this;
    }

    /**
     * Sending message
     *
     * @param senders target senders
     * @return for method chain
     */
    public ComponentParser send(CommandSender... senders) {
        return send(EMPTY_VARIABLE, senders);
    }

    /**
     * Sending broadcast message
     *
     * @param variable variable
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

    /**
     * Sending broadcast message
     *
     * @param variable variable
     * @param console  if true, it will be displayed on the console.
     * @return for method chain
     */
    public ComponentParser broadcast(ComponentVariable variable, boolean console) {
        TextComponent[] components = getComponents(variable);
        if (console) {
            Bukkit.getConsoleSender().spigot().sendMessage(components);
        }
        Bukkit.spigot().broadcast(components);
        return this;
    }

    /**
     * Sending broadcast message
     *
     * @param console If true, it will be displayed on the console.
     * @return for method chain
     */
    public ComponentParser broadcast(boolean console) {
        return broadcast(EMPTY_VARIABLE, console);
    }

    /**
     * Display a text on the console.
     *
     * @param variable variable
     * @return for method chain
     */
    public ComponentParser console(ComponentVariable variable) {
        Bukkit.getConsoleSender().spigot().sendMessage(getComponents(variable));
        return this;
    }

    /**
     * Display a text on the console.
     *
     * @return for method chain
     */
    public ComponentParser console() {
        return console(EMPTY_VARIABLE);
    }

    /**
     * Logs at the specified log level.
     *
     * @param variable variable
     * @param logger   logger
     * @param level    log level
     * @return for method chain
     */
    public ComponentParser log(ComponentVariable variable, Logger logger, Level level) {
        logger.log(level, getText(variable));
        return this;
    }

    /**
     * Logs at the specified log level.
     *
     * @param logger logger
     * @param level  log level
     * @return for method chain
     */
    public ComponentParser log(Logger logger, Level level) {
        return log(EMPTY_VARIABLE, logger, level);
    }

    /**
     * Gets the text of this object.
     * This method removes the decoration. Use {@link ComponentParser#getComponents(ComponentVariable)} if you need decoration.
     *
     * @return Parsed text
     */
    public String getText(ComponentVariable variable) {
        StringBuilder sb = new StringBuilder(); // たぶんそこまで呼び出し回数多くないので都度作成する (使いまわさない)
        for (TextComponent component : getComponents(variable)) {
            sb.append(component.getText());
        }
        return sb.toString();
    }

    /**
     * Gets the text of this object.
     * This method removes the decoration. Use {@link ComponentParser#getComponents()} if you need decoration.
     *
     * @return Parsed text
     */
    public String getText() {
        return getText(EMPTY_VARIABLE);
    }

    /**
     * Returns a String representation of this object.
     * This method is not meant to get the text of this object. Use {@link ComponentParser#getText()} in such cases.
     *
     * @return String representation
     */
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
