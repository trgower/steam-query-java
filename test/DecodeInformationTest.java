import org.junit.jupiter.api.Test;
import steam.GameServer;

import java.io.IOException;
import java.net.DatagramPacket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecodeInformationTest {

    @Test
    public void decodeTest() throws IOException {
        byte[] res = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x61, 0x61, 0x61, 0x00, 0x61, 0x61, 0x61, 0x00, 0x61, 0x61, 0x61, 0x00, 0x61, 0x61, 0x61, 0x00, 0x00, 0x00, 0x01, 0x0A, 0x02, 'd', 'w', 0x00, 0x01, 0x61, 0x61, 0x00, 0x00};
        DatagramPacket packet = new DatagramPacket(res, res.length);
        GameServer gs = new GameServer();
        gs.parseInfo(packet);

        assertEquals(0, gs.getProtocol());
        assertEquals("aaa", gs.getName());
        assertEquals("aaa", gs.getMap());
        assertEquals("aaa", gs.getFolder());
        assertEquals("aaa", gs.getGame());
        assertEquals(0, gs.getAppid());
        assertEquals(1, gs.getPlayers());
        assertEquals(10, gs.getMaxPlayers());
        assertEquals(2, gs.getBots());
        assertEquals('d', gs.getType());
        assertEquals('w', gs.getEnv());
        assertEquals(0, gs.getVisibility());
        assertEquals(1, gs.getVac());
        assertEquals("aa", gs.getVersion());
        assertEquals(0, gs.getEDF());
    }
}
