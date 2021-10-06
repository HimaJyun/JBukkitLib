package jp.jyn.jbukkitlib.config.parser.component;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * variable applied component
 */
public class Component implements Cloneable {
    /**
     * NO-OP Component.
     */
    public final static Component NOOP = new Component(null) {
        @Override
        public Component actionbar(Player player) {return this;}

        @Override
        public Component actionbar(Iterable<Player> players) {return this;}

        @Override
        public Component actionbar(Player... players) {return this;}

        @Override
        public Component title(String subtitle, int fadeIn, int stay, int fadeOut, Player player) {return this;}

        @Override
        public Component title(String subtitle, int fadeIn, int stay, int fadeOut, Iterable<Player> players) {return this;}

        @Override
        public Component title(String subtitle, int fadeIn, int stay, int fadeOut, Player... players) {return this;}

        @Override
        public Component subtitle(String title, int fadeIn, int stay, int fadeOut, Player player) {return this;}

        @Override
        public Component subtitle(String title, int fadeIn, int stay, int fadeOut, Iterable<Player> players) {return this;}

        @Override
        public Component subtitle(String title, int fadeIn, int stay, int fadeOut, Player... players) {return this;}

        @Override
        public Component send(ChatMessageType position, Player player) {return this;}

        @Override
        public Component send(ChatMessageType position, Iterable<Player> players) {return this;}

        @Override
        public Component send(ChatMessageType position, Player... players) {return this;}

        @Override
        public Component send(CommandSender sender) {return this;}

        @Override
        public Component send(Iterable<CommandSender> senders) {return this;}

        @Override
        public Component send(CommandSender... senders) {return this;}

        @Override
        public Component broadcast(boolean console) {return this;}

        @Override
        public Component broadcast() {return this;}

        @Override
        public Component console() {return this;}

        @Override
        public Component log(Logger logger, Level level) {return this;}

        @Override
        public Component log(Logger logger) {return this;}

        @Override
        public String toPlaintext() {return "";}

        @Override
        public String toLegacyText() {return "";}

        @Override
        public TextComponent[] toTextComponent() {return new TextComponent[0];}

        @Override
        public String toString() {return "Component.NOOP{}";}

        @Override
        public boolean equals(Object obj) {return obj == this;}

        @Override
        public int hashCode() {return System.identityHashCode(this);}

        @SuppressWarnings("MethodDoesntCallSuperMethod") // IF互換のため
        @Override
        public Component clone() {return this;}
    };

    private final TextComponent[] components;
    // オブジェクトの参照/代入はアトミック + toLegacyText()を複数回実行してしまうことは許容できるのでvolatile不要
    private String legacyText;

    public Component(TextComponent[] components) {
        this.components = components;
    }

    /**
     * Sending actionbar.
     *
     * @param player target player
     * @return for method chain
     */
    public Component actionbar(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
        return this;
    }

    /**
     * Sending actionbar.
     *
     * @param players target players
     * @return for method chain
     */
    public Component actionbar(Iterable<Player> players) {
        for (var player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
        }
        return this;
    }

    /**
     * Sending actionbar.
     *
     * @param players target players
     * @return for method chain
     */
    public Component actionbar(Player... players) {
        for (var player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
        }
        return this;
    }

    /**
     * Sending title.<br>
     * See: {@link Player#sendTitle(String, String, int, int, int)}
     *
     * @param subtitle subtitle. if you need other Component, use {@link Component#toLegacyText()}.
     * @param fadeIn   time in ticks for titles to fade in. Defaults to 10.
     * @param stay     time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut  time in ticks for titles to fade out. Defaults to 20.
     * @param player   target player.
     * @return for method chain
     */
    public Component title(String subtitle, int fadeIn, int stay, int fadeOut, Player player) {
        player.sendTitle(toLegacyText(), subtitle, fadeIn, stay, fadeOut);
        return this;
    }

    /**
     * Sending title.<br>
     * See: {@link Player#sendTitle(String, String, int, int, int)}
     *
     * @param subtitle subtitle. if you need other Component, use {@link Component#toLegacyText()}.
     * @param fadeIn   time in ticks for titles to fade in. Defaults to 10.
     * @param stay     time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut  time in ticks for titles to fade out. Defaults to 20.
     * @param players  target players.
     * @return for method chain
     */
    public Component title(String subtitle, int fadeIn, int stay, int fadeOut, Iterable<Player> players) {
        var title = toLegacyText();
        for (Player player : players) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
        return this;
    }

    /**
     * Sending title.<br>
     * See: {@link Player#sendTitle(String, String, int, int, int)}
     *
     * @param subtitle subtitle. if you need other Component, use {@link Component#toLegacyText()}.
     * @param fadeIn   time in ticks for titles to fade in. Defaults to 10.
     * @param stay     time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut  time in ticks for titles to fade out. Defaults to 20.
     * @param players  target players.
     * @return for method chain
     */
    public Component title(String subtitle, int fadeIn, int stay, int fadeOut, Player... players) {
        var title = toLegacyText();
        for (Player player : players) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
        return this;
    }

    /**
     * Sending subtitle.<br>
     * See: {@link Player#sendTitle(String, String, int, int, int)}
     *
     * @param title   title. if you need other Component, use {@link Component#toLegacyText()}.
     * @param fadeIn  time in ticks for titles to fade in. Defaults to 10.
     * @param stay    time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut time in ticks for titles to fade out. Defaults to 20.
     * @param player  target player.
     * @return for method chain
     */
    public Component subtitle(String title, int fadeIn, int stay, int fadeOut, Player player) {
        player.sendTitle(title, toLegacyText(), fadeIn, stay, fadeOut);
        return this;
    }

    /**
     * Sending subtitle.<br>
     * See: {@link Player#sendTitle(String, String, int, int, int)}
     *
     * @param title   title. if you need other Component, use {@link Component#toLegacyText()}.
     * @param fadeIn  time in ticks for titles to fade in. Defaults to 10.
     * @param stay    time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut time in ticks for titles to fade out. Defaults to 20.
     * @param players target players.
     * @return for method chain
     */
    public Component subtitle(String title, int fadeIn, int stay, int fadeOut, Iterable<Player> players) {
        var subtitle = toLegacyText();
        for (Player player : players) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
        return this;
    }

    /**
     * Sending subtitle.<br>
     * See: {@link Player#sendTitle(String, String, int, int, int)}
     *
     * @param title   title. if you need other Component, use {@link Component#toLegacyText()}.
     * @param fadeIn  time in ticks for titles to fade in. Defaults to 10.
     * @param stay    time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut time in ticks for titles to fade out. Defaults to 20.
     * @param players target players.
     * @return for method chain
     */
    public Component subtitle(String title, int fadeIn, int stay, int fadeOut, Player... players) {
        var subtitle = toLegacyText();
        for (Player player : players) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
        return this;
    }

    /**
     * Sending message.
     *
     * @param position the screen position
     * @param player   target player
     * @return for method chain
     */
    public Component send(ChatMessageType position, Player player) {
        player.spigot().sendMessage(position, components);
        return this;
    }

    /**
     * Sending message.
     *
     * @param position the screen position
     * @param players  target players
     * @return for method chain
     */
    public Component send(ChatMessageType position, Iterable<Player> players) {
        for (var player : players) {
            player.spigot().sendMessage(position, components);
        }
        return this;
    }

    /**
     * Sending message.
     *
     * @param position the screen position
     * @param players  target players
     * @return for method chain
     */
    public Component send(ChatMessageType position, Player... players) {
        for (var player : players) {
            player.spigot().sendMessage(position, components);
        }
        return this;
    }

    /**
     * Sending message.
     *
     * @param sender target sender
     * @return for method chain
     */
    public Component send(CommandSender sender) {
        sender.spigot().sendMessage(components);
        return this;
    }

    /**
     * Sending message.
     *
     * @param senders target senders
     * @return for method chain
     */
    public Component send(Iterable<CommandSender> senders) {
        for (var sender : senders) {
            sender.spigot().sendMessage(components);
        }
        return this;
    }

    /**
     * Sending message.
     *
     * @param senders target senders
     * @return for method chain
     */
    public Component send(CommandSender... senders) {
        for (var sender : senders) {
            sender.spigot().sendMessage(components);
        }
        return this;
    }

    /**
     * Sending broadcast message.
     *
     * @param console if true, it will be displayed on the console.
     * @return for method chain
     */
    public Component broadcast(boolean console) {
        if (console) {
            Bukkit.getConsoleSender().spigot().sendMessage(components);
        }
        Bukkit.spigot().broadcast(components);
        return this;
    }

    /**
     * Sending broadcast message.
     *
     * @return for method chain
     */
    public Component broadcast() {
        Bukkit.spigot().broadcast(components);
        return this;
    }

    /**
     * Display a text on the console.
     *
     * @return for method chain
     */
    public Component console() {
        Bukkit.getConsoleSender().spigot().sendMessage(components);
        return this;
    }

    /**
     * Logging at the specified log level.
     *
     * @param logger logger
     * @param level  log level
     * @return for method chain
     */
    public Component log(Logger logger, Level level) {
        logger.log(level, this::toPlaintext);
        return this;
    }

    /**
     * Logging at the {@link Level#INFO} log level.
     *
     * @param logger logger
     * @return for method chain
     */
    public Component log(Logger logger) {
        logger.info(this::toPlaintext);
        return this;
    }

    /**
     * <p>Get text of this components.</p>
     *
     * <p>This method removes the decoration.
     * Use {@link #toLegacyText()} if you need decoration.</p>
     *
     * @return plain text
     */
    public String toPlaintext() {
        StringBuilder sb = new StringBuilder();
        for (TextComponent component : components) {
            sb.append(component.toPlainText());
        }
        return sb.toString();
    }

    /**
     * <p>Get text of this components.</p>
     *
     * <p>This method includes the decoration.
     * Use {@link #toPlaintext()} if you not need decoration.</p>
     *
     * @return legacy text
     */
    public String toLegacyText() {
        if (legacyText != null) {
            return legacyText;
        }

        StringBuilder sb = new StringBuilder();
        for (TextComponent component : components) {
            sb.append(component.toLegacyText());
        }
        return (legacyText = sb.toString());
    }

    /**
     * <p>Gets an array of TextComponents.</p>
     *
     * <p>This method return new copy of the TextComponent,
     * so modifying the returned array does not affect the original object.</p>
     *
     * @return TextComponent array
     */
    public TextComponent[] toTextComponent() {
        // と～っても非効率だけどイミュータブルにするためにはこうするしかない (そのまま返すと変更できてしまう)
        TextComponent[] c = new TextComponent[components.length];
        for (int i = 0; i < components.length; i++) {
            c[i] = components[i].duplicate();
        }
        return c;
    }

    /**
     * <p>Returns a String representation of this object.</p>
     *
     * <p>This method is not meant to get the text to send to the player.
     * Use {@link #toPlaintext()} or {@link #toLegacyText()} in such cases.</p>
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "Component{" + Arrays.toString(components) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Arrays.equals(components, component.components);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(components);
    }

    @Override
    public Component clone() {
        try {
            Component clone = (Component) super.clone();
            for (int i = 0; i < this.components.length; i++) {
                clone.components[i] = this.components[i].duplicate();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
