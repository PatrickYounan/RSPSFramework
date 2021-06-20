package rs2.net;

import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public final class ClientBuffer {

    public static final int[] BIT_MASK = {
            0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff,
            0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
            0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1
    };

    private final ByteBuffer buffer;

    private int bitPosition;
    private int marked;

    public ClientBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void accessBitMode() {
        bitPosition = buffer.position() * 8;
    }

    public void accessByteMode() {
        buffer.position((bitPosition + 7) / 8);
    }

    public void putHeader(int value) {
        putByte(value);
    }

    public void putVarHeader(int value) {
        putHeader(value);
        marked = buffer.position();
        putByte(0);
    }

    public void putVarShortHeader(int value) {
        putHeader(value);
        marked = buffer.position();
        putShort(0);
    }

    public void flushVarHeader() {
        buffer.put(marked, (byte) (buffer.position() - marked - 1));
    }

    public void flushVarShortHeader() {
        buffer.putShort(marked, (short) (buffer.position() - marked - 2));
    }

    public void copyBytes(ClientBuffer clientBuffer) {
        for (int i = 0; i < clientBuffer.getBuffer().position(); i++) putByte(clientBuffer.getBuffer().get(i));
    }

    public void copyBytesA(ClientBuffer clientBuffer) {
        for (int i = 0; i < clientBuffer.getBuffer().position(); i++) putByteA(clientBuffer.getBuffer().get(i));
    }

    public void copyBytesA(byte[] values) {
        for (byte value : values) putByteA(value);
    }

    public void putByte(int value) {
        buffer.put((byte) value);
    }

    public void putByteA(int value) {
        putByte(value + 128);
    }

    public void putByteC(int value) {
        putByte(-value);
    }

    public void putByteS(int value) {
        putByte(128 - value);
    }

    public void putInt(int value) {
        buffer.putInt(value);
    }

    public void putIntMiddle(int value) {
        putByte(value >> 8);
        putByte(value);
        putByte(value >> 24);
        putByte(value >> 16);
    }

    public void putIntInvMiddle(int value) {
        putByte(value >> 16);
        putByte(value >> 24);
        putByte(value);
        putByte(value >> 8);
    }

    public void putLEInt(int value) {
        putByte(value);
        putByte(value >> 8);
        putByte(value >> 16);
        putByte(value >> 24);
    }

    public void putShort(int value) {
        buffer.putShort((short) value);
    }

    public void putShortA(int value) {
        putByte(value >> 8);
        putByteA(value);
    }

    public void putLEShort(int value) {
        putByte(value);
        putByte(value >> 8);
    }

    public void putLEShortA(int value) {
        putByteA(value);
        putByte(value >> 8);
    }

    public void putLong(long value) {
        buffer.putLong(value);
    }

    public void putLELong(long value) {
        putByte((int) value);
        putByte((int) (value >> 8));
        putByte((int) (value >> 16));
        putByte((int) (value >> 24));
        putByte((int) (value >> 32));
        putByte((int) (value >> 40));
        putByte((int) (value >> 48));
        putByte((int) (value >> 56));
    }

    public void putString(String value) {
        buffer.put(value.getBytes());
        putByte(0);
    }

    public void putBits(int amount, int value) {
        int bytePos = bitPosition >> 3;
        int bitOffset = 8 - (bitPosition & 7);
        bitPosition += amount;

        for (; amount > bitOffset; bitOffset = 8) {
            byte tmp = buffer.get(bytePos);
            tmp &= ~BIT_MASK[bitOffset];
            tmp |= (value >> (amount - bitOffset)) & BIT_MASK[bitOffset];
            buffer.put(bytePos++, tmp);
            amount -= bitOffset;
        }
        byte previous = buffer.get(bytePos);
        if (amount == bitOffset) {
            previous &= ~BIT_MASK[bitOffset];
            previous |= value & BIT_MASK[bitOffset];
        } else {
            previous &= ~(BIT_MASK[amount] << (bitOffset - amount));
            previous |= (value & BIT_MASK[amount]) << (bitOffset - amount);
        }
        buffer.put(bytePos, previous);
    }

    public int readByte() {
        return buffer.get();
    }

    public int readUnsignedByte() {
        return buffer.get() & 0xFF;
    }

    public int readByteC() {
        return -readByte();
    }

    public int readByteA() {
        return readUnsignedByte() - 128;
    }

    public int readByteS() {
        return 128 - readByte();
    }

    public int readInt() {
        return buffer.getInt();
    }

    public int readLEInt() {
        return readUnsignedByte() + (readUnsignedByte() << 8) + (readUnsignedByte() << 16) + (readUnsignedByte() << 24);
    }

    public int readShort() {
        return buffer.getShort();
    }

    public int readShortA() {
        return readUnsignedByte() << 8 | readByteA() & 0xFF;
    }

    public int readLEShort() {
        return readUnsignedByte() | readUnsignedByte() << 8;
    }

    public int readLEShortA() {
        return (readByteA() & 0xFF) | ((readUnsignedByte() << 8));
    }

    public long readLong() {
        return buffer.getLong();
    }

    public long readLELong() {
        return readByte() |
                (long) readByte() << 8L |
                (long) readByte() << 16L |
                (long) readByte() << 24L |
                (long) readByte() << 32L |
                (long) readByte() << 40L |
                (long) readByte() << 48L |
                (long) readByte() << 56L;
    }

    public String readString() {
        StringBuilder string = new StringBuilder();
        byte value;

        while ((value = (byte) readByte()) != 0)
            string.append((char) value);
        return string.toString();
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public static ClientBuffer alloc(int size) {
        return new ClientBuffer(ByteBuffer.allocate(size));
    }

    public static ClientBuffer copy(ByteBuffer buffer) {
        return new ClientBuffer(buffer);
    }

}
