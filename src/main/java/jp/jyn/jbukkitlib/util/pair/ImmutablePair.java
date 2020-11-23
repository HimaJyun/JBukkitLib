package jp.jyn.jbukkitlib.util.pair;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Immutable pair
 *
 * @param <K> Type 1
 * @param <V> Type 2
 */
public class ImmutablePair<K, V> implements Pair<K, V> {
    public final K key;
    public final V value;

    /**
     * Create new immutable pair
     *
     * @param key   value 1
     * @param value value 2
     */
    public ImmutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Create new immutable pair
     *
     * @param entry Map.Entry
     */
    public ImmutablePair(Map.Entry<K, V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    /**
     * Create new immutable pair
     *
     * @param pair Pair
     */
    public ImmutablePair(Pair<K, V> pair) {
        this(pair.getKey(), pair.getValue());
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
    public static <K, V> ImmutablePair<K, V> of(K key, V value) {
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
    public static <K, V> ImmutablePair<K, V> of(Pair<K, V> pair) {
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
    public static <K, V> ImmutablePair<K, V> of(Map.Entry<K, V> entry) {
        return new ImmutablePair<>(entry);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public Map.Entry<K, V> toMapEntry() {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    @Override
    public String toString() {
        return "ImmutablePair[" + key + ", " + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
        return key.equals(that.key) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }

}
