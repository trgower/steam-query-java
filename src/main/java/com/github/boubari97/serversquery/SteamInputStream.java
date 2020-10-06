package com.github.boubari97.serversquery;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SteamInputStream extends DataInputStream {
    public SteamInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public String readString() throws IOException {
        StringBuilder builder = new StringBuilder();
        byte b = readByte();
        while (b != 0x00) {
            builder.append((char) b);
            b = readByte();
        }
        return builder.toString();
    }

    public int readSteamLong() throws IOException {
        return Integer.reverseBytes(readInt());
    }

    public float readSteamFloat() throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(readInt()));
    }

    public int readSteamShort() throws IOException {
        return Short.reverseBytes(readShort());
    }

}
