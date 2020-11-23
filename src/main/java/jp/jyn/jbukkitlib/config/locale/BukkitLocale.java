package jp.jyn.jbukkitlib.config.locale;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public interface BukkitLocale<T> extends Iterable<T> {
    /**
     * Gets the object for the specified locale.
     *
     * @param locale target locale
     * @return An object in the specified locale. Invoke the initializer if it has not been initialized.
     */
    T get(String locale);

    /**
     * Gets the object in the specified player's locale.
     *
     * @param player target player
     * @return An object in the specified locale. Invoke the initializer if it has not been initialized.
     */
    T get(Player player);

    /**
     * Gets the object in the default locale.
     *
     * @return An object in the specified locale. Invoke the initializer if it has not been initialized.
     */
    T get();

    /**
     * Invoke the initializer if the object in the specified locale has not been initialized.
     *
     * @param locale target locale
     */
    void load(String locale);

    /**
     * Invoke the initializer if the object in the specified locale has not been initialized.
     *
     * @param locale target locales
     */
    void load(String... locale);

    /**
     * Invoke the initializer if the object in the specified locale has not been initialized.
     *
     * @param locale target locales
     */
    void load(Iterable<String> locale);

    /**
     * Iterates over all initialized locales.
     *
     * @param action action
     */
    void forEach(BiConsumer<String, ? super T> action);

    /**
     * Return {@link Set} of the all initialized locales.
     *
     * @return initialized locale.
     */
    Set<Map.Entry<String, T>> entrySet();
}
