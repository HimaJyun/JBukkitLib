package jp.jyn.jbukkitlib.util.pair;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Mutable pair
 *
 * @param <K> Type 1
 * @param <V> Type 2
 */
public class MutablePair<K, V> implements Pair<K, V> {
    public K key;
    public V value;

    /**
     * Create new mutable pair
     *
     * @param key   value 1
     * @param value value 2
     */
    public MutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Create new mutable pair
     *
     * @param entry Map.Entry
     */
    public MutablePair(Map.Entry<K, V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    /**
     * Create new mutable pair
     *
     * @param pair Pair
     */
    public MutablePair(Pair<K, V> pair) {
        this(pair.getKey(), pair.getValue());
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
    public static <K, V> MutablePair<K, V> of(K key, V value) {
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
    public static <K, V> MutablePair<K, V> of(Pair<K, V> pair) {
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
    public static <K, V> MutablePair<K, V> of(Map.Entry<K, V> entry) {
        return new MutablePair<>(entry);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public K setKey(K key) {
        K old = this.key;
        this.key = key;
        return old;
    }

    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }

    @Override
    public Map.Entry<K, V> toMapEntry() {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    @Override
    public String toString() {
        return "MutablePair[" + key + ", " + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutablePair<?, ?> that = (MutablePair<?, ?>) o;
        return key.equals(that.key) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }
}
