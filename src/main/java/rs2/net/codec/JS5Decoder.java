package rs2.net.codec;

import rs2.cache.Cache;
import rs2.net.Client;
import rs2.net.ClientDecoder;

import java.nio.ByteBuffer;

/**
 * @author Patrick
 */
public class JS5Decoder implements ClientDecoder {

    public static final JS5Decoder JS5 = new JS5Decoder();

    @Override
    public void decode(Client client, ByteBuffer buffer) {
        while (buffer.remaining() >= 4) {
            buffer.mark();
            int opcode = buffer.get() & 0xFF;
            switch (opcode) {
            case 0:
            case 1:
                int container = buffer.get() & 0xFF;
                int archive = buffer.getShort() & 0xFFFF;
                if (container == 255 && archive == 255) {
                    client.write(Cache.getReferenceData());
                    return;
                }
                client.write(Cache.getArchiveData(container, archive));
                break;
            case 2:
            case 3:
            case 4:
                buffer.get();
                buffer.getShort();
                break;
            case 5:
            case 9:
                if (buffer.remaining() < 4) {
                    buffer.reset();
                    return;
                }
                buffer.getInt();
                break;
            case 6:
                buffer.get();
                buffer.get();
                buffer.get();
                buffer.getShort();
                break;
            case 7:
                buffer.get();
                buffer.getShort();
                client.close();
                break;
            default:
                System.out.println("Unhandled JS5 opcode: " + opcode + "!");
                buffer.get();
                buffer.getShort();
                break;
            }
        }
    }
}


