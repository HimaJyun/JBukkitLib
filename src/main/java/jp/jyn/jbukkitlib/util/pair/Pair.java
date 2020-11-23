package jp.jyn.jbukkitlib.util.pair;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Pair
 *
 * @param <K> Type 1
 * @param <V> Type 2
 */
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
    default Map.Entry<K, V> toMapEntry() {
        return new AbstractMap.SimpleEntry<>(getKey(), getValue());
    }

    /**
     * Add key/value to Map
     *
     * @param map target Map
     * @return Return value of {@link java.util.Map#put(Object, Object)}
     */
    default V put(Map<K, V> map) {
        return map.put(getKey(), getValue());
    }

    /**
     * Create new pair
     *
     * @param key   value 1
     * @param value value 2
     * @param <K>   Type 1
     * @param <V>   Type 2
     * @return Pair
     */
    static <K, V> Pair<K, V> of(K key, V value) {
        return new ImmutablePair<>(key, value);
    }

    /**
     * Create new pair
     *
     * @param pair Pair
     * @param <K>  Type 1
     * @param <V>  Type 2
     * @return Pair
     */
    static <K, V> Pair<K, V> of(Pair<K, V> pair) {
        return new ImmutablePair<>(pair);
    }

    /**
     * Create new pair
     *
     * @param entry Map.Entry
     * @param <K>   Type 1
     * @param <V>   Type 2
     * @return Pair
     */
    static <K, V> Pair<K, V> of(Map.Entry<K, V> entry) {
        return new ImmutablePair<>(entry);
    }

    /**
     * Create new immutable pair
     *
     * @param key   value 1
     * @param value value 2
     * @param <K>   Type 1
     * @param <V>   Type 2
     * @return ImmutablePair
     */
    static <K, V> ImmutablePair<K, V> immutable(K key, V value) {
        return new ImmutablePair<>(key, value);
    }

    /**
     * Create new immutable pair
     *
     * @param pair Pair
     * @param <K>  Type 1
     * @param <V>  Type 2
     * @return ImmutablePair
     */
    static <K, V> ImmutablePair<K, V> immutable(Pair<K, V> pair) {
        return new ImmutablePair<>(pair);
    }

    /**
     * Create new immutable pair
     *
     * @param entry Map.Entry
     * @param <K>   Type 1
     * @param <V>   Type 2
     * @return ImmutablePair
     */
    static <K, V> ImmutablePair<K, V> immutable(Map.Entry<K, V> entry) {
        return new ImmutablePair<>(entry);
    }

    /**
     * Create new mutable pair
     *
     * @param key   value 1
     * @param value value 2
     * @param <K>   Type 1
     * @param <V>   Type 2
     * @return MutablePair
     */
    static <K, V> MutablePair<K, V> mutable(K key, V value) {
        return new MutablePair<>(key, value);
    }

    /**
     * Create new mutable pair
     *
     * @param pair Pair
     * @param <K>  Type 1
     * @param <V>  Type 2
     * @return MutablePair
     */
    static <K, V> MutablePair<K, V> mutable(Pair<K, V> pair) {
        return new MutablePair<>(pair);
    }

    /**
     * Create new mutable pair
     *
     * @param entry Map.Entry
     * @param <K>   Type 1
     * @param <V>   Type 2
     * @return MutablePair
     */
    static <K, V> MutablePair<K, V> mutable(Map.Entry<K, V> entry) {
        return new MutablePair<>(entry);
    }
}
