package jp.jyn.jbukkitlib.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * ActionBar Sender
 *
 * @deprecated Use {@link org.bukkit.entity.Player.Spigot#sendMessage(ChatMessageType, BaseComponent)}
 */
@Deprecated
public class ActionBarSender {
    /**
     * <p>Init ActionBarSender</p>
     */
    public ActionBarSender() { }

    /**
     * Reset action bar
     *
     * @param player target player
     */
    public void reset(Player player) {
        send(player, "");
    }

    /**
     * Send action bar
     *
     * @param player  target player
     * @param message message
     */
    public void send(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
