package jp.jyn.jbukkitlib.util.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CachedObjectPool<E> implements ObjectPool<E> {
    private final Queue<E> queue = new ConcurrentLinkedQueue<>();
    private final Supplier<E> supplier;
    private final UnaryOperator<E> initializer;

    public CachedObjectPool(Supplier<E> supplier, UnaryOperator<E> initializer) {
        this.supplier = supplier;
        this.initializer = initializer;
    }

    public CachedObjectPool(Supplier<E> supplier) {
        this(supplier, UnaryOperator.identity());
    }

    @Override
    public E get() {
        E obj = queue.poll();
        if (obj == null) {
            obj = supplier.get();
        }
        return initializer.apply(obj);
    }

    @Override
    public void put(E obj) {
        queue.add(obj);
    }
}
