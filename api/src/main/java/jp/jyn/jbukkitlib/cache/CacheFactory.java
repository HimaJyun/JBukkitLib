package jp.jyn.jbukkitlib.cache;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface CacheFactory {
    // TODO: ThreadSafeCacheFactory
    <K, V> Map<K, V> create();

    /**
     * Non thread-safe cache
     */
    class Simple implements CacheFactory {
        public final static CacheFactory DISABLE = ZeroMap::getInstance;
        public final static CacheFactory INFINITY = HashMap::new;

        private final int size;
        private final CacheFactory factory;

        public Simple(int size) {
            this.size = size;
            if (size < 0) {
                factory = INFINITY;
            } else if (size == 0) {
                factory = DISABLE;
            } else {
                factory = this::lru;
            }
        }

        @Override
        public <K, V> Map<K, V> create() {
            return factory.create();
        }

        private <K, V> Map<K, V> lru() {
            return new LRUMap<>(size);
        }
    }
}
