package rs2.cache;

import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public final class XTEACypher {

    private static final int DELTA = -1640531527;
    private static final int SUM = -957401312;
    private static final int NUM_ROUNDS = 32;

    public static ByteBuffer decrypt(int[] keys, ByteBuffer buffer, int offset, int length) {
        int numBlocks = (length - offset) / 8;
        int[] block = new int[2];
        for (int i = 0; i < numBlocks; i++) {
            int index = i * 8 + offset;
            block[0] = buffer.getInt(index);
            block[1] = buffer.getInt(index + 4);
            decipher(keys, block);
            buffer.putInt(index, block[0]);
            buffer.putInt(index + 4, block[1]);
        }
        return buffer;
    }

    private static void decipher(int[] keys, int[] block) {
        long sum = SUM;
        for (int i = 0; i < NUM_ROUNDS; i++) {
            block[1] -= (keys[(int) ((sum & 0x1933) >>> 11)] + sum ^ block[0] + ((long) block[0] << 4 ^ block[0] >>> 5));
            sum -= DELTA;
            block[0] -= (((long) block[1] << 4 ^ block[1] >>> 5) + block[1] ^ keys[(int) (sum & 0x3)] + sum);
        }
    }

}