package rs2.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author Patrick
 */
public final class ClientServer implements Runnable {

    private final ServerSocketChannel server;
    private final Selector selector;

    public ClientServer() throws IOException {
        this.server = ServerSocketChannel.open();
        this.selector = Selector.open();
    }

    public void bind() throws IOException {
        server.configureBlocking(false).register(selector, SelectionKey.OP_ACCEPT);
        server.bind(new InetSocketAddress(43594));
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                selector.select();
                for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (!key.isValid()) {
                        key.cancel();
                        continue;
                    }
                    if (key.isAcceptable()) {

                        SocketChannel channel = server.accept();
                        if (channel == null)
                            continue;
                        channel.configureBlocking(false).register(selector, SelectionKey.OP_READ).attach(new Client(channel));
                    } else if (key.isReadable()) {
                        Client client = (Client) key.attachment();
                        try {
                            client.decode();
                        } catch (Exception e) {
                            client.close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
