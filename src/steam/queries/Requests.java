package steam.queries;

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

    public static byte[] INFO(byte[] challenge) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HEADER);
        baos.write(INFO_CODE);
        try {
            baos.write(INFO_MSG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        baos.write(0x00);
        baos.write(challenge);

        return baos.toByteArray();
    }

    public static byte[] PLAYERS(byte[] challenge) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HEADER);
        baos.write(PLAYERS_CODE);
        baos.write(challenge);

        return baos.toByteArray();
    }

    public static byte[] RULES(byte[] challenge) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HEADER);
        baos.write(RULES_CODE);
        baos.write(challenge);

        return baos.toByteArray();
    }

    public static byte[] MASTER(byte regionCode, InetSocketAddress host, String filter) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(MASTER_CODE); // Tells the master server that this is a query (?) ...it's an opcode...
        baos.write(regionCode);

        String addr = host.getAddress().getHostAddress() + ":" + host.getPort() + "\0";
        try {
            baos.write(addr.getBytes());
            baos.write(filter.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return baos.toByteArray();
    }

}
