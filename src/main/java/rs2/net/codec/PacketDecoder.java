package rs2.net.codec;

import rs2.net.Client;
import rs2.net.ClientDecoder;

import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public final class PacketDecoder implements ClientDecoder {

    public static final PacketDecoder PACKET = new PacketDecoder();

    @Override
    public void decode(Client client, ByteBuffer buffer) {
    }

}
