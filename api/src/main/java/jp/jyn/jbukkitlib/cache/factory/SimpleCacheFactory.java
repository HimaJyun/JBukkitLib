package jp.jyn.jbukkitlib.cache.factory;

import jp.jyn.jbukkitlib.cache.LRUMap;
import jp.jyn.jbukkitlib.cache.NoOpMap;

import java.util.HashMap;
import java.util.Map;

public class SimpleCacheFactory implements CacheFactory {
    public final static CacheFactory DISABLE = NoOpMap::getInstance;
    public final static CacheFactory INFINITY = HashMap::new;

    private final int size;

    public SimpleCacheFactory(int size) {
        this.size = size;
    }

    @Override
    public <K, V> Map<K, V> create() {
        if (size < 0) {
            return INFINITY.create();
        } else if (size == 0) {
            return DISABLE.create();
        } else {
            return new LRUMap<>(size);
        }
    }
}
