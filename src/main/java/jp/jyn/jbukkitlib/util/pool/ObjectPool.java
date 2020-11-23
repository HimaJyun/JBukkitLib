package jp.jyn.jbukkitlib.util.pool;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Object pool
 *
 * @param <E> Element type
 */
public interface ObjectPool<E> {
    /**
     * Get value from pool.
     *
     * @return value
     */
    E get();

    /**
     * Return value to pool.
     *
     * @param obj object
     */
    void put(E obj);

    /**
     * Use value and auto return.
     *
     * @param consumer value consumer
     */
    default void use(Consumer<E> consumer) {
        E v = get();
        consumer.accept(v);
        put(v);
    }

    /**
     * Use value and auto return.
     *
     * @param function value function
     * @param <R>      Return type
     * @return function return value
     */
    default <R> R use(Function<E, R> function) {
        E v = get();
        R ret = function.apply(v);
        put(v);
        return ret;
    }
}
