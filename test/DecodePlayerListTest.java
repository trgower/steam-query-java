import org.junit.jupiter.api.Test;
import steam.queries.SourceQuery;
import steam.servers.GameServer;

import java.io.IOException;
import java.net.DatagramPacket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecodePlayerListTest {

    @Test
    public void decodeTest() throws IOException {
        GameServer gs = new GameServer();
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, SourceQuery.PLAYERS_LIST, 0x01, 0x00, 0x61, 0x61, 0x61, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        DatagramPacket recv = new DatagramPacket(response, response.length);

        gs.parsePlayers(recv);

        assertEquals("aaa", gs.getPlayerList().get(0).getName());
        assertEquals(1, gs.getPlayerList().get(0).getScore());
    }

}
