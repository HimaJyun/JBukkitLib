package jp.jyn.jbukkitlib.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Lazy Initializer
 *
 * @param <E> the type of elements held in this lazy
 */
@FunctionalInterface
public interface Lazy<E> {
    /**
     * Get Object
     *
     * @return object
     */
    E get();

    /**
     * not Lazy
     *
     * @param <E> the type of elements held in this lazy
     */
    class Computed<E> implements Lazy<E> {
        private final E value;

        public Computed(E value) {
            this.value = value;
        }

        @Override
        public E get() {
            return value;
        }
    }

    /**
     * Non thread-safe Lazy
     *
     * @param <E> the type of elements held in this lazy
     */
    class Simple<E> implements Lazy<E> {
        private Supplier<E> initializer;
        private E value = null;

        public Simple(Supplier<E> initializer) {
            this.initializer = Objects.requireNonNull(initializer);
        }

        public E get() {
            if (value == null) {
                value = initializer.get();
                initializer = null;
            }

            return value;
        }
    }

    /**
     * Thread-safe Lazy
     *
     * @param <E> the type of elements held in this lazy
     */
    class ThreadSafe<E> implements Lazy<E> {
        private Supplier<E> initializer;
        private volatile E value = null;

        public ThreadSafe(Supplier<E> initializer) {
            this.initializer = Objects.requireNonNull(initializer);
        }

        public E get() {
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
    }
}
