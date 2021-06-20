package rs2.cache;

import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public final class ByteBufferUtils {

    /**
     * Gets a string from the byte buffer.
     *
     * @param buffer The byte buffer.
     * @return The string.
     */
    public static String getString(ByteBuffer buffer) {
        StringBuilder string = new StringBuilder();
        byte value;
        while ((value = buffer.get()) != 0)
            string.append((char) value);
        return string.toString();
    }

}