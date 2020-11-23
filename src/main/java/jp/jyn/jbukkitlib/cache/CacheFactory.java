package jp.jyn.jbukkitlib.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface // たぶん無理
public interface CacheFactory {
    /**
     * Create cache non thread-safe
     *
     * @param <K> Key type
     * @param <V> Value type
     * @return Map
     */
    default <K, V> Map<K, V> create() {
        return this.create(false);
    }

    /**
     * Create cache with specified concurrency.
     *
     * @param concurrency True if need thread-safety
     * @param <K>         Key type
     * @param <V>         Value type
     * @return Map
     * @throws UnsupportedOperationException If the specified concurrency is not supported
     */
    <K, V> Map<K, V> create(boolean concurrency) throws UnsupportedOperationException;

    /**
     * Infinite size cache
     */
    CacheFactory INFINITY = new CacheFactory() {
        @Override
        public <K, V> Map<K, V> create(boolean concurrency) throws UnsupportedOperationException {
            return concurrency ? new ConcurrentHashMap<>() : new HashMap<>();
        }
    };

    /**
     * Disable (no-op) cache
     */
    CacheFactory DISABLE = new CacheFactory() {
        @Override
        public <K, V> Map<K, V> create(boolean concurrency) throws UnsupportedOperationException {
            return NoOpMap.getInstance();
        }
    };
}
