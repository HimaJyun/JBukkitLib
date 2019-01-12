package jp.jyn.jbukkitlib.cache.factory;

import jp.jyn.jbukkitlib.cache.NoOpMap;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface CacheFactory {
    // TODO: ThreadSafeCacheFactory
    CacheFactory DISABLE = NoOpMap::getInstance;
    CacheFactory INFINITY = HashMap::new;

    <K, V> Map<K, V> create();
}
