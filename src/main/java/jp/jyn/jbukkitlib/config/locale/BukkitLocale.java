package jp.jyn.jbukkitlib.config.locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
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
     * Get objects for specified players locale.
     *
     * @param players target players
     * @return locale object mapping
     */
    default Map<T, List<Player>> get(Player... players) {
        switch (players.length) {
            case 0:
                return Collections.emptyMap();
            case 1:
                return Collections.singletonMap(get(players[0]), Collections.singletonList(players[0]));
        }

        Map<T, List<Player>> r = new IdentityHashMap<>();
        for (Player player : players) {
            T obj = get(player);
            r.computeIfAbsent(obj, ign -> new ArrayList<>()).add(player);
        }
        return r;
    }

    /**
     * Get objects for specified senders locale.
     *
     * @param senders target senders
     * @return locale object mapping
     */
    default Map<T, List<CommandSender>> get(CommandSender... senders) {
        switch (senders.length) {
            case 0:
                return Collections.emptyMap();
            case 1:
                return Collections.singletonMap(get(senders[0]), Collections.singletonList(senders[0]));
        }

        Map<T, List<CommandSender>> r = new IdentityHashMap<>();
        for (CommandSender sender : senders) {
            T obj = get(sender);
            r.computeIfAbsent(obj, ign -> new ArrayList<>()).add(sender);
        }
        return r;
    }

    /**
     * <p>Get objects for specified senders locale.</p>
     * <p>This method is an alias for {@link #getSenders(Collection)} for consistency in our API's.
     * If you need the {@code Map<T, List<Player>>} object, use {@link #getPlayers(Collection)}.</p>
     *
     * @param senders target senders
     * @return locale object mapping
     */
    default Map<T, List<CommandSender>> get(Collection<? extends CommandSender> senders) {
        return getSenders(senders);
    }

    /**
     * Get objects for specified players locale.
     *
     * @param players target players
     * @return locale object mapping
     */
    default Map<T, List<Player>> getPlayers(Collection<Player> players) {
        if (players.isEmpty()) {
            return Collections.emptyMap();
        }

        Iterator<Player> i = players.iterator();
        //noinspection ResultOfMethodCallIgnored
        i.hasNext();
        Player first = i.next();
        if (!i.hasNext()) {
            return Collections.singletonMap(get(first), Collections.singletonList(first));
        }

        Map<T, List<Player>> r = new IdentityHashMap<>();
        do {
            Player player = i.next();
            r.computeIfAbsent(get(player), ign -> new ArrayList<>()).add(player);
        } while (i.hasNext());
        return r;
    }

    /**
     * Get objects for specified senders locale.
     *
     * @param senders target senders
     * @return locale object mapping
     */
    default Map<T, List<CommandSender>> getSenders(Collection<? extends CommandSender> senders) {
        if (senders.isEmpty()) {
            return Collections.emptyMap();
        }

        Iterator<? extends CommandSender> i = senders.iterator();
        //noinspection ResultOfMethodCallIgnored
        i.hasNext();
        CommandSender first = i.next();
        if (!i.hasNext()) {
            return Collections.singletonMap(get(first), Collections.singletonList(first));
        }

        Map<T, List<CommandSender>> r = new IdentityHashMap<>();
        do {
            CommandSender sender = i.next();
            r.computeIfAbsent(get(sender), ign -> new ArrayList<>()).add(sender);
        } while (i.hasNext());
        return r;
    }

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
