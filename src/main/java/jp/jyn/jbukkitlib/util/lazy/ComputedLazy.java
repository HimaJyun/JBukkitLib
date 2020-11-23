package jp.jyn.jbukkitlib.util.lazy;

/**
 * Computed Lazy
 *
 * @param <T> Type
 */
public class ComputedLazy<T> implements Lazy<T> {
    private final T value;

    /**
     * Computed Lazy
     *
     * @param value Lazy value
     */
    public ComputedLazy(T value) {
        this.value = value;
    }

    /**
     * Computed Lazy
     *
     * @param value Lazy value
     * @param <T>   Type
     * @return ComputedLazy
     */
    public static <T> ComputedLazy<T> of(T value) {
        return new ComputedLazy<>(value);
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public String toString() {
        return "ComputedLazy[" + value + "]";
    }
}
