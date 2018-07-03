package steam;

import steam.queries.Region;
import steam.queries.Requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class MasterServer extends SteamServer {

    private HashMap<InetSocketAddress, GameServer> gameServers;
    private final InetSocketAddress fin = new InetSocketAddress("0.0.0.0", 0);

    public MasterServer() {
        super(new InetSocketAddress("hl2master.steampowered.com", 27011));
        gameServers = new HashMap<>();
    }

    /**
     * requestServers
     * @param filter string that defines a filter for the query
     * @throws IOException
     */
    public void requestServers(String filter) throws IOException {
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

    public GameServer getServer(String ip, int port) {
        return gameServers.get(new InetSocketAddress(ip, port));
    }

    public HashMap<InetSocketAddress, GameServer> getGameServers() {
        return gameServers;
    }
}
