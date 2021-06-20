package rs2.cache;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * A cache reader.
 *
 * @author Patrick
 */
public final class Cache {

    private static CacheFileManager[] fileManagers;

    private static CacheFile referenceFile;

    public static byte[] reference = null;

    public static void init(String path) throws Throwable {
        byte[] cacheFileBuffer = new byte[520];
        RandomAccessFile containersInformFile = new RandomAccessFile(path + "/main_file_cache.idx255", "r");
        RandomAccessFile dataFile = new RandomAccessFile(path + "/main_file_cache.dat2", "r");
        referenceFile = new CacheFile(255, containersInformFile, dataFile, 500000, cacheFileBuffer);
        int length = (int) (containersInformFile.length() / 6);
        fileManagers = new CacheFileManager[length];

        for (int i = 0; i < length; i++) {
            File file = new File(path + "/main_file_cache.idx" + i);
            if (file.exists() && file.length() > 0)
                fileManagers[i] = new CacheFileManager(new CacheFile(i, new RandomAccessFile(file, "r"), dataFile, 1000000, cacheFileBuffer), true);
        }
        reference = Cache.generateReferences();
    }

    public static ByteBuffer getArchiveData(int index, int archive) {
        final byte[] data = index == 255 ? referenceFile.getContainerData(archive) : fileManagers[index].getCacheFile().getContainerData(archive);
        if (data == null) return null;

        final int compression = data[0] & 0xff;
        final boolean referenceTable = index == 255 && archive == 255;
        final int length = referenceTable ? data.length : ((data[1] & 0xff) << 24) + ((data[2] & 0xff) << 16) + ((data[3] & 0xff) << 8) + (data[4] & 0xff);
        final int offset = referenceTable ? 0 : 5;

        final int writeLength = length + offset + (compression != 0 ? 4 : 0);
        final ByteBuffer buf = ByteBuffer.allocate(writeLength + 8 + (length / 512));
        buf.put((byte) index);
        buf.putShort((short) archive);
        buf.put((byte) compression);
        buf.putInt(length);
        for (int i = offset; i < writeLength; i++) {
            if (buf.position() % 512 == 0) buf.put((byte) 255);
            buf.put(data[i]);
        }

        return buf;
    }

    public static ByteBuffer getReferenceData() {
        ByteBuffer buffer = ByteBuffer.allocate(reference.length << 2);
        buffer.put((byte) 255);
        buffer.putShort((short) 255);
        buffer.put((byte) 0);
        buffer.putInt(reference.length);
        int offset = 10;
        for (byte aCachedReference : reference) {
            if (offset == 512) {
                buffer.put((byte) 255);
                offset = 1;
            }
            buffer.put(aCachedReference);
            offset++;
        }
        return buffer;
    }

    public static byte[] generateReferences() {
        ByteBuffer buffer = ByteBuffer.allocate(fileManagers.length * 8);
        for (int index = 0; index < fileManagers.length; index++) {
            if (fileManagers[index] == null) {
                buffer.putInt(index == 24 ? 609698396 : 0);
                continue;
            }
            buffer.putInt(fileManagers[index].getInformation().getInformation().getCrc());
        }
        return buffer.array();
    }

    public static CacheFile getReferenceFile() {
        return referenceFile;
    }


}