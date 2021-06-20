package rs2.cache;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * @author Patrick
 */
public final class ContainerSegmentData {

    /**
     * The information container.
     */
    private final ContainerSegment information;

    /**
     * The container indexes.
     */
    private int[] containersIndexes;

    /**
     * The containers.
     */
    private FilesContainer[] containers;

    /**
     * Construct a new containers information.
     *
     * @param informationContainerPackedData The information container data packed.
     */
    public ContainerSegmentData(byte[] informationContainerPackedData) {
        information = new ContainerSegment();
        information.setVersion((informationContainerPackedData[informationContainerPackedData.length - 2] << 8 & 0xff00) + (informationContainerPackedData[-1 + informationContainerPackedData.length] & 0xff));
        CRC32 crc32 = new CRC32();
        crc32.update(informationContainerPackedData);
        information.setCrc((int) crc32.getValue());
        decodeContainersInformation(unpackCacheContainer(informationContainerPackedData));
    }

    /**
     * Unpacks a container.
     *
     * @param packedData The packed container data.
     * @return The unpacked data.
     */
    public static byte[] unpackCacheContainer(byte[] packedData) {
        ByteBuffer buffer = ByteBuffer.wrap(packedData);
        int compression = buffer.get() & 0xFF;
        int containerSize = buffer.getInt();
        if (containerSize < 0 || containerSize > 5000000) {
            return null;
        }
        if (compression == 0) {
            byte[] unpacked = new byte[containerSize];
            buffer.get(unpacked, 0, containerSize);
            return unpacked;
        }
        int decompressedSize = buffer.getInt();
        if (decompressedSize < 0 || decompressedSize > 20000000) {
            return null;
        }
        byte[] decompressedData = new byte[decompressedSize];
        if (compression == 1) {
            BZip2Decompressor.decompress(decompressedData, packedData, 9);
        } else {
            GZipDecompressor.decompress(buffer, decompressedData);
        }
        return decompressedData;
    }

    /**
     * Get the container indexes.
     *
     * @return The container indexes.
     */
    public int[] getContainersIndexes() {
        return containersIndexes;
    }

    /**
     * Get the containers.
     *
     * @return The containers.
     */
    public FilesContainer[] getContainers() {
        return containers;
    }

    /**
     * Get the information container.
     *
     * @return The information container.
     */
    public ContainerSegment getInformation() {
        return information;
    }

    /**
     * Decode the containers information.
     *
     * @param data The data.
     */
    public void decodeContainersInformation(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int protocol = buffer.get() & 0xFF;
        if (protocol != 5 && protocol != 6) {
            throw new RuntimeException();
        }

        int revision = protocol < 6 ? 0 : buffer.getInt();
        int nameHash = buffer.get() & 0xFF;

        boolean filesNamed = (0x1 & nameHash) != 0;
        boolean whirpool = (0x2 & nameHash) != 0;

        containersIndexes = new int[buffer.getShort() & 0xFFFF];
        int lastIndex = -1;
        for (int index = 0; index < containersIndexes.length; index++) {
            containersIndexes[index] = (buffer.getShort() & 0xFFFF) + (index == 0 ? 0 : containersIndexes[index - 1]);
            if (containersIndexes[index] > lastIndex) {
                lastIndex = containersIndexes[index];
            }
        }
        containers = new FilesContainer[lastIndex + 1];
        for (int i : containersIndexes) {
            containers[i] = new FilesContainer();
        }
        if (filesNamed) {
            for (int containersIndex : containersIndexes) {
                containers[containersIndex].setNameHash(buffer.getInt());
            }
        }
        byte[][] filesHashes = null;
        if (whirpool) {
            filesHashes = new byte[containers.length][];
            for (int containersIndex : containersIndexes) {
                filesHashes[containersIndex] = new byte[64];
                buffer.get(filesHashes[containersIndex], 0, 64);
            }
        }
        for (int containersIndex : containersIndexes) {
            containers[containersIndex].setCrc(buffer.getInt());
        }
        for (int containersIndex : containersIndexes) {
            containers[containersIndex].setVersion(buffer.getInt());
        }
        for (int containersIndex : containersIndexes) {
            containers[containersIndex].setFilesIndexes(new int[buffer.getShort() & 0xFFFF]);
        }
        for (int containersIndex : containersIndexes) {
            int lastFileIndex = -1;
            for (int fileIndex = 0; fileIndex < containers[containersIndex].getFilesIndexes().length; fileIndex++) {
                containers[containersIndex].getFilesIndexes()[fileIndex] = (buffer.getShort() & 0xFFFF) + (fileIndex == 0 ? 0 : containers[containersIndex].getFilesIndexes()[fileIndex - 1]);
                if (containers[containersIndex].getFilesIndexes()[fileIndex] > lastFileIndex) {
                    lastFileIndex = containers[containersIndex].getFilesIndexes()[fileIndex];
                }
            }
            containers[containersIndex].setFiles(new ContainerSegment[lastFileIndex + 1]);
            for (int fileIndex = 0; fileIndex < containers[containersIndex].getFilesIndexes().length; fileIndex++) {
                containers[containersIndex].getFiles()[containers[containersIndex].getFilesIndexes()[fileIndex]] = new ContainerSegment();
            }
        }
        if (whirpool) {
            for (int containersIndex : containersIndexes) {
                for (int fileIndex = 0; fileIndex < containers[containersIndex].getFilesIndexes().length; fileIndex++) {
                    containers[containersIndex].getFiles()[containers[containersIndex].getFilesIndexes()[fileIndex]].setVersion(filesHashes[containersIndex][containers[containersIndex].getFilesIndexes()[fileIndex]]);
                }
            }
        }
        if (filesNamed) {
            for (int containersIndex : containersIndexes) {
                for (int fileIndex = 0; fileIndex < containers[containersIndex].getFilesIndexes().length; fileIndex++) {
                    containers[containersIndex].getFiles()[containers[containersIndex].getFilesIndexes()[fileIndex]].setNameHash(buffer.getInt());
                }
            }
        }
    }

}
