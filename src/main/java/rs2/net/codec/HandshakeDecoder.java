package rs2.net.codec;

import rs2.net.Client;
import rs2.net.ClientDecoder;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * @author Patrick
 */
public final class HandshakeDecoder implements ClientDecoder {

    public static final HandshakeDecoder HANDSHAKE = new HandshakeDecoder();

    static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void decode(Client client, ByteBuffer buffer) {
        int opcode = buffer.get() & 0xFF;
        switch (opcode) {
        case 15:
            client.write(ByteBuffer.allocate(1).put((byte) 0));
            client.decodeNext(JS5Decoder.JS5);
            break;
        case 14:
            buffer.get();
            client.write(ByteBuffer.allocate(9).put((byte) 0).putLong(RANDOM.nextLong()));
            client.decodeNext(LoginDecoder.LOGIN);
            break;
        }
    }

}
