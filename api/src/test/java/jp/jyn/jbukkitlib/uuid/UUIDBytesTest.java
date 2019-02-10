package jp.jyn.jbukkitlib.uuid;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UUIDBytesTest {

    @Test
    public void test() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, UUIDBytes.fromBytes(UUIDBytes.toBytes(uuid)));
    }
}
