package com.github.boubari97.serversquery;

import org.junit.jupiter.api.Test;
import com.github.boubari97.serversquery.queries.QueryFilterBuilder;
import com.github.boubari97.serversquery.queries.RegionConst;
import com.github.boubari97.serversquery.queries.Requests;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class EncodeQueryTest {

    @Test
    void encoderTest() {
        QueryFilterBuilder builder = new QueryFilterBuilder().napp(500);
        byte[] actual = Requests.masterRequest(RegionConst.EVERYWHERE,
                new InetSocketAddress("0.0.0.0", 0), builder.build());
        byte[] expected = {0x31, (byte) 0xFF, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x3A,
                0x30, 0x00, 0x5C, 0x6E, 0x61, 0x70, 0x70, 0x5C, 0x35, 0x30, 0x30, 0x00};

        assertArrayEquals(expected, actual);
    }

}
