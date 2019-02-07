package jp.jyn.jbukkitlib.util;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A small utility to help initialize the Map
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class MapBuilder<K, V> {
    /**
     * Initialize map with lambda.
     * <pre>
     * {@code
     * private final Map<String, String> map = MapBuilder.initMap(new HashMap<>(),m -> {
     *     m.put("key","value");
     *     m.put("lambda","init");
     * });
     * }
     * </pre>
     *
     * @param map         Map
     * @param initializer Map initializer
     * @param <K>         Key type
     * @param <V>         Value type
     * @return Initialized map
     */
    public static <K, V> Map<K, V> initMap(Map<K, V> map, Consumer<Map<K, V>> initializer) {
        initializer.accept(map);
        return map;
    }

    /**
     * Initialize unmodifiable map with lambda.
     * <pre>
     * {@code
     * private final Map<String, String> map = MapBuilder.initUnmodifiableMap(new HashMap<>(),m -> {
     *     m.put("key","value");
     *     m.put("lambda","init");
     * });
     * }
     * </pre>
     *
     * @param map         Map
     * @param initializer Map initializer
     * @param <K>         Key type
     * @param <V>         Value type
     * @return Initialized unmodifiable map
     */
    public static <K, V> Map<K, V> initUnmodifiableMap(Map<K, V> map, Consumer<Map<K, V>> initializer) {
        initializer.accept(map);
        return Collections.unmodifiableMap(map);
    }

    private final Map<K, V> map;

    /**
     * Initialize MapBuilder.
     *
     * @param map Map
     */
    public MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    /**
     * put to map.
     *
     * @param key   key
     * @param value value
     * @return for method chain
     */
    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    /**
     * return the map.
     *
     * @return Map
     */
    public Map<K, V> toMap() {
        return map;
    }

    /**
     * return the unmodifiable map.
     *
     * @return Unmodifiable map
     */
    public Map<K, V> toUnmodifiableMap() {
        return Collections.unmodifiableMap(map);
    }
}
