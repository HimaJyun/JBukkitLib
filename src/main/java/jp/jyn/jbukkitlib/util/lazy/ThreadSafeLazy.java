package jp.jyn.jbukkitlib.util.lazy;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Thread-safe Lazy
 *
 * @param <T> Type
 */
public class ThreadSafeLazy<T> implements Lazy<T> {
    private volatile Supplier<T> initializer;
    private T value = null;

    private static final VarHandle VAR_HANDLE;

    static {
        try {
            VAR_HANDLE = MethodHandles.lookup().findVarHandle(ThreadSafeLazy.class, "value", Object.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        // x86では効果が薄い(Release/Acquireに対応する命令がないので)、ARM(M1とか)なら命令があるので伸びるかも
        // https://www.cl.cam.ac.uk/~pes20/cpp/cpp0xmappings.html
        T local = (T) VAR_HANDLE.getAcquire(this); // こうするとパフォーマンスが向上する……らしい
        if (local == null) {
            synchronized (this) {
                local = (T) VAR_HANDLE.getAcquire(this);
                if (local == null) {
                    local = Objects.requireNonNull(initializer.get(), "ThreadSafeLazy does not allow null");
                    VAR_HANDLE.setRelease(this, local);
                    initializer = null; // もう要らない
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
