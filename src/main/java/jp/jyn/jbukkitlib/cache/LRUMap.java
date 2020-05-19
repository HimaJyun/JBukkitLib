package jp.jyn.jbukkitlib.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>LRU Map</p>
 * <p>Note: Non thread-safe</p>
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LRUMap(int maxSize) {
        super((maxSize * 4) / 3, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
