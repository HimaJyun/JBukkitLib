package jp.jyn.jbukkitlib.util.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * <p>An object pool that creates objects with the first specified number of elements</p>
 * <p>When it gets it when there is no element it will block the thread until it gets an object.</p>
 *
 * @param <E> Element type
 */
public class BlockingPool<E> implements ObjectPool<E> {
    private final BlockingQueue<E> queue;
    private final UnaryOperator<E> reinitializer;

    /**
     * @param max           max size
     * @param supplier      object supplier
     * @param reinitializer reinitializer
     */
    public BlockingPool(int max, Supplier<E> supplier, UnaryOperator<E> reinitializer) {
        this.queue = new ArrayBlockingQueue<>(max);
        this.reinitializer = reinitializer;

        for (int i = 0; i < max; i++) {
            queue.add(reinitializer.apply(supplier.get()));
        }
    }

    /**
     * @param max      max size
     * @param supplier object supplier
     */
    public BlockingPool(int max, Supplier<E> supplier) {
        this(max, supplier, UnaryOperator.identity());
    }

    @Override
    public E get() {
        try {
            return reinitializer.apply(queue.take());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get value from pool with timeout.
     *
     * @param timeout timeout
     * @param unit    timeout unit
     * @return value, or null if the specified waiting time elapses before an element is available
     */
    public E get(long timeout, TimeUnit unit) {
        try {
            E obj = queue.poll(timeout, unit);
            if (obj == null) {
                return null;
            }
            return reinitializer.apply(obj);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(E obj) {
        queue.add(obj);
    }
}
