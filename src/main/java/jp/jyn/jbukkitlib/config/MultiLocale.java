package jp.jyn.jbukkitlib.config;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MultiLocale<T> {
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
    void forEach(BiConsumer<String, T> action);

    /**
     * Use multiple locales with any map.
     *
     * @param defaultLocale default locale
     * @param initializer   object initializer (argument: locale)
     * @param map           map supplier (If use a thread-safe Map, it will be thread-safe.)
     * @param <T>           Type
     * @return MultiLocale object
     */
    static <T> MultiLocale<T> init(String defaultLocale, Function<String, T> initializer, Supplier<Map<String, T>> map) {
        return new Simple<>(defaultLocale, initializer, map);
    }

    /**
     * Use multiple locales.
     *
     * @param defaultLocale default locale
     * @param initializer   object initializer (argument: locale)
     * @param <T>           Type
     * @return MultiLocale object
     */
    static <T> MultiLocale<T> init(String defaultLocale, Function<String, T> initializer) {
        return new Simple<>(defaultLocale, initializer);
    }

    /**
     * Use single locale.
     *
     * @param defaultLocale default locale
     * @param locale        initialized object
     * @param <T>           Type
     * @return MultiLocale object
     */
    static <T> MultiLocale<T> fixed(String defaultLocale, T locale) {
        return new Fixed<>(defaultLocale, locale);
    }

    /**
     * Use single locale.
     *
     * @param defaultLocale default locale
     * @param initializer   object initializer (argument: locale)
     * @param <T>           Type
     * @return MultiLocale object
     */
    static <T> MultiLocale<T> fixed(String defaultLocale, Function<String, T> initializer) {
        return new Fixed<>(defaultLocale, initializer);
    }

    class Simple<T> implements MultiLocale<T> {
        private final Map<String, T> locale;

        private final String defaultLocale;
        private final Function<String, T> initializer;

        /**
         * Use multiple locales with any map.
         *
         * @param defaultLocale default locale
         * @param initializer   object initializer (argument: locale)
         * @param map           map supplier (If use a thread-safe Map, it will be thread-safe.)
         */
        public Simple(String defaultLocale, Function<String, T> initializer, Supplier<Map<String, T>> map) {
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
        public Simple(String defaultLocale, Function<String, T> initializer) {
            this(defaultLocale, initializer, HashMap::new);
        }

        /**
         * Use multiple locales with any map.
         *
         * @param defaultLocale default locale
         * @param initializer   object initializer (argument: locale)
         * @param map           map supplier (If use a thread-safe Map, it will be thread-safe.)
         * @param <T>           Type
         * @return MultiLocale object
         */
        public static <T> Simple<T> init(String defaultLocale, Function<String, T> initializer, Supplier<Map<String, T>> map) {
            return new Simple<>(defaultLocale, initializer, map);
        }

        /**
         * Use multiple locales.
         *
         * @param defaultLocale default locale
         * @param initializer   object initializer (argument: locale)
         * @param <T>           Type
         * @return MultiLocale object
         */
        public static <T> Simple<T> init(String defaultLocale, Function<String, T> initializer) {
            return new Simple<>(defaultLocale, initializer);
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
        public void forEach(BiConsumer<String, T> action) {
            locale.forEach(action);
        }
    }

    class Fixed<T> implements MultiLocale<T> {
        private final T locale;
        private final String defaultLocale;

        /**
         * Use single locale.
         *
         * @param defaultLocale default locale
         * @param locale        initialized object
         */
        public Fixed(String defaultLocale, T locale) {
            this.defaultLocale = defaultLocale;
            this.locale = locale;
        }

        /**
         * Use single locale.
         *
         * @param defaultLocale default locale
         * @param initializer   object initializer (argument: locale)
         */
        public Fixed(String defaultLocale, Function<String, T> initializer) {
            this(defaultLocale, initializer.apply(defaultLocale));
        }

        /**
         * Use single locale.
         *
         * @param defaultLocale default locale
         * @param locale        initialized object
         * @param <T>           Type
         * @return MultiLocale object
         */
        public static <T> Fixed<T> init(String defaultLocale, T locale) {
            return new Fixed<>(defaultLocale, locale);
        }

        /**
         * Use single locale.
         *
         * @param defaultLocale default locale
         * @param initializer   object initializer (argument: locale)
         * @param <T>           Type
         * @return MultiLocale object
         */
        public static <T> Fixed<T> init(String defaultLocale, Function<String, T> initializer) {
            return new Fixed<>(defaultLocale, initializer);
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
        public void forEach(BiConsumer<String, T> action) {
            action.accept(defaultLocale, locale);
        }
    }
}
