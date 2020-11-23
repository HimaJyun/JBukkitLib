package jp.jyn.jbukkitlib.config.locale;

import org.bukkit.command.CommandSender;
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

/**
 * BukkitLocale implementation using a single locale.
 * This can be used for settings that use a single locale regardless of the player's locale.
 *
 * @param <T> Type
 */
public class SingleLocale<T> implements BukkitLocale<T> {
    private final String name;
    private final T locale;

    /**
     * BukkitLocale implementation using a single locale.
     * This can be used for settings that use a single locale regardless of the player's locale.
     *
     * @param name   locale name
     * @param locale locale object
     */
    private SingleLocale(String name, T locale) {
        this.name = Objects.requireNonNull(name);
        this.locale = Objects.requireNonNull(locale, "default locale does not allowed null");
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
    public T get(CommandSender sender) {
        return this.locale;
    }

    @Override
    public T get() {
        return locale;
    }

    @Override
    public void forEach(BiConsumer<String, ? super T> action) {
        Objects.requireNonNull(action);
        action.accept(name, locale);
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        return Collections.singleton(new AbstractMap.SimpleImmutableEntry<>(name, locale));
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

    @Override
    public String toString() {
        return "SingleLocale{" +
            "name='" + name + '\'' +
            ", locale=" + locale +
            '}';
    }
}
