package jp.jyn.jbukkitlib.uuid;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * UUID from/to byte array converter.
 */
public class UUIDBytes {
    private UUIDBytes() {}

    /**
     * UUID to byte array
     *
     * @param uuid uuid
     * @return byte array
     */
    public static byte[] toBytes(UUID uuid) {
        return ByteBuffer.allocate(16)
            .putLong(uuid.getMostSignificantBits())
            .putLong(uuid.getLeastSignificantBits())
            .array();
    }

    /**
     * byte array to UUID
     *
     * @param bytes byte array
     * @return UUID
     */
    public static UUID fromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long most = bb.getLong();
        long least = bb.getLong();
        return new UUID(most, least);
    }
}
