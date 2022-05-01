package steam;

import steam.queries.Requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class GameServer extends SteamServer {

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

    private byte[] challenge;
    private boolean challengeValid;
    private ArrayList<Player> playerList;
    private HashMap<String, String> rules;


    public GameServer() {
        init();
    }

    public GameServer(String ip, int port) {
        this(new InetSocketAddress(ip, port), true);
    }

    public GameServer(InetSocketAddress host, boolean requestAll) {
        super(host);
        init();
        if (requestAll) {   // We only request server data when explicitly asked because all of these methods block
            try {
                requestAll();
            } catch (IOException e) {

            }
        }
    }

    public void init() {
        this.playerList = new ArrayList<Player>();
        this.challenge = new byte[4];
        this.rules = new HashMap<>();
        this.challengeValid = false;
    }

    /**
     * Requests all information for the server. It is recommended to only use this when initiating a GameServer object
     * because you do not need to update the challenge number. If you want to update the information for real-time data,
     * you should use requestInfo(), requestPlayers(), requestRules().
     * @throws IOException
     */
    public void requestAll() throws IOException {
        requestChallenge();
        requestInfo();
        requestPlayers();
        requestRules();
    }

    /**
     * Sends an information request to the game server. This method blocks until a response is received or until
     * the socket times out. If the packet is received it will parse the data and store it in this object.
     * @throws IOException
     */
    public void requestInfo() throws IOException {
        send(Requests.INFO(challenge));

        DatagramPacket recv = recieve(Requests.INFO_RESPONSE);
        if (recv != null) {
            parseInfo(recv);
        }
    }

    /**
     * Parses server information received by the game server and stores it in this GameServer object.
     * @param packet data received by the server
     * @throws IOException
     */
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

    /**
     * Requests a challenge number from the game server. This is a mandatory preliminary step before requesting the
     * player list or the rules from the server.
     * @return true is challenge was received, false otherwise
     * @throws IOException
     */
    public boolean requestChallenge() throws IOException {
        send(Requests.PLAYERS(Requests.HEADER));

        DatagramPacket recv = recieve(Requests.CHALLENGE_RESPONSE);
        if (recv != null) {
            SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(recv.getData()));
            sis.skipBytes(5);
            this.challenge[0] = sis.readByte();
            this.challenge[1] = sis.readByte();
            this.challenge[2] = sis.readByte();
            this.challenge[3] = sis.readByte();

            this.challengeValid = true;
        }

        return challengeValid;
    }

    /**
     * Sends a request to the game server for a list of players in the server. This method blocks until a response is
     * received or until the socket times out. If the packet is received it will parse the data and store it in this object.
     * @throws IOException
     */
    public void requestPlayers() throws IOException {
        if (!challengeValid) {
            System.out.println("ERROR: You must request a challenge number first, call requestChallenge()");
            return;
        }
        send(Requests.PLAYERS(challenge));

        DatagramPacket recv = recieve(Requests.PLAYERS_RESPONSE);
        if (recv != null) {
            parsePlayers(recv);
        }
    }

    /**
     * Parses player information received by the server and stores each player information in a Player object and
     * stores those Player objects in a List.
     * @param packet Packet received by server
     * @throws IOException
     */
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

    /**
     * Sends a request to the game server for a list of rules in the server. This method blocks until a response is
     * received or until the socket times out. If the packet is received it will parse the data and store it in this object.
     * @throws IOException
     */
    public void requestRules() throws IOException {
        if (!challengeValid) {
            System.out.println("ERROR: You must request a challenge number first, call requestChallenge()");
            return;
        }
        send(Requests.RULES(challenge));

        DatagramPacket recv = recieve(Requests.RULES_RESPONSE);
        if (recv != null) {
            parseRules(recv);
        }
    }

    /**
     * Parses rules information received by the server and stores each key/value pair in a HashMap.
     * @param packet rules data
     * @throws IOException
     */
    public void parseRules(DatagramPacket packet) throws IOException {
        SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(packet.getData()));
        sis.skipBytes(5);

        int num = sis.readSteamShort();
        for (int i = 0; i < num; i++) {
            rules.put(sis.readString(), sis.readString());
        }

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

}
