import org.junit.jupiter.api.Test;
import steam.queries.QueryFilterBuilder;
import steam.queries.Region;
import steam.queries.Requests;
import steam.servers.MasterServer;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class EncodeQueryTest {

    @Test
    public void encoderTest() {
        MasterServer server = new MasterServer();
        QueryFilterBuilder builder = new QueryFilterBuilder().napp(500);
        byte[] enc = Requests.MASTER(Region.EVERYWHERE, new InetSocketAddress("0.0.0.0", 0), builder.build());
        byte[] exp = {0x31, (byte) 0xFF, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x3A, 0x30, 0x00, 0x5C, 0x6E, 0x61, 0x70, 0x70, 0x5C, 0x35, 0x30, 0x30, 0x00};

        assertArrayEquals(exp, enc);
    }

}
