import org.junit.jupiter.api.Test;
import steam.Tools;
import steam.queries.SourceQuery;
import steam.servers.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecodePlayerListTest {

    private ArrayList<Player> playerList;

    @Test
    public void decodeTest() throws IOException {
        playerList = new ArrayList<Player>();
        byte[] response = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, SourceQuery.PLAYERS_LIST, 0x01, 0x00, 0x61, 0x61, 0x61, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        DatagramPacket recv = new DatagramPacket(response, response.length);
        parsePlayers(recv);

        assertEquals("aaa", playerList.get(0).getName());
        assertEquals(1, playerList.get(0).getScore());
    }

    // This isn't the right way to do it, fix it
    public void parsePlayers(DatagramPacket packet) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.getData()));
        dis.skipBytes(5);

        int num = dis.readByte();
        for (int i = 0; i < num; i++) {
            playerList.add(new Player(
                    dis.readByte(),
                    Tools.readString(dis),
                    Integer.reverseBytes(dis.readInt()),
                    Float.intBitsToFloat(Integer.reverseBytes(dis.readInt()))));
        }

    }

}
