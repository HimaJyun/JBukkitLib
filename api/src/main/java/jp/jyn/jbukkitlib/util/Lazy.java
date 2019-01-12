package jp.jyn.jbukkitlib.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>Lazy Initializer</p>
 * <p>Note: Not thread safe</p>
 *
 * @param <T> Type
 */
public class Lazy<T> {
    private T value = null;

    private Supplier<T> initializer;

    public Lazy(Supplier<T> initializer) throws NullPointerException {
        this.initializer = Objects.requireNonNull(initializer);
    }

    public T get() {
        if (value == null) {
            value = initializer.get();
            initializer = null;
        }

        return value;
    }
}
