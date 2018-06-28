import org.junit.jupiter.api.Test;
import steam.*;
import steam.queries.MasterQuery;
import steam.queries.QueryFilterBuilder;
import steam.servers.MasterServer;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncodeQueryTest {

    @Test
    public void encoderTest() {
        MasterServer server = new MasterServer(MasterServers.SOURCE);
        QueryFilterBuilder builder = new QueryFilterBuilder().napp(500);
        MasterQuery query = new MasterQuery(RegionCode.EVERYWHERE, new InetSocketAddress("0.0.0.0", 0), builder.build());
        byte[] enc = server.encodeQuery(query);
        String res = bytesToHex(enc);

        assertEquals("31FF302E302E302E303A30005C6E6170705C35303000", res);
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
