package steam;

import org.junit.jupiter.api.Test;
import steam.queries.Requests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecodeResponseTest {

    @Test
    void testDecodeFromGameServer() throws IOException {
        byte[] res = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x61, 0x61, 0x61, 0x00, 0x61,
                0x61, 0x61, 0x00, 0x61, 0x61, 0x61, 0x00, 0x61, 0x61, 0x61, 0x00, 0x00,
                0x00, 0x01, 0x0A, 0x02, 'd', 'w', 0x00, 0x01, 0x61, 0x61, 0x00, 0x00};
        DatagramPacket packet = new DatagramPacket(res, res.length);
        GameServer gs = new GameServer();
        gs.parseInfo(packet);

        assertEquals(0, gs.getProtocol());
        assertEquals("aaa", gs.getName());
        assertEquals("aaa", gs.getMap());
        assertEquals("aaa", gs.getFolder());
        assertEquals("aaa", gs.getGame());
        assertEquals(0, gs.getAppId());
        assertEquals(1, gs.getPlayers());
        assertEquals(10, gs.getMaxPlayers());
        assertEquals(2, gs.getBots());
        assertEquals('d', gs.getType());
        assertEquals('w', gs.getEnv());
        assertEquals(0, gs.getVisibility());
        assertEquals(1, gs.getVac());
        assertEquals("aa", gs.getVersion());
        assertEquals(0, gs.getEdf());
    }

    @Test
    void testDecodeResponseFromMasterServer() throws IOException {
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0x66, (byte) 0x0A, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        DatagramPacket packet = new DatagramPacket(response, response.length);
        MasterServer ms = new MasterServer();
        ms.parseResponse(packet);
        InetSocketAddress expectedAddress = new InetSocketAddress("255.255.255.255", 256);

        assertEquals(expectedAddress, ms.getServer("255.255.255.255", 256).getHost());
    }

    @Test
    void testDecodeParsingPlayers() throws IOException {
        GameServer gs = new GameServer();
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                Requests.PLAYERS_RESPONSE, 0x01, 0x00, 0x61, 0x61, 0x61, 0x00,
                0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        DatagramPacket datagramPacket = new DatagramPacket(response, response.length);
        gs.parsePlayers(datagramPacket);
        Player player = new Player((byte) 0, "aaa", 1, 0.0f);
        List<Player> expectedList = new ArrayList<>();
        expectedList.add(player);
        List<Player> actualList = gs.getPlayerList();

        assertEquals(expectedList, actualList);
    }

    @Test
    void testDecodeRules() throws IOException {
        GameServer gs = new GameServer();
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                Requests.RULES_RESPONSE, 0x01, 0x00, 0x61, 0x61, 0x61, 0x00,
                0x61, 0x61, 0x61, 0x00};
        DatagramPacket datagramPacket = new DatagramPacket(response, response.length);
        gs.parseRules(datagramPacket);
        Map<String, String> expectedRules = new HashMap<>();
        expectedRules.put("aaa", "aaa");

        assertEquals(expectedRules, gs.getRules());
    }
}
