package com.github.boubari97.serversquery;

import com.github.boubari97.serversquery.queries.RegionConst;
import com.github.boubari97.serversquery.queries.Requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class MasterServer extends SteamServer {

    private static final String DEFAULT_HOSTNAME = "0.0.0.0";
    private static final int DEFAULT_PORT = 0;
    private final Map<InetSocketAddress, GameServer> gameServers;
    private final InetSocketAddress fin = new InetSocketAddress(DEFAULT_HOSTNAME, DEFAULT_PORT);

    public MasterServer() {
        super(new InetSocketAddress("hl2master.steampowered.com", 27011));
        gameServers = new HashMap<>();
    }

    /**
     * Request game servers based on the filter given. This does not request information for each game server. You need
     * to do that with requestServerData().
     * @param filter string that defines a filter for the query
     * @return the hash map of game servers in this MasterServer object. Can be retrieved using getGameServers()
     * @throws IOException throws in socket.send(packet) by DatagramSocket class
     */
    public Map<InetSocketAddress, GameServer> requestServers(String filter) throws IOException {
        boolean finished = false;
        InetSocketAddress last = new InetSocketAddress(DEFAULT_HOSTNAME, DEFAULT_PORT);
        while (!finished) {
            // We send queries until the last IP read is 0.0.0.0 and port it 0. That is the Master Server's way of
            // telling us that the list is complete.

            // Send query to master server
            byte[] sendBuf = Requests.masterRequest(RegionConst.EVERYWHERE, last, filter + "\0");
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, host);
            socket.send(packet);

            DatagramPacket receive = receive(Requests.MASTER_RESPONSE);
            if (receive != null) {
                last = parseResponse(receive); // Save the last IP read
                if (last.equals(fin)) {
                    finished = true;
                }
            }
        }
        return gameServers;
    }

    /**
     * Parses the list of game servers returned by the Master server.
     * @param datagramPacket packet received that needs to be parsed
     * @return the last InetSocketAddress read which is used to retrieve the next page of servers
     * @throws IOException in skipBytes() by SteamInputStream class
     */
    public InetSocketAddress parseResponse(DatagramPacket datagramPacket) throws IOException {
        InetSocketAddress last = new InetSocketAddress(DEFAULT_HOSTNAME, DEFAULT_PORT);
        SteamInputStream inputStream = new SteamInputStream(new ByteArrayInputStream(datagramPacket.getData()));
        inputStream.skipBytes(6); // The first 6 bytes are not a part of the server list

        int len = datagramPacket.getLength();
        for (int i = 6; i < len; i += 6) { // each ip + port pair is 6 bytes long
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByAddress(new byte[] {inputStream.readByte(), inputStream.readByte(), inputStream.readByte(), inputStream.readByte()}), inputStream.readUnsignedShort());
            if (address.getPort() != 0) gameServers.putIfAbsent(address, new GameServer(address, false));
            last = address;
        }

        return last;
    }

    /**
     * Iterates through all servers and requests their information, players, and rules asynchronously. This method can
     * take a very long time especially if every server doesn't respond. Each socket is set to a 3 second timeout.
     * @throws IOException in requestAll()
     */
    public void requestServerData() throws IOException {
        for (Map.Entry<InetSocketAddress, GameServer> inetSocketAddressGameServerEntry : gameServers.entrySet()) {
            inetSocketAddressGameServerEntry.getValue().requestAll();
        }
    }

    /**
     * Iterates through all servers and updates their information, players, and rules asynchronously. This method does
     * not request a challenge number. This method can take a very long time especially if every server doesn't respond.
     * Each socket is set to a 3 second timeout.
     * @throws IOException in updateServerData()
     */
    public void updateServerData() throws IOException {
        for (Map.Entry<InetSocketAddress, GameServer> pair : gameServers.entrySet()) {
            pair.getValue().updateServerData();
        }
    }

    public GameServer getServer(String ip, int port) {
        return gameServers.get(new InetSocketAddress(ip, port));
    }

    public Map<InetSocketAddress, GameServer> getGameServers() {
        return gameServers;
    }
}
