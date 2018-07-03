package steam;

import steam.queries.Region;
import steam.queries.Requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MasterServer extends SteamServer {

    private HashMap<InetSocketAddress, GameServer> gameServers;
    private final InetSocketAddress fin = new InetSocketAddress("0.0.0.0", 0);

    public MasterServer() {
        super(new InetSocketAddress("hl2master.steampowered.com", 27011));
        gameServers = new HashMap<>();
    }

    /**
     * Request game servers based on the filter given. This does not request information for each game server. You need
     * to do that with requestServerData().
     * @param filter string that defines a filter for the query
     * @return the hash map of game servers in this MasterServer object. Can be retrieved using getGameServers()
     * @throws IOException
     */
    public HashMap<InetSocketAddress, GameServer> requestServers(String filter) throws IOException {
        boolean finished = false;
        InetSocketAddress last = new InetSocketAddress("0.0.0.0", 0);
        while (!finished) {
            // We send queries until the last IP read is 0.0.0.0 and port it 0. That is the Master Server's way of
            // telling us that the list is complete.

            // Send query to master server
            byte[] sendBuf = Requests.MASTER(Region.EVERYWHERE, last, filter + "\0");
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, host);
            socket.send(packet);

            DatagramPacket recv = recieve(Requests.MASTER_RESPONSE);
            if (recv != null) {
                last = parseResponse(recv);     // Save the last IP read
                if (last.equals(fin)) {
                    finished = true;
                }
            }
        }
        return gameServers;
    }

    /**
     * @param recv packet received that needs to be parsed
     * @return the last InetSocketAddress read
     * @throws IOException
     */
    public InetSocketAddress parseResponse(DatagramPacket recv) throws IOException {
        InetSocketAddress last = new InetSocketAddress("0.0.0.0", 0);
        SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(recv.getData()));
        sis.skipBytes(6); // The first 6 bytes are not a part of the server list

        int len = recv.getLength();
        for (int i = 6; i < len; i += 6) { // each ip + port pair is 6 bytes long
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(new byte[] {sis.readByte(), sis.readByte(), sis.readByte(), sis.readByte()}), sis.readUnsignedShort());
            if (addr.getPort() != 0) gameServers.putIfAbsent(addr, new GameServer(addr, false));
            last = addr;
        }

        return last;
    }

    /**
     * Iterates through all servers and requests their information, players, and rules asynchronously. This method can
     * take a very long time especially if every server doesn't respond. Each socket is set to a 3 second timeout.
     * @throws IOException
     */
    public void requestServerData() throws IOException {
        Iterator it = gameServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<InetSocketAddress, GameServer> pair = (Map.Entry) it.next();
            pair.getValue().requestAll();
        }
    }

    public GameServer getServer(String ip, int port) {
        return gameServers.get(new InetSocketAddress(ip, port));
    }

    public HashMap<InetSocketAddress, GameServer> getGameServers() {
        return gameServers;
    }
}
