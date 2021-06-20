package rs2.model;

/**
 * @author Patrick
 */
public final class Position {

    private final int x;
    private final int y;
    private final int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(int x, int y) {
        this(x, y, 0);
    }

    public int getRegionX() {
        return x >> 3;
    }

    public int getRegionY() {
        return y >> 3;
    }

    public int getSceneX(Position base) {
        return x - ((base.getRegionX() - 6) << 3);
    }

    public int getSceneY(Position base) {
        return y - ((base.getRegionY() - 6) << 3);
    }

    public int getSceneX() {
        return getSceneX(this);
    }

    public int getSceneY() {
        return getSceneY(this);
    }

    public int getLocalX() {
        return x - ((x >> 6) << 6);
    }

    public int getLocalY() {
        return y - ((y >> 6) << 6);
    }

    public int getZ() {
        return z;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
