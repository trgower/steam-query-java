package steam.servers;

import steam.queries.SourceQuery;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class SourceServer {

    private InetSocketAddress host;
    private byte protocol;
    private String name;
    private String map;
    private String folder;
    private String game;
    private int appid;
    private int players;
    private int maxPlayers;
    private int bots;
    private char type;
    private char env;
    private byte visibility;
    private byte vac;
    private String version;
    private byte EDF;
    private int gamePort;
    private Long steamid;
    private int sourceTVPort;
    private String sourceTVName;
    private String descTags;
    private Long gameid;


    public SourceServer(InetSocketAddress host) {
        this.host = host;

        try {
            requestInfo();
        } catch (IOException e) {

        }
    }

    public void requestInfo() throws IOException {
        byte[] q = SourceQuery.A2S_INFO();
        DatagramPacket packet = new DatagramPacket(q, q.length, host.getAddress(), host.getPort());
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);

        byte[] buf = new byte[4096];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        socket.receive(recv);

        if (recv.getLength() > 0 && recv.getData()[4] == SourceQuery.INFO_RESPONSE)
            parseInfo(recv);
        else
            System.out.println("ERROR: wrong packet received, expected INFO_RESPONSE");
    }

    public void parseInfo(DatagramPacket packet) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.getData()));
        dis.skipBytes(5);

        this.protocol = dis.readByte();
        this.name = readString(dis);
        this.map = readString(dis);
        this.folder = readString(dis);
        this.game = readString(dis);
        this.appid = dis.readUnsignedShort();
        this.players = dis.read();
        this.maxPlayers = dis.read();
        this.bots = dis.read();
        this.type = (char) dis.read();
        this.env = (char) dis.read();
        this.visibility = dis.readByte();
        this.vac = dis.readByte();
        this.version = readString(dis);
        this.EDF = dis.readByte();

        if ((EDF & 0x80) > 0) {
            this.gamePort = Short.reverseBytes(dis.readShort()); // different byte ordering?
        }
        if ((EDF & 0x10) > 0) {
            this.steamid = dis.readLong();
        }
        if ((EDF & 0x40) > 0) {
            this.sourceTVPort = dis.readUnsignedShort();
            this.sourceTVName = readString(dis);
        }
        if ((EDF & 0x20) > 0) {
            this.descTags = readString(dis);
        }
        if ((EDF & 0x01) > 0) {
            this.gameid = dis.readLong();
        }

    }

    public String readString(DataInputStream dis) throws IOException {
        String res = "";
        byte b = dis.readByte();
        while (b != 0x00) {
            res += (char) b;
            b = dis.readByte();
        }

        return res;
    }

    public InetSocketAddress getHost() {
        return host;
    }

    public void setHost(InetSocketAddress host) {
        this.host = host;
    }

    public byte getProtocol() {
        return protocol;
    }

    public String getName() {
        return name;
    }

    public String getMap() {
        return map;
    }

    public String getFolder() {
        return folder;
    }

    public String getGame() {
        return game;
    }

    public int getAppid() {
        return appid;
    }

    public int getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getBots() {
        return bots;
    }

    public char getType() {
        return type;
    }

    public char getEnv() {
        return env;
    }

    public byte getVisibility() {
        return visibility;
    }

    public byte getVac() {
        return vac;
    }

    public String getVersion() {
        return version;
    }

    public byte getEDF() {
        return EDF;
    }

    public int getGamePort() {
        return gamePort;
    }

    public Long getSteamid() {
        return steamid;
    }

    public int getSourceTVPort() {
        return sourceTVPort;
    }

    public String getSourceTVName() {
        return sourceTVName;
    }

    public String getDescTags() {
        return descTags;
    }

    public Long getGameid() {
        return gameid;
    }
}
