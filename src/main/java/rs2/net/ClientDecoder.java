package rs2.net;

import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public interface ClientDecoder {

    void decode(Client client, ByteBuffer buffer);
}
