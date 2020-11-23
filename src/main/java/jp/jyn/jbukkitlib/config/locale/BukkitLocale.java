package jp.jyn.jbukkitlib.config.locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Use different objects depending on the player's locale.
 *
 * @param <T> Type
 */
public interface BukkitLocale<T> extends Iterable<T> {
    /**
     * Get object for specified locale.
     *
     * @param locale target locale
     * @return specified locale object
     */
    T get(String locale);

    /**
     * Get object for specified player locale.
     * Use {@link Player#getLocale()}, must use an exact matching locale name.
     *
     * @param player target player
     * @return player locale object, use if not exists default locale.
     */
    default T get(Player player) {
        return get(player.getLocale());
    }

    /**
     * Get object for specified sender locale.
     * Use {@link #get(Player)} if the sender is a player, else use {@link #get()}.
     *
     * @param sender target sender
     * @return sender locale object, use if not exists default locale.
     */
    default T get(CommandSender sender) {
        return sender instanceof Player ? get((Player) sender) : get();
    }

    /**
     * Get object for default locale.
     *
     * @return default locale
     */
    T get();

    /**
     * Iterate all locales.
     *
     * @param action action
     */
    void forEach(BiConsumer<String, ? super T> action);

    /**
     * Return {@link Set} of the all locales.
     *
     * @return locale set
     */
    Set<Map.Entry<String, T>> entrySet();
}
