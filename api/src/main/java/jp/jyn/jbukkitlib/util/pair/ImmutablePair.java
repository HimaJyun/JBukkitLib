package jp.jyn.jbukkitlib.util.pair;

import java.util.AbstractMap;
import java.util.Map;

public class ImmutablePair<K, V> implements Pair<K, V> {
    public final K key;
    public final V value;

    public ImmutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public ImmutablePair(Map.Entry<K, V> entry) {
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

    @Override
    public Map.Entry<K, V> toMapEntry() {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    @Override
    public V put(Map<K, V> map) {
        return map.put(key, value);
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
