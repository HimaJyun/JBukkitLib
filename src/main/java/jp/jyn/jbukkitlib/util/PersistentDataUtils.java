package jp.jyn.jbukkitlib.util;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class PersistentDataUtils {
    private PersistentDataUtils() {}

    /**
     * Toggle the value. Set this if it doesn't have a value and remove it if it does.
     *
     * @param key  Data key
     * @param data Data container
     * @return The current state.
     */
    public static boolean toggle(NamespacedKey key, PersistentDataContainer data) {
        if (data.has(key, PersistentDataType.BYTE)) {
            data.remove(key);
            return false;
        } else {
            data.set(key, PersistentDataType.BYTE, (byte) 1);
            return true;
        }
    }

    /**
     * Store UUID as int array
     */
    public static final PersistentDataType<int[], UUID> UUID = new PersistentDataType<int[], UUID>() {
        @Override
        public Class<int[]> getPrimitiveType() {
            return int[].class;
        }

        @Override
        public Class<UUID> getComplexType() {
            return UUID.class;
        }

        @Override
        public int[] toPrimitive(UUID complex, PersistentDataAdapterContext context) {
            long most = complex.getMostSignificantBits();
            long least = complex.getLeastSignificantBits();
            return new int[]{
                (int) (most >>> 32),
                (int) most,
                (int) (least >>> 32),
                (int) least
            };
        }

        @Override
        public UUID fromPrimitive(int[] primitive, PersistentDataAdapterContext context) {
            long most = (((long) primitive[0]) << 32) | primitive[1];
            long least = (((long) primitive[2]) << 32) | primitive[3];
            return new UUID(most, least);
        }
    };

    /**
     * Store boolean as byte
     */
    public final static PersistentDataType<Byte, Boolean> BOOLEAN = new PersistentDataType<Byte, Boolean>() {
        @Override
        public Class<Byte> getPrimitiveType() {
            return Byte.class;
        }

        @Override
        public Class<Boolean> getComplexType() {
            return Boolean.class;
        }

        @Override
        public Byte toPrimitive(Boolean complex, PersistentDataAdapterContext context) {
            return (byte) (complex ? 1 : 0);
        }

        @Override
        public Boolean fromPrimitive(Byte primitive, PersistentDataAdapterContext context) {
            return primitive == 1 ? Boolean.TRUE : Boolean.FALSE;
        }
    };
}
