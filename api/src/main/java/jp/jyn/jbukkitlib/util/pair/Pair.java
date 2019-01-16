package jp.jyn.jbukkitlib.util.pair;

import java.util.Map;

public interface Pair<K, V> {
    /**
     * Get key
     *
     * @return Key
     */
    K getKey();

    /**
     * Get value
     *
     * @return value
     */
    V getValue();

    /**
     * Convert to Map.Entry
     *
     * @return map entry
     */
    Map.Entry<K, V> toMapEntry();

    /**
     * Add key/value to Map
     *
     * @param map target Map
     * @return Return value of {@link java.util.Map#put(Object, Object)}
     */
    V put(Map<K, V> map);
}
