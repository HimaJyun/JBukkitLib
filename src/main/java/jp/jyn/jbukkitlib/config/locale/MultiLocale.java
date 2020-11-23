package jp.jyn.jbukkitlib.config.locale;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * BukkitLocale implementation using a multiple locale.
 * All objects must be pre-initialized.
 *
 * @param <T> Type
 */
public class MultiLocale<T> implements BukkitLocale<T> {
    private final T defaultLocale;
    private final Map<String, T> locales; // 不変かつメモリキャッシュ的に安全な状態(=final)のHashMapに対するgetはスレッドセーフ

    /**
     * BukkitLocale implementation using a single locale.
     * All objects must be pre-initialized.
     *
     * @param name    default locale name
     * @param locale  default locale object
     * @param locales locales map
     */
    public MultiLocale(String name, T locale, Map<String, T> locales) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(locale, "default locale does not allowed null");
        Objects.requireNonNull(locales);

        Map<String, T> m = new HashMap<>(locales);
        m.put(name, locale);

        this.locales = Collections.unmodifiableMap(m);
        this.defaultLocale = locale;
    }

    /**
     * BukkitLocale implementation using a single locale.
     * All objects must be pre-initialized.
     *
     * @param name    default locale name
     * @param locales locales map, need include default locale.
     */
    public MultiLocale(String name, Map<String, T> locales) {
        this(name, locales.get(name), locales);
    }

    @Override
    public T get(String locale) {
        return locales.getOrDefault(locale, defaultLocale);
    }

    @Override
    public T get() {
        return defaultLocale;
    }

    @Override
    public void forEach(BiConsumer<String, ? super T> action) {
        Objects.requireNonNull(action);
        locales.forEach(action);
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        return locales.entrySet();
    }

    @Override
    public Iterator<T> iterator() {
        return locales.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        locales.values().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return locales.values().spliterator();
    }

    @Override
    public String toString() {
        return "MultiLocale{" +
            "default=" + defaultLocale +
            ", locales=" + locales +
            '}';
    }
}
