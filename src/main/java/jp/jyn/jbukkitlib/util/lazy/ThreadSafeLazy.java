package jp.jyn.jbukkitlib.util.lazy;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Thread-safe Lazy
 *
 * @param <T> Type
 */
public class ThreadSafeLazy<T> implements Lazy<T> {
    private volatile Supplier<T> initializer;
    private volatile T value = null;

    /**
     * Thread-safe Lazy
     *
     * @param initializer Lazy initializer. This Supplier does not allow null to be return.
     */
    public ThreadSafeLazy(Supplier<T> initializer) {
        this.initializer = Objects.requireNonNull(initializer);
    }

    /**
     * Thread-safe Lazy
     *
     * @param initializer Lazy initializer. This Supplier does not allow null to be return.
     * @param <T>         Type
     * @return ThreadSafeLazy
     */
    public static <T> ThreadSafeLazy<T> of(Supplier<T> initializer) {
        return new ThreadSafeLazy<>(initializer);
    }

    @Override
    public T get() {
        T local = value; // こうするとパフォーマンスが向上する……らしい
        if (local == null) {
            synchronized (this) {
                local = value;
                if (local == null) {
                    value = local = Objects.requireNonNull(initializer.get(), "ThreadSafeLazy does not allow null");
                    initializer = null;
                }
            }
        }

        return local;
    }

    @Override
    public String toString() {
        return "ThreadSafeLazy[" + (value == null ? "not initialized, " + initializer : value) + "]";
    }
}
