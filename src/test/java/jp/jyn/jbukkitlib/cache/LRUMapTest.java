package jp.jyn.jbukkitlib.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LRUMapTest {
    @Test
    public void limitTest() {
        final int limit = 10;
        LRUMap<Object, Object> map = new LRUMap<>(limit);

        // Maximum size limit
        for (int i = 0; i < (limit + 1); i++) {
            map.put(new Object(), new Object());
        }
        assertEquals(map.size(), limit);

        // Retain values
        Object key = new Object();
        map.put(key, new Object());
        assertTrue(map.containsKey(key));

        // Delete with LRU
        for (int i = 0; i < (limit + 1); i++) {
            map.put(new Object(), new Object());
        }
        assertFalse(map.containsKey(key));
    }
}
