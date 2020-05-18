package jp.jyn.jbukkitlib.util;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Simple pair
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
    Map.Entry<K, V> toMapEntry();

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
     * @return Immutable pair
     */
    static <K, V> Pair<K, V> of(K key, V value) {
        return new Immutable<>(key, value);
    }

    /**
     * Immutable pair
     *
     * @param <K> Type 1
     * @param <V> Type 2
     */
    class Immutable<K, V> implements Pair<K, V> {
        public final K key;
        public final V value;

        public Immutable(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public Immutable(Map.Entry<K, V> entry) {
            this(entry.getKey(), entry.getValue());
        }

        public Immutable(Pair<K, V> pair) {
            this(pair.getKey(), pair.getValue());
        }

        public static <K, V> Immutable<K, V> of(K key, V value) {
            return new Immutable<>(key, value);
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
            return new AbstractMap.SimpleEntry<>(key, value);
        }

        @Override
        public String toString() {
            return "Pair.Immutable[" + key + ", " + value + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Immutable<?, ?> that = (Immutable<?, ?>) o;
            return key.equals(that.key) && value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }
    }

    /**
     * Mutable pair
     *
     * @param <K> Type 1
     * @param <V> Type 2
     */
    class Mutable<K, V> implements Pair<K, V> {
        public K key;
        public V value;

        public Mutable(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public Mutable(Map.Entry<K, V> entry) {
            this(entry.getKey(), entry.getValue());
        }

        public Mutable(Pair<K, V> pair) {
            this(pair.getKey(), pair.getValue());
        }

        public static <K, V> Mutable<K, V> of(K key, V value) {
            return new Mutable<>(key, value);
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
            return new AbstractMap.SimpleImmutableEntry<>(key, value);
        }

        @Override
        public String toString() {
            return "Pair.Mutable[" + key + ", " + value + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Mutable<?, ?> that = (Mutable<?, ?>) o;
            return key.equals(that.key) && value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }
    }
}
