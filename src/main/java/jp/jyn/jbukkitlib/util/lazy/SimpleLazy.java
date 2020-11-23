package jp.jyn.jbukkitlib.util.lazy;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Non thread-safe Lazy
 *
 * @param <T> Type
 */
public class SimpleLazy<T> implements Lazy<T> {
    private Supplier<T> initializer;
    private T value = null;

    /**
     * Non thread-safe Lazy
     *
     * @param initializer Lazy initializer
     */
    public SimpleLazy(Supplier<T> initializer) {
        this.initializer = Objects.requireNonNull(initializer);
    }

    /**
     * Non thread-safe Lazy
     *
     * @param initializer Lazy initializer
     * @param <T>         Type
     * @return SimpleLazy
     */
    public static <T> SimpleLazy<T> of(Supplier<T> initializer) {
        return new SimpleLazy<>(initializer);
    }

    @Override
    public T get() {
        // valueで判定するとinitializer.get()がnullを返した時に
        //  - 何度もinitializer.get()が呼ばれる(副作用のあるSupplierを使えない)
        //  - NPEになる
        // のどちらか(実装次第、今の実装なら後者)なので、initializerがnullになっているか = 1度でも実行しているかをフラグに使う
        if (initializer != null) {
            value = initializer.get();
            initializer = null;
        }

        return value;
    }

    @Override
    public String toString() {
        return "SimpleLazy[" + (initializer != null ? "not initialized, " + initializer : value) + "]";
    }
}
