package jp.jyn.jbukkitlib.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Lazy Initializer
 *
 * @param <T> This Lazy type
 */
@FunctionalInterface
public interface Lazy<T> extends Supplier<T> {
    /**
     * Get Object
     *
     * @return object
     */
    T get();

    /**
     * Calculated Lazy
     *
     * @param value Lazy value
     * @param <T>   This Lazy type
     * @return Lazy.Computed
     */
    static <T> Lazy<T> of(T value) {
        return Lazy.Computed.of(value);
    }

    /**
     * Non thread-safe Lazy
     *
     * @param initializer Lazy initializer
     * @param <T>         This Lazy type
     * @return Lazy.Simple
     */
    static <T> Lazy<T> of(Supplier<T> initializer) {
        return Lazy.Simple.of(initializer);
    }

    /**
     * Calculated Lazy
     *
     * @param <T> This Lazy type
     */
    class Computed<T> implements Lazy<T> {
        private final T value;

        public Computed(T value) {
            this.value = value;
        }

        /**
         * Calculated Lazy
         *
         * @param value Lazy value
         * @param <T>   This Lazy type
         * @return Lazy.Computed
         */
        public static <T> Computed<T> of(T value) {
            return new Computed<>(value);
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public String toString() {
            return "Lazy.Computed[" + value + "]";
        }
    }

    /**
     * Non thread-safe Lazy
     *
     * @param <T> This Lazy type
     */
    class Simple<T> implements Lazy<T> {
        private Supplier<T> initializer;
        private T value = null;

        public Simple(Supplier<T> initializer) {
            this.initializer = Objects.requireNonNull(initializer);
        }

        /**
         * Non thread-safe Lazy
         *
         * @param initializer Lazy initializer
         * @param <T>         This Lazy type
         * @return Lazy.Simple
         */
        public static <T> Simple<T> of(Supplier<T> initializer) {
            return new Simple<>(initializer);
        }

        public T get() {
            if (value == null) {
                value = initializer.get();
                initializer = null;
            }

            return value;
        }

        @Override
        public String toString() {
            return "Lazy.Simple[" + (value == null ? "not initialized, " + initializer : value) + "]";
        }
    }

    /**
     * Thread-safe Lazy
     *
     * @param <T> This Lazy type
     */
    class ThreadSafe<T> implements Lazy<T> {
        private Supplier<T> initializer;
        private volatile T value = null;

        public ThreadSafe(Supplier<T> initializer) {
            this.initializer = Objects.requireNonNull(initializer);
        }

        /**
         * Thread-safe Lazy
         *
         * @param initializer Lazy initializer
         * @param <T>         This Lazy type
         * @return Lazy.ThreadSafe
         */
        public static <T> ThreadSafe<T> of(Supplier<T> initializer) {
            return new ThreadSafe<>(initializer);
        }

        public T get() {
            if (value == null) {
                synchronized (this) {
                    if (value == null) {
                        value = initializer.get();
                        initializer = null;
                    }
                }
            }

            return value;
        }

        @Override
        public String toString() {
            return "Lazy.ThreadSafe[" + (value == null ? "not initialized, " + initializer : value) + "]";
        }
    }
}
