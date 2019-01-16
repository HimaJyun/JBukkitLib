package jp.jyn.jbukkitlib.util.pair;

import java.util.AbstractMap;
import java.util.Map;

public class MutablePair<K, V> implements Pair<K, V> {
    public K key;
    public V value;

    public MutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public MutablePair(Map.Entry<K, V> entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
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
        return value;
    }

    @Override
    public Map.Entry<K, V> toMapEntry() {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    @Override
    public V put(Map<K, V> map) {
        return map.put(key, value);
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
