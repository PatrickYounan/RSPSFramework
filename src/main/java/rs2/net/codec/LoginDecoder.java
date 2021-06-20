package rs2.net.codec;

import rs2.model.Player;
import rs2.cache.ByteBufferUtils;
import rs2.cache.StringUtils;
import rs2.net.Client;
import rs2.net.ClientDecoder;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public final class LoginDecoder implements ClientDecoder {

    public static final LoginDecoder LOGIN = new LoginDecoder();

    @Override
    public void decode(Client client, ByteBuffer buffer) {
        if (buffer.remaining() < 2) {
            buffer.compact();
            return;
        }
        int loginType = buffer.get();
        if (loginType != 16 && loginType != 18) {
            client.close();
            return;
        }
        int blockLength = buffer.get() & 0xff;
        if (buffer.remaining() < blockLength) {
            buffer.flip();
            buffer.compact();
            return;
        }

        if (buffer.getInt() != 421) {
            client.close();
            return;
        }

        buffer.get();

        for (int i = 0; i < 12; i++)
            buffer.getInt();

        int rsaPayload = buffer.get() & 0xFF;
        byte[] rsa = new byte[rsaPayload];
        buffer.get(rsa);

        ByteBuffer rsaBuffer = ByteBuffer.wrap(new BigInteger(rsa).toByteArray());
        int rsaOpcode = rsaBuffer.get() & 0xFF;

        if (rsaOpcode != 10) {
            client.close();
            return;
        }

        rsaBuffer.getLong();
        rsaBuffer.getLong();

        rsaBuffer.getInt();

        String username = StringUtils.getStringFromLong(rsaBuffer.getLong());
        String password = ByteBufferUtils.getString(rsaBuffer);

        Player player = new Player(client, username, password);
        player.getWriter().writeLogin();

        client.setPlayer(player);
        client.decodeNext(PacketDecoder.PACKET);
    }

}
