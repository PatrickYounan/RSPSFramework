package rs2.cache;

import java.nio.ByteBuffer;
import java.util.zip.Inflater;

public final class GZipDecompressor {

    private static final Inflater inflater = new Inflater(true);

    public static void decompress(ByteBuffer buffer, byte[] data) {
        synchronized (inflater) {
            try {
                inflater.setInput(buffer.array(), buffer.position() + 10, -buffer.position() - 18 + buffer.limit());
                inflater.inflate(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            inflater.reset();
        }
    }

}
