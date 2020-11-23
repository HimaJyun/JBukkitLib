package jp.jyn.jbukkitlib.config.locale;

import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class SingleLocale<T> implements BukkitLocale<T> {
    private final T locale;
    private final String defaultLocale;

    /**
     * Use single locale.
     *
     * @param defaultLocale default locale
     * @param locale        initialized object
     */
    public SingleLocale(String defaultLocale, T locale) {
        this.defaultLocale = defaultLocale;
        this.locale = locale;
    }

    /**
     * Use single locale.
     *
     * @param defaultLocale default locale
     * @param initializer   object initializer (argument: locale)
     */
    public SingleLocale(String defaultLocale, Function<String, T> initializer) {
        this(defaultLocale, initializer.apply(defaultLocale));
    }

    @Override
    public T get(Player player) {
        return locale;
    }

    @Override
    public T get(String locale) {
        return this.locale;
    }

    @Override
    public T get() {
        return locale;
    }

    @Override
    public void load(String locale) { }

    @Override
    public void load(String... locale) { }

    @Override
    public void load(Iterable<String> locale) { }

    @Override
    public void forEach(BiConsumer<String, ? super T> action) {
        Objects.requireNonNull(action);
        action.accept(defaultLocale, locale);
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        return Collections.singleton(new AbstractMap.SimpleImmutableEntry<>(defaultLocale, locale));
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.singletonList(locale).iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return Collections.singletonList(locale).spliterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        action.accept(locale);
    }
}
