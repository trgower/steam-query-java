package steam;

import steam.queries.Requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.*;

public class GameServer extends SteamServer {

    private byte protocol;
    private String name;
    private String map;
    private String folder;
    private String game;
    private int appId;
    private int players;
    private int maxPlayers;
    private int bots;
    private char type;
    private char env;
    private byte visibility;
    private byte vac;
    private String version;
    private byte edf;
    private int gamePort;
    private Long steamId;
    private int sourceTVPort;
    private String sourceTVName;
    private String descTags;
    private Long gameId;

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

    public GameServer(InetSocketAddress host, boolean isNeedToRequestAll) {
        super(host);
        init();
        if (isNeedToRequestAll) {   // We only request server data when explicitly asked because all of these methods block
            try {
                requestAll();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public void init() {
        this.playerList = new ArrayList<>();
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
        updateServerData();
        requestChallenge();
    }

    public void updateServerData() throws IOException{
        requestInfo();
        requestPlayers();
        requestRules();
    }

    /**
     * Sends an information request to the game server. This method blocks until a response is received or until
     * the socket times out. If the packet is received it will parse the data and store it in this object.
     * @throws IOException
     */
    private void requestInfo() throws IOException {
        send(Requests.infoRequest());
        DatagramPacket receive = receive(Requests.INFO_RESPONSE);
        if (receive != null) {
            parseInfo(receive);
        }
    }

    /**
     * Parses server information received by the game server and stores it in this GameServer object.
     * @param packet data received by the server
     * @throws IOException
     */
    public void parseInfo(DatagramPacket packet) throws IOException {
        SteamInputStream inputStream = new SteamInputStream(new ByteArrayInputStream(packet.getData()));
        inputStream.skipBytes(5);

        this.protocol = inputStream.readByte();
        this.name = inputStream.readString();
        this.map = inputStream.readString();
        this.folder = inputStream.readString();
        this.game = inputStream.readString();
        this.appId = inputStream.readSteamShort();
        this.players = inputStream.read();
        this.maxPlayers = inputStream.read();
        this.bots = inputStream.read();
        this.type = (char) inputStream.read();
        this.env = (char) inputStream.read();
        this.visibility = inputStream.readByte();
        this.vac = inputStream.readByte();
        this.version = inputStream.readString();
        this.edf = inputStream.readByte();

        if ((edf & 0x80) > 0) {
            this.gamePort = inputStream.readSteamShort();
        }
        if ((edf & 0x10) > 0) {
            this.steamId = inputStream.readLong();
        }
        if ((edf & 0x40) > 0) {
            this.sourceTVPort = inputStream.readSteamShort();
            this.sourceTVName = inputStream.readString();
        }
        if ((edf & 0x20) > 0) {
            this.descTags = inputStream.readString();
        }
        if ((edf & 0x01) > 0) {
            this.gameId = inputStream.readLong();
        }

    }

    /**
     * Requests a challenge number from the game server. This is a mandatory preliminary step before requesting the
     * player list or the rules from the server.
     * @return true is challenge was received, false otherwise
     * @throws IOException
     */
    private void requestChallenge() throws IOException {
        send(Requests.playersRequest(Requests.HEADER));
        DatagramPacket receive = receive(Requests.CHALLENGE_RESPONSE);
        if (receive != null) {
            SteamInputStream inputStream = new SteamInputStream(new ByteArrayInputStream(receive.getData()));
            inputStream.skipBytes(5);
            this.challenge[0] = inputStream.readByte();
            this.challenge[1] = inputStream.readByte();
            this.challenge[2] = inputStream.readByte();
            this.challenge[3] = inputStream.readByte();

            this.challengeValid = true;
        }
    }

    /**
     * Sends a request to the game server for a list of players in the server. This method blocks until a response is
     * received or until the socket times out. If the packet is received it will parse the data and store it in this object.
     * @throws IOException
     */
    private void requestPlayers() throws IOException {
        if (!challengeValid) {
            return; // Call requestChallenge() first
        }
        send(Requests.playersRequest(challenge));
        DatagramPacket receive = receive(Requests.PLAYERS_RESPONSE);
        if (receive != null) {
            parsePlayers(receive);
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
    private void requestRules() throws IOException {
        if (!challengeValid) {
            return; // Call requestChallenge() first
        }
        send(Requests.rulesRequest(challenge));
        DatagramPacket receive = receive(Requests.RULES_RESPONSE);
        if (receive != null) {
            parseRules(receive);
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

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public InetSocketAddress getHost() {
        return host;
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

    public int getAppId() {
        return appId;
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

    public byte getEdf() {
        return edf;
    }

    public int getGamePort() {
        return gamePort;
    }

    public Long getSteamId() {
        return steamId;
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

    public Long getGameId() {
        return gameId;
    }

}
