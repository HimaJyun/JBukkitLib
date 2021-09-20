package jp.jyn.jbukkitlib.config.parser.component;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * variable applied component
 */
public interface Component extends Cloneable {
    Component NOOP = new Component() {
        @Override
        public Component actionbar(Player player) {return this;}

        @Override
        public Component actionbar(Iterable<Player> players) {return this;}

        @Override
        public Component actionbar(Player... players) {return this;}

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

    /**
     * Sending actionbar.
     *
     * @param player target player
     * @return for method chain
     */
    default Component actionbar(Player player) {
        return send(ChatMessageType.ACTION_BAR, player);
    }

    /**
     * Sending actionbar.
     *
     * @param players target players
     * @return for method chain
     */
    default Component actionbar(Iterable<Player> players) {
        for (var player : players) {
            actionbar(player);
        }
        return this;
    }

    /**
     * Sending actionbar.
     *
     * @param players target players
     * @return for method chain
     */
    default Component actionbar(Player... players) {
        for (var player : players) {
            actionbar(player);
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
    Component send(ChatMessageType position, Player player);

    /**
     * Sending message.
     *
     * @param position the screen position
     * @param players  target players
     * @return for method chain
     */
    default Component send(ChatMessageType position, Iterable<Player> players) {
        for (var player : players) {
            send(position, player);
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
    default Component send(ChatMessageType position, Player... players) {
        for (var player : players) {
            send(position, player);
        }
        return this;
    }

    /**
     * Sending message.
     *
     * @param sender target sender
     * @return for method chain
     */
    Component send(CommandSender sender);

    /**
     * Sending message.
     *
     * @param senders target senders
     * @return for method chain
     */
    default Component send(Iterable<CommandSender> senders) {
        for (var sender : senders) {
            send(sender);
        }
        return this;
    }

    /**
     * Sending message.
     *
     * @param senders target senders
     * @return for method chain
     */
    default Component send(CommandSender... senders) {
        for (var sender : senders) {
            send(sender);
        }
        return this;
    }

    /**
     * Sending broadcast message.
     *
     * @param console if true, it will be displayed on the console.
     * @return for method chain
     */
    Component broadcast(boolean console);

    /**
     * Sending broadcast message.
     *
     * @return for method chain
     */
    default Component broadcast() {
        return broadcast(false);
    }

    /**
     * Display a text on the console.
     *
     * @return for method chain
     */
    Component console();

    /**
     * Logging at the specified log level.
     *
     * @param logger logger
     * @param level  log level
     * @return for method chain
     */
    Component log(Logger logger, Level level);

    /**
     * Logging at the {@link Level#INFO} log level.
     *
     * @param logger logger
     * @return for method chain
     */
    default Component log(Logger logger) {
        return log(logger, Level.INFO);
    }

    /**
     * <p>Get text of this components.</p>
     *
     * <p>This method removes the decoration.
     * Use {@link #toLegacyText()} if you need decoration.</p>
     *
     * @return plain text
     */
    String toPlaintext();

    /**
     * <p>Get text of this components.</p>
     *
     * <p>This method includes the decoration.
     * Use {@link #toPlaintext()} if you not need decoration.</p>
     *
     * @return legacy text
     */
    String toLegacyText();

    /**
     * <p>Gets an array of TextComponents.</p>
     *
     * <p>This method return new copy of the TextComponent,
     * so modifying the returned array does not affect the original object.</p>
     *
     * @return TextComponent array
     */
    TextComponent[] toTextComponent();


    /**
     * <p>Returns a String representation of this object.</p>
     *
     * <p>This method is not meant to get the text to send to the player.
     * Use {@link #toPlaintext()} or {@link #toLegacyText()} in such cases.</p>
     *
     * @return String representation
     */
    String toString();
}
