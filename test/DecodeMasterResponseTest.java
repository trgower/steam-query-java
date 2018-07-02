import org.junit.jupiter.api.Test;
import steam.servers.MasterServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecodeMasterResponseTest {

    @Test
    public void decodeTest() throws IOException {
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x66, (byte) 0x0A, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        DatagramPacket packet = new DatagramPacket(response, response.length);

        MasterServer ms = new MasterServer();

        ArrayList<InetSocketAddress> res = ms.parseResponse(packet);

        assertEquals("255.255.255.255", res.get(0).getAddress().getHostAddress());
        assertEquals("0.0.0.0", res.get(1).getAddress().getHostAddress());
    }

}
