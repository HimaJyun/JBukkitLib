package jp.jyn.jbukkitlib.config.locale;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiLocale<T> implements BukkitLocale<T> {
    private final String defaultLocale;
    private final Function<String, T> initializer;

    private final Map<String, T> locale;

    /**
     * Use multiple locales with any map.
     *
     * @param defaultLocale default locale
     * @param initializer   object initializer (argument: locale)
     * @param map           map supplier (If use a thread-safe Map, it will be thread-safe.)
     */
    public MultiLocale(String defaultLocale, Function<String, T> initializer, Supplier<Map<String, T>> map) {
        this.defaultLocale = defaultLocale;
        this.initializer = initializer;
        this.locale = map.get();
    }

    /**
     * Use multiple locales.
     *
     * @param defaultLocale default locale
     * @param initializer   object initializer (argument: locale)
     */
    public MultiLocale(String defaultLocale, Function<String, T> initializer) {
        this(defaultLocale, initializer, HashMap::new);
    }

    @Override
    public T get(String locale) {
        return this.locale.computeIfAbsent(locale, initializer);
    }

    @Override
    public T get(Player player) {
        return locale.computeIfAbsent(player.getLocale(), initializer);
    }

    @Override
    public T get() {
        return locale.computeIfAbsent(defaultLocale, initializer);
    }

    @Override
    public void load(String locale) {
        this.locale.computeIfAbsent(locale, initializer);
    }

    @Override
    public void load(String... locales) {
        for (String s : locales) {
            locale.computeIfAbsent(s, initializer);
        }
    }

    @Override
    public void load(Iterable<String> locales) {
        for (String s : locales) {
            locale.computeIfAbsent(s, initializer);
        }
    }

    @Override
    public void forEach(BiConsumer<String, ? super T> action) {
        Objects.requireNonNull(action);
        locale.forEach(action);
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        return locale.entrySet();
    }

    @Override
    public Iterator<T> iterator() {
        return locale.values().iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return locale.values().spliterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        locale.values().forEach(action);
    }
}
