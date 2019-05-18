package jp.jyn.jbukkitlib.uuid;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UUIDBytesTest {

    @Test
    public void test() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, UUIDBytes.fromBytes(UUIDBytes.toBytes(uuid)));
    }
}
