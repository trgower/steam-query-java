package steam.queries;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SourceQuery {

    public static byte[] HEADER = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    public static byte INFO_CODE = 0x54;
    public static byte PLAYERS_CODE = 0x55;
    public static byte RULES_CODE = 0x56;
    public static byte CHALLENGE_RESPONSE = 0x41;
    public static byte INFO_RESPONSE = 0x49;
    public static byte PLAYERS_LIST = 0x44;

    
    public static byte[] A2S_INFO() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HEADER);
        baos.write(INFO_CODE);
        try {
            baos.write("Source Engine Query".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        baos.write(0x00);

        return baos.toByteArray();
    }

    public static byte[] A2S_PLAYER(byte[] challenge) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HEADER);
        baos.write(PLAYERS_CODE);
        baos.write(challenge);

        return baos.toByteArray();
    }

    public static byte[] A2S_RULES() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HEADER);
        baos.write(RULES_CODE);

        return baos.toByteArray();
    }
}
