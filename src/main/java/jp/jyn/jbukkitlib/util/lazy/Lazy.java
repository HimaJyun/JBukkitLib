package jp.jyn.jbukkitlib.util.lazy;

import java.util.function.Supplier;

/**
 * Lazy Initializer
 *
 * @param <T> Type
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
     * @param <T>   Type
     * @return ComputedLazy
     */
    static <T> Lazy<T> of(T value) {
        return new ComputedLazy<>(value);
    }

    /**
     * Non thread-safe Lazy. If you need thread-safety, use {@link Lazy#threadSafe(Supplier)}
     *
     * @param initializer Lazy initializer
     * @param <T>         Type
     * @return SimpleLazy
     */
    static <T> Lazy<T> of(Supplier<T> initializer) {
        return new SimpleLazy<>(initializer);
    }

    /**
     * Thread-Safe Lazy.
     *
     * @param initializer Lazy initializer. This Supplier does not allow null to be return.
     * @param <T>         Type
     * @return ThreadSafeLazy
     */
    static <T> Lazy<T> threadSafe(Supplier<T> initializer) {
        return new ThreadSafeLazy<>(initializer);
    }
}
