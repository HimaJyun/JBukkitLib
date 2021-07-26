package jp.jyn.jbukkitlib.config.locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
        return sender instanceof Player p ? get(p) : get();
    }

    /**
     * Get object for default locale.
     *
     * @return default locale
     */
    T get();

    @SafeVarargs
    private <E> Map<T, List<E>> get(Function<E, T> getter, E... senders) {
        return switch (senders.length) {
            case 0 -> Collections.emptyMap();
            case 1 -> Collections.singletonMap(getter.apply(senders[0]), Collections.singletonList(senders[0]));
            default -> {
                Map<T, List<E>> r = new IdentityHashMap<>();
                for (E sender : senders) {
                    T obj = getter.apply(sender);
                    r.computeIfAbsent(obj, ign -> new ArrayList<>()).add(sender);
                }
                yield r;
            }
        };
    }

    /**
     * Get objects for specified players locale.
     *
     * @param players target players
     * @return locale object mapping
     */
    default Map<T, List<Player>> get(Player... players) {
        return get(this::get, players);
    }

    /**
     * Get objects for specified senders locale.
     *
     * @param senders target senders
     * @return locale object mapping
     */
    default Map<T, List<CommandSender>> get(CommandSender... senders) {
        return get(this::get, senders);
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

    private <E> Map<T, List<E>> getCollections(Function<E, T> getter, Collection<E> collection) {
        if (collection.isEmpty()) {
            return Collections.emptyMap();
        }

        var i = collection.iterator();
        E e = i.next();
        if (!i.hasNext()) {
            return Collections.singletonMap(getter.apply(e), Collections.singletonList(e));
        }

        Map<T, List<E>> r = new IdentityHashMap<>();
        r.computeIfAbsent(getter.apply(e), ign -> new ArrayList<>()).add(e);
        do {
            e = i.next();
            r.computeIfAbsent(getter.apply(e), ign -> new ArrayList<>()).add(e);
        } while (i.hasNext());
        return r;
    }

    /**
     * Get objects for specified players locale.
     *
     * @param players target players
     * @return locale object mapping
     */
    default Map<T, List<Player>> getPlayers(Collection<Player> players) {
        return getCollections(this::get, players);
    }

    /**
     * Get objects for specified senders locale.
     *
     * @param senders target senders
     * @return locale object mapping
     */
    @SuppressWarnings("unchecked")
    default Map<T, List<CommandSender>> getSenders(Collection<? extends CommandSender> senders) {
        return getCollections(this::get, (Collection<CommandSender>) senders);
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
