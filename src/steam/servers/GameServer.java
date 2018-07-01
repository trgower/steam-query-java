package steam.servers;

import steam.Tools;
import steam.queries.SourceQuery;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class GameServer {

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

    private DatagramSocket socket;
    private ArrayList<Player> playerList;
    private byte[] challenge;
    private boolean challengeRecieved;

    private boolean loaded = false;


    public GameServer(String ip, int port) {
        this(new InetSocketAddress(ip, port));
    }

    public GameServer(InetSocketAddress host) {
        this.host = host;
        this.playerList = new ArrayList<Player>();
        this.challenge = new byte[4];
        this.challengeRecieved = false;

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
            socket.setTrafficClass(0x04); // set transmission class to reliable. We don't care about speed
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            requestInfo();
            requestChallenge();
            requestPlayers();
        } catch (IOException e) {

        }
    }

    public void requestInfo() throws IOException {
        byte[] q = SourceQuery.A2S_INFO();
        DatagramPacket packet = new DatagramPacket(q, q.length, host.getAddress(), host.getPort());
        socket.send(packet);

        DatagramPacket recv = recieve(SourceQuery.INFO_RESPONSE);
        if (recv != null) {
            parseInfo(recv);
        }
    }

    public void parseInfo(DatagramPacket packet) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.getData()));
        dis.skipBytes(5);

        this.protocol = dis.readByte();
        this.name = Tools.readString(dis);
        this.map = Tools.readString(dis);
        this.folder = Tools.readString(dis);
        this.game = Tools.readString(dis);
        this.appid = dis.readUnsignedShort();
        this.players = dis.read();
        this.maxPlayers = dis.read();
        this.bots = dis.read();
        this.type = (char) dis.read();
        this.env = (char) dis.read();
        this.visibility = dis.readByte();
        this.vac = dis.readByte();
        this.version = Tools.readString(dis);
        this.EDF = dis.readByte();

        if ((EDF & 0x80) > 0) {
            this.gamePort = Short.reverseBytes(dis.readShort());
        }
        if ((EDF & 0x10) > 0) {
            this.steamid = dis.readLong();
        }
        if ((EDF & 0x40) > 0) {
            this.sourceTVPort = dis.readUnsignedShort();
            this.sourceTVName = Tools.readString(dis);
        }
        if ((EDF & 0x20) > 0) {
            this.descTags = Tools.readString(dis);
        }
        if ((EDF & 0x01) > 0) {
            this.gameid = dis.readLong();
        }

        loaded = true;

    }

    public boolean requestChallenge() throws IOException {
        if (challengeRecieved) return true;

        byte[] req = SourceQuery.A2S_PLAYER(SourceQuery.HEADER); // Send 0xFFFFFFFF as challenge number to request a challenge
        DatagramPacket packet = new DatagramPacket(req, req.length, host.getAddress(), host.getPort());
        socket.send(packet);

        DatagramPacket recv = recieve(SourceQuery.CHALLENGE_RESPONSE);
        if (recv != null) {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recv.getData()));
            dis.skipBytes(5);
            this.challenge[0] = dis.readByte();
            this.challenge[1] = dis.readByte();
            this.challenge[2] = dis.readByte();
            this.challenge[3] = dis.readByte();

            challengeRecieved = true;
        }

        return challengeRecieved;
    }

    public void requestPlayers() throws IOException {
        byte[] req = SourceQuery.A2S_PLAYER(challenge);
        DatagramPacket packet = new DatagramPacket(req, req.length, host.getAddress(), host.getPort());
        socket.send(packet);

        DatagramPacket recv = recieve(SourceQuery.PLAYERS_LIST);
        if (recv != null) {
            parsePlayers(recv);
        }
    }

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

    public DatagramPacket recieve(byte expected) {
        byte[] buf = new byte[4096];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(recv);
        } catch (IOException e) {
            System.out.println(host.getAddress().getHostAddress() + ":" + host.getPort() + " did not respond.");
            return null;
        }

        if (recv.getLength() > 0 && recv.getData()[4] != expected) {
            System.out.println("ERROR: wrong packet received, expected " + Tools.byteToHex(expected));
            return null;
        }

        return recv;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
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
