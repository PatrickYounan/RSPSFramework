package rs2.cache;

/**
 * A container.
 *
 * @author Patrick
 */
public class ContainerSegment {

    private int version = -1;
    private int crc = -1;
    private int nameHash = -1;

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public int getCrc() {
        return crc;
    }

    public void setNameHash(int nameHash) {
        this.nameHash = nameHash;
    }

    public int getNameHash() {
        return nameHash;
    }
}
