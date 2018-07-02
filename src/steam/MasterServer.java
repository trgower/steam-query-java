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
    private InetSocketAddress fin = new InetSocketAddress("0.0.0.0", 0);

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
            // Send query to master server
            byte[] sendBuf = Requests.MASTER(Region.EVERYWHERE, last, filter + "\0");
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, host);
            socket.send(packet);

            DatagramPacket recv = recieve(Requests.MASTER_RESPONSE);
            if (recv != null) {
                last = parseResponse(recv);
                if (last.equals(fin)) {
                    finished = true;
                }
            }
        }
    }

    public InetSocketAddress parseResponse(DatagramPacket recv) throws IOException {
        InetSocketAddress last = new InetSocketAddress("0.0.0.0", 0);
        SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(recv.getData()));
        sis.skipBytes(6);

        int len = recv.getLength();
        for (int i = 6; i < len; i += 6) { // each ip + port pair is 6 bytes long
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(new byte[] {sis.readByte(), sis.readByte(), sis.readByte(), sis.readByte()}), sis.readUnsignedShort());
            if (addr.getPort() != 0) gameServers.put(addr, new GameServer(addr, false));
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
