package rs2.net;

import rs2.model.Player;
import rs2.net.codec.HandshakeDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Patrick
 */
public final class Client {

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(256);
    private final SocketChannel channel;

    private ClientDecoder decoder = HandshakeDecoder.HANDSHAKE;

    private Player player;

    public Client(SocketChannel channel) {
        this.channel = channel;
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(ClientBuffer buffer) {
        write(buffer.getBuffer());
    }

    public void write(ByteBuffer buffer) {
        if (buffer == null) {
            close();
            return;
        }

        buffer.flip();
        try {
            channel.write(buffer);
        } catch (IOException e) {
            close();
        }
    }

    public void decodeNext(ClientDecoder decoder) {
        this.decoder = decoder;
    }

    public void decode() throws IOException {
        if (channel.read(buffer) < 0) {
            close();
            return;
        }
        buffer.flip();
        if (decoder != null) decoder.decode(this, buffer);
        buffer.clear();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

}
