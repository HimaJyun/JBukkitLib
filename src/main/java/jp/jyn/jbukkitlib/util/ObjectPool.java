package jp.jyn.jbukkitlib.util;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Object pool
 *
 * @param <E> Element type
 */
public interface ObjectPool<E> {
    /**
     * Get the value from the pool.
     *
     * @return value
     */
    E get();

    /**
     * Return the value to the pool.
     *
     * @param obj object
     */
    void put(E obj);

    /**
     * <p>An object pool that creates objects with the first specified number of elements</p>
     * <p>When it gets it when there is no element it will block the thread until it gets an object.</p>
     *
     * @param <E> Element type
     */
    class Blocking<E> implements ObjectPool<E> {
        private final BlockingQueue<E> queue;
        private final UnaryOperator<E> initializer;

        /**
         * @param max         max size
         * @param supplier    object supplier
         * @param initializer initializer
         */
        public Blocking(int max, Supplier<E> supplier, UnaryOperator<E> initializer) {
            this.queue = new ArrayBlockingQueue<>(max);
            this.initializer = initializer;

            for (int i = 0; i < max; i++) {
                queue.add(initializer.apply(supplier.get()));
            }
        }

        /**
         * @param max      max size
         * @param supplier object supplier
         */
        public Blocking(int max, Supplier<E> supplier) {
            this(max, supplier, UnaryOperator.identity());
        }

        @Override
        public E get() {
            try {
                return initializer.apply(queue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public E get(long timeout, TimeUnit unit) {
            try {
                E obj = queue.poll(timeout, unit);
                if (obj == null) {
                    return null;
                }
                return initializer.apply(obj);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void put(E obj) {
            queue.add(obj);
        }
    }

    /**
     * <p>Object pool to cache objects</p>
     * <p>When it gets when there is no object, it creates a new object.</p>
     *
     * @param <E> Element type
     */
    class Cached<E> implements ObjectPool<E> {
        private final Queue<E> queue = new ConcurrentLinkedQueue<>();
        private final Supplier<E> supplier;
        private final UnaryOperator<E> initializer;

        /**
         * @param supplier    object supplier
         * @param initializer initializer
         */
        public Cached(Supplier<E> supplier, UnaryOperator<E> initializer) {
            this.supplier = supplier;
            this.initializer = initializer;
        }

        /**
         * @param supplier object supplier
         */
        public Cached(Supplier<E> supplier) {
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
}
