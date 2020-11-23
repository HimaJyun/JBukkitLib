package jp.jyn.jbukkitlib.util.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * <p>Object pool to cache objects</p>
 * <p>When it gets when there is no object, it creates a new object.</p>
 *
 * @param <E> Element type
 */
public class CachedPool<E> implements ObjectPool<E> {
    private final Queue<E> queue = new ConcurrentLinkedQueue<>();
    private final Supplier<E> supplier;
    private final UnaryOperator<E> reinitializer;

    /**
     * @param supplier      object supplier
     * @param reinitializer reinitializer
     */
    public CachedPool(Supplier<E> supplier, UnaryOperator<E> reinitializer) {
        this.supplier = supplier;
        this.reinitializer = reinitializer;
    }

    /**
     * @param supplier object supplier
     */
    public CachedPool(Supplier<E> supplier) {
        this(supplier, UnaryOperator.identity());
    }

    @Override
    public E get() {
        E obj = queue.poll();
        if (obj == null) {
            obj = supplier.get();
        }
        return reinitializer.apply(obj);
    }

    @Override
    public void put(E obj) {
        queue.add(obj);
    }
}
