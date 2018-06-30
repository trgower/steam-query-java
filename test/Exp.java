import org.junit.jupiter.api.Test;
import steam.MasterServers;
import steam.queries.QueryFilterBuilder;
import steam.servers.MasterServer;
import steam.servers.GameServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Exp {

    @Test
    public void test() throws IOException {
        MasterServer server = new MasterServer(MasterServers.SOURCE);
        String filter = new QueryFilterBuilder().napp(500).game("dayz").build();
        HashSet<InetSocketAddress> list = server.getServers(filter);

        HashMap<InetSocketAddress, GameServer> map = new HashMap<>();
        Iterator<InetSocketAddress> it = list.iterator();
        while (it.hasNext()) {
            InetSocketAddress addr = it.next();
            map.put(addr, new GameServer(addr));
        }
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
