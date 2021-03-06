package jp.jyn.jbukkitlib.uuid;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UUIDConverterTest {

    private final UUID himajyun = UUID.fromString("3d4187e5-5565-48a3-899f-0fcc365e7084");

    @Disabled
    @Test
    public void uuidGetterTest() throws Exception {
        Optional<Map.Entry<String, UUID>> result = new UUIDConverter.UUIDGetter("himajyun").call();
        assertTrue(result.isPresent());
        assertEquals(himajyun, result.get().getValue());
        System.out.println(result.get().getKey() + " : " + result.get().getValue());

        result = new UUIDConverter.UUIDGetter("not-found-user").call();
        assertFalse(result.isPresent());
    }

    @Disabled
    @Test
    public void multipleUUIDGetterTest() throws Exception {
        Map<String, UUID> result = new UUIDConverter.MultipleUUIDGetter("himajyun", "nuyjamih").call();
        System.out.println(result);
    }

    @Disabled
    @Test
    public void nameGetterTest() throws Exception {
        Optional<String> result = new UUIDConverter.NameGetter(himajyun).call();
        assertTrue(result.isPresent());
        System.out.println(result.get() + " : " + himajyun.toString());

        result = new UUIDConverter.NameGetter(UUID.randomUUID()).call();
        assertFalse(result.isPresent());
    }
}
