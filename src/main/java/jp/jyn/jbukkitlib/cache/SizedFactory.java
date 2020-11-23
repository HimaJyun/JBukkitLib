package jp.jyn.jbukkitlib.cache;

import java.util.Collections;
import java.util.Map;

/**
 * Size-specifiable cache
 */
public class SizedFactory implements CacheFactory {
    private final int size;
    private final CacheFactory factory;

    /**
     * <p>Initialize the factory by specifying the size.</p>
     * <p>Infinite if size is less than 0, disable if 0, use LRU of the specified size if greater than 1.</p>
     *
     * @param size Cache size
     */
    public SizedFactory(int size) {
        this.size = size;
        if (size < 0) {
            factory = CacheFactory.INFINITY;
        } else if (size == 0) {
            factory = CacheFactory.DISABLE;
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
