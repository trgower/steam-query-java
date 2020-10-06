package com.github.boubari97.serversquery.queries;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Requests {

    public static final byte[] HEADER = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    public static final byte INFO_CODE = 0x54;
    public static final byte PLAYERS_CODE = 0x55;
    public static final byte RULES_CODE = 0x56;
    public static final byte MASTER_CODE = 0x31;
    public static final byte MASTER_RESPONSE = 0x66;
    public static final byte CHALLENGE_RESPONSE = 0x41;
    public static final byte INFO_RESPONSE = 0x49;
    public static final byte PLAYERS_RESPONSE = 0x44;
    public static final byte RULES_RESPONSE = 0x45;
    public static final byte[] INFO_MSG = "Source Engine Query".getBytes();

    private Requests() {

    }

    public static byte[] infoRequest() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(HEADER);
            outputStream.write(INFO_CODE);
            outputStream.write(INFO_MSG);
            outputStream.write(0x00);

            return outputStream.toByteArray();
        }
    }

    public static byte[] playersRequest(byte[] challenge) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(HEADER);
            outputStream.write(PLAYERS_CODE);
            outputStream.write(challenge);

            return outputStream.toByteArray();
        }
    }

    public static byte[] rulesRequest(byte[] challenge) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(HEADER);
            outputStream.write(RULES_CODE);
            outputStream.write(challenge);

            return outputStream.toByteArray();
        }
    }

    public static byte[] masterRequest(byte regionCode, InetSocketAddress host, String filter) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(MASTER_CODE); // Tells the master server that this is a query (?) ...it's an opcode...
            outputStream.write(regionCode);

            String address = host.getAddress().getHostAddress() + ":" + host.getPort() + "\0";
            outputStream.write(address.getBytes());
            outputStream.write(filter.getBytes());

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
