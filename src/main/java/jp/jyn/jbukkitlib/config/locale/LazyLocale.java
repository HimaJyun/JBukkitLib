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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * BukkitLocale implementation using a multiple locale.
 * This implementation lazy-initialization the object as needed.
 *
 * @param <T> Type
 */
public class LazyLocale<T> implements BukkitLocale<T> {
    private final Function<String, T> initializer;

    private final String defaultLocaleName;
    private final T defaultLocale;
    private final Map<String, T> locales;

    /**
     * BukkitLocale implementation using a multiple locale.
     * This implementation lazy-initialization the object as needed.
     *
     * @param name        default locale name
     * @param locale      default locale object
     * @param initializer object initializer, If returns null, using default locale.
     * @param map         Map supplier, If use a thread-safe Map, it will be thread-safe.
     */
    public LazyLocale(String name, T locale, Function<String, T> initializer, Supplier<Map<String, T>> map) {
        Objects.requireNonNull(initializer);
        Objects.requireNonNull(map);

        this.defaultLocaleName = Objects.requireNonNull(name);
        this.defaultLocale = Objects.requireNonNull(locale, "default locale does not allowed null");

        this.locales = map.get();
        this.locales.put(name, locale);
        this.initializer = initializer.andThen(v -> v == null ? this.defaultLocale : v);
    }

    /**
     * BukkitLocale implementation using a multiple locale.
     * This implementation lazy-initialization the object as needed.
     *
     * @param name        default locale name
     * @param initializer object initializer, If returns null, using default locale.
     * @param map         Map supplier, If use a thread-safe Map, it will be thread-safe.
     */
    public LazyLocale(String name, Function<String, T> initializer, Supplier<Map<String, T>> map) {
        this(name, initializer.apply(name), initializer, map);
    }

    /**
     * BukkitLocale implementation using a multiple locale.
     * This implementation lazy-initialization the object as needed.
     *
     * @param name        default locale name
     * @param locale      default locale object
     * @param initializer object initializer, If returns null, using default locale.
     */
    public LazyLocale(String name, T locale, Function<String, T> initializer) {
        this(name, locale, initializer, HashMap::new);
    }

    /**
     * BukkitLocale implementation using a multiple locale.
     * This implementation lazy-initialization the object as needed.
     *
     * @param name        default locale name
     * @param initializer object initializer, If returns null, using default locale.
     */
    public LazyLocale(String name, Function<String, T> initializer) {
        this(name, initializer, HashMap::new);
    }

    @Override
    public T get(String locale) {
        return locales.computeIfAbsent(locale, initializer);
    }

    @Override
    public T get() {
        return defaultLocale;
    }

    private Map<String, T> registered() {
        // イテレーション処理用に、利用可能な言語として登録した(initializerがちゃんと値を返した)物だけを集める
        // 利用不可能な言語なので(initializerがnullを返したので)、代替としてデフォルトの言語が使われている物は除外する
        // マップを作り直すのでやや負荷が大きいが
        //  - 頻繁に呼び出されるものではない
        //  - そこまで個数が多くない
        // と予想されるため、たぶん大丈夫。

        Map<String, T> v = new HashMap<>();
        v.put(defaultLocaleName, locales.get(defaultLocaleName));
        for (Map.Entry<String, T> e : locales.entrySet()) {
            // default以外でdefaultと同じ物 == initializerがnullを返したのでdefaultを使ってる奴はスキップ
            if (e.getValue() == defaultLocale) { // NOT BUG
                continue;
            }
            v.put(e.getKey(), e.getValue());
        }
        return Collections.unmodifiableMap(v);
    }

    @Override
    public void forEach(BiConsumer<String, ? super T> action) {
        Objects.requireNonNull(action);
        registered().forEach(action);
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        return registered().entrySet();
    }

    @Override
    public Iterator<T> iterator() {
        return registered().values().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        registered().values().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return registered().values().spliterator();
    }

    @Override
    public String toString() {
        return "LazyLocale{" +
            "default='" + defaultLocaleName + '\'' +
            ", locales=" + locales +
            '}';
    }
}
