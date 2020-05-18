package jp.jyn.jbukkitlib.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface CacheFactory {
    /**
     * Create cache non thread-safe
     *
     * @param <K> Type of key
     * @param <V> Type of value
     * @return Map
     */
    default <K, V> Map<K, V> create() {
        return this.create(false);
    }

    /**
     * Creates a cache that specified concurrency.
     *
     * @param concurrency True if it needs to be Thread-safe
     * @param <K>         Type of key
     * @param <V>         Type of value
     * @return Map
     * @throws UnsupportedOperationException If the specified concurrency is not supported
     */
    <K, V> Map<K, V> create(boolean concurrency) throws UnsupportedOperationException;

    /**
     * Size-specifiable cache
     */
    class Sized implements CacheFactory {
        /**
         * Disable (do nothing) cache
         */
        public final static CacheFactory DISABLE = new CacheFactory() {
            @Override
            public <K, V> Map<K, V> create(boolean concurrency) throws UnsupportedOperationException {
                return NoOpMap.getInstance();
            }
        };
        /**
         * Infinite size cache
         */
        public final static CacheFactory INFINITY = new CacheFactory() {
            @Override
            public <K, V> Map<K, V> create(boolean concurrency) throws UnsupportedOperationException {
                return concurrency ? new ConcurrentHashMap<>() : new HashMap<>();
            }
        };

        private final int size;
        private final CacheFactory factory;

        /**
         * <p>Initialize the factory by specifying the size.</p>
         * <p>Infinite if size is less than 0, disable if 0, use LRU of the specified size if greater than 1.</p>
         *
         * @param size Cache size
         */
        public Sized(int size) {
            this.size = size;
            if (size < 0) {
                factory = Sized.INFINITY;
            } else if (size == 0) {
                factory = Sized.DISABLE;
            } else {
                factory = this::lru;
            }
        }

        private <K, V> Map<K, V> lru(boolean concurrency) {
            if (concurrency) {
                // TODO: More efficient implementation
                return Collections.synchronizedMap(new LRUMap<>(size));
            } else {
                return new LRUMap<>(size);
            }
        }

        @Override
        public <K, V> Map<K, V> create(boolean concurrency) throws UnsupportedOperationException {
            return factory.create(concurrency);
        }
    }
}
