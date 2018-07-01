package steam.servers;

import steam.SteamInputStream;
import steam.Tools;
import steam.queries.SourceQuery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

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
    private HashMap<String, String> rules;

    private boolean infoRecieved;
    private boolean playersRecieved;
    private boolean rulesRecieved;
    private boolean challengeRecieved;

    public GameServer() {
        this.playerList = new ArrayList<Player>();
        this.challenge = new byte[4];
        this.challengeRecieved = false;
        this.infoRecieved = false;
        this.playersRecieved = false;
        this.rulesRecieved = false;
        this.rules = new HashMap<>();
    }

    public GameServer(String ip, int port) {
        this(new InetSocketAddress(ip, port));
    }

    public GameServer(InetSocketAddress host) {
        this();

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
            infoRecieved = true;
        }
    }

    public void parseInfo(DatagramPacket packet) throws IOException {
        SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(packet.getData()));
        sis.skipBytes(5);

        this.protocol = sis.readByte();
        this.name = sis.readString();
        this.map = sis.readString();
        this.folder = sis.readString();
        this.game = sis.readString();
        this.appid = sis.readSteamShort();
        this.players = sis.read();
        this.maxPlayers = sis.read();
        this.bots = sis.read();
        this.type = (char) sis.read();
        this.env = (char) sis.read();
        this.visibility = sis.readByte();
        this.vac = sis.readByte();
        this.version = sis.readString();
        this.EDF = sis.readByte();

        if ((EDF & 0x80) > 0) {
            this.gamePort = sis.readSteamShort();
        }
        if ((EDF & 0x10) > 0) {
            this.steamid = sis.readLong();
        }
        if ((EDF & 0x40) > 0) {
            this.sourceTVPort = sis.readSteamShort();
            this.sourceTVName = sis.readString();
        }
        if ((EDF & 0x20) > 0) {
            this.descTags = sis.readString();
        }
        if ((EDF & 0x01) > 0) {
            this.gameid = sis.readLong();
        }

    }

    public boolean requestChallenge() throws IOException {
        if (challengeRecieved) return true;

        byte[] req = SourceQuery.A2S_PLAYER(SourceQuery.HEADER); // Send 0xFFFFFFFF as challenge number to request a challenge
        DatagramPacket packet = new DatagramPacket(req, req.length, host.getAddress(), host.getPort());
        socket.send(packet);

        DatagramPacket recv = recieve(SourceQuery.CHALLENGE_RESPONSE);
        if (recv != null) {
            SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(recv.getData()));
            sis.skipBytes(5);
            this.challenge[0] = sis.readByte();
            this.challenge[1] = sis.readByte();
            this.challenge[2] = sis.readByte();
            this.challenge[3] = sis.readByte();

            challengeRecieved = true;
        }

        return challengeRecieved;
    }

    public void requestPlayers() throws IOException {
        byte[] req = SourceQuery.A2S_PLAYER(challenge);
        DatagramPacket packet = new DatagramPacket(req, req.length, host.getAddress(), host.getPort());
        socket.send(packet);

        DatagramPacket recv = recieve(SourceQuery.PLAYERS_RESPONSE);
        if (recv != null) {
            parsePlayers(recv);
            playersRecieved = true;
        }
    }

    public void parsePlayers(DatagramPacket packet) throws IOException {
        SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(packet.getData()));
        sis.skipBytes(5);

        int num = sis.readByte();
        for (int i = 0; i < num; i++) {
            playerList.add(new Player(
                    sis.readByte(),
                    sis.readString(),
                    sis.readSteamLong(),
                    sis.readSteamFloat()));
        }

    }

    public void requestRules() throws IOException {
        byte[] req = SourceQuery.A2S_RULES(challenge);
        DatagramPacket packet = new DatagramPacket(req, req.length, host.getAddress(), host.getPort());
        socket.send(packet);

        DatagramPacket recv = recieve(SourceQuery.RULES_RESPONSE);
        if (recv != null) {
            parseRules(recv);
            rulesRecieved = true;
        }
    }

    public void parseRules(DatagramPacket packet) throws IOException {
        SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(packet.getData()));
        sis.skipBytes(5);

        int num = sis.readSteamShort();
        for (int i = 0; i < num; i++) {
            rules.put(sis.readString(), sis.readString());
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
            System.out.println("ERROR: wrong packet received, expected 0x" + Tools.byteToHex(expected));
            return null;
        }

        return recv;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public HashMap<String, String> getRules() {
        return rules;
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

    public boolean isInfoRecieved() {
        return infoRecieved;
    }

    public boolean isPlayersRecieved() {
        return playersRecieved;
    }

    public boolean isRulesRecieved() {
        return rulesRecieved;
    }

    public boolean isChallengeRecieved() {
        return challengeRecieved;
    }
}
