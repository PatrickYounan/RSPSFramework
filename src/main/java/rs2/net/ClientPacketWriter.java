package rs2.net;

import rs2.model.Player;
import rs2.model.Position;

import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public final class ClientPacketWriter {

    public static final int[] SIDEBAR_INTERFACES = {
            90, 320, 274, 149, 387, 271, 192, -1, 131, 148, 182, 261, 262, 239
    };

    private final Player player;

    public ClientPacketWriter(Player player) {
        this.player = player;
    }

    public void writeLogin() {
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.put((byte) 2); // return code
        buffer.put((byte) 0); // rights
        buffer.put((byte) 0);
        buffer.putShort((short) 1); // index
        buffer.put((byte) 0);
        player.getClient().write(buffer);

        writeMap();

        for (int i = 0; i < SIDEBAR_INTERFACES.length; i++) {
            writeTab(i, SIDEBAR_INTERFACES[i]);
        }

    }

    public void writeMap() {
        ClientBuffer out = ClientBuffer.alloc(157);
        Position position = player.getPosition();
        out.putVarShortHeader(251);

        for (int x = (position.getRegionX() - 6) / 8; x <= ((position.getRegionX() + 6) / 8); x++) {
            for (int y = (position.getRegionY() - 6) / 8; y <= ((position.getRegionY() + 6) / 8); y++) {
                out.putInt(0);
                out.putInt(0);
                out.putInt(0);
                out.putInt(0);
            }
        }
        out.putByteS(position.getZ());
        out.putShort(position.getRegionX());
        out.putLEShort(position.getRegionY());
        out.putShort(position.getSceneY());
        out.putShort(position.getSceneX());

        out.flushVarShortHeader();
        player.getClient().write(out);
    }

    public void writeTab(int index, int widget) {
        ClientBuffer out = ClientBuffer.alloc(4);
        out.putHeader(138);
        out.putByte(index);
        out.putShortA(widget);
        player.getClient().write(out);
    }

}
