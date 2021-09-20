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
public class ArrayComponent implements Component {
    private final TextComponent[] components;

    public ArrayComponent(TextComponent[] components) {
        this.components = components;
    }

    @Override
    public ArrayComponent send(ChatMessageType position, Player player) {
        player.spigot().sendMessage(position, components);
        return this;
    }

    @Override
    public ArrayComponent send(CommandSender sender) {
        sender.spigot().sendMessage(components);
        return this;
    }

    @Override
    public ArrayComponent broadcast(boolean console) {
        if (console) {
            Bukkit.getConsoleSender().spigot().sendMessage(components);
        }
        Bukkit.spigot().broadcast(components);
        return this;
    }

    @Override
    public ArrayComponent console() {
        Bukkit.getConsoleSender().spigot().sendMessage(components);
        return this;
    }

    @Override
    public ArrayComponent log(Logger logger, Level level) {
        logger.log(level, this::toPlaintext);
        return this;
    }

    @Override
    public String toPlaintext() {
        var sb = new StringBuilder();
        for (var component : components) {
            sb.append(component.toPlainText());
        }
        return sb.toString();
    }

    @Override
    public String toLegacyText() {
        var sb = new StringBuilder();
        for (var component : components) {
            sb.append(component.toLegacyText());
        }
        return sb.toString();
    }

    @Override
    public TextComponent[] toTextComponent() {
        // と～っても非効率だけどイミュータブルにするためにはこうするしかない (そのまま返すと変更できてしまう)
        var c = new TextComponent[components.length];
        for (int i = 0; i < components.length; i++) {
            c[i] = components[i].duplicate();
        }
        return c;
    }

    @Override
    public String toString() {
        return "SimpleComponent{" + Arrays.toString(components) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var component = (ArrayComponent) o;
        return Arrays.equals(components, component.components);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(components);
    }

    @Override
    public ArrayComponent clone() {
        try {
            var clone = (ArrayComponent) super.clone();
            for (int i = 0; i < this.components.length; i++) {
                clone.components[i] = this.components[i].duplicate();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
