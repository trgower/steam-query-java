import org.junit.jupiter.api.Test;
import steam.queries.SourceQuery;
import steam.servers.GameServer;

import java.io.IOException;
import java.net.DatagramPacket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecodeRulesTest {

    @Test
    public void decodeTest() throws IOException {
        GameServer gs = new GameServer();
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, SourceQuery.RULES_RESPONSE, 0x01, 0x00, 0x61, 0x61, 0x61, 0x00, 0x61, 0x61, 0x61, 0x00};
        DatagramPacket recv = new DatagramPacket(response, response.length);

        gs.parseRules(recv);

        assertEquals("aaa", gs.getRules().get("aaa"));
    }

}
