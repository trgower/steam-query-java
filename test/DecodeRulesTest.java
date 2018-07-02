import org.junit.jupiter.api.Test;
import steam.queries.Requests;
import steam.GameServer;

import java.io.IOException;
import java.net.DatagramPacket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecodeRulesTest {

    @Test
    public void decodeTest() throws IOException {
        GameServer gs = new GameServer();
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, Requests.RULES_RESPONSE, 0x01, 0x00, 0x61, 0x61, 0x61, 0x00, 0x61, 0x61, 0x61, 0x00};
        DatagramPacket recv = new DatagramPacket(response, response.length);

        gs.parseRules(recv);

        assertEquals("aaa", gs.getRules().get("aaa"));
    }

}
