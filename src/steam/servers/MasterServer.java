package steam.servers;

import steam.SteamInputStream;
import steam.SteamResponse;
import steam.queries.Region;
import steam.queries.Requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;

public class MasterServer {

    private final String server = "hl2master.steampowered.com";
    private DatagramSocket socket;

    public MasterServer() {
        try {
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(15000);
            this.socket.setTrafficClass(0x04);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getAllServers
     * @return HashSet of all servers listed on the steam master server.
     * @throws IOException
     */
    public HashSet<InetSocketAddress> getAllServers() throws IOException {
        return getServers("");
    }

    /**
     * getServers
     * @param filter string that defines a filter for the query
     * @return HashSet of all servers listed on the steam master server.
     * @throws IOException
     */
    public HashSet<InetSocketAddress> getServers(String filter) throws IOException {

        InetSocketAddress fin = new InetSocketAddress(InetAddress.getByAddress(new byte[] {0x00, 0x00, 0x00, 0x00}), 0x0000);
        InetSocketAddress last = new InetSocketAddress(InetAddress.getByAddress(new byte[] {0x00, 0x00, 0x00, 0x00}), 0x0000);
        boolean finished = false;

        HashSet<InetSocketAddress> all = new HashSet<>();

        while (!finished) {
            // Send query to master server
            byte[] sendBuf = Requests.MASTER(Region.EVERYWHERE, last, filter + "\0");
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getByName(server), 27011);
            socket.send(packet);

            // Recieve response
            byte[] recvBuf = new byte[4096]; // magic buffer size
            DatagramPacket recv = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(recv); // BLOCKS: will timeout after 15 seconds

            ArrayList<InetSocketAddress> parsed = parseResponse(recv);
            last = parsed.get(parsed.size() - 1);

            if (last.equals(fin)) {
                finished = true;
                parsed.remove(last);
            }

            all.addAll(parsed);
        }

        return all;

    }

    public ArrayList<InetSocketAddress> parseResponse(DatagramPacket recv) throws IOException {
        ArrayList<InetSocketAddress> list = new ArrayList<>();

        SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(recv.getData()));

        int headerWidth = SteamResponse.MASTER_SERVER_LIST.length;
        sis.skipBytes(headerWidth); // TODO: check if the first 6 bytes contain the correct code

        int len = recv.getLength();
        for (int i = headerWidth; i < len; i += 6) { // each ip + port pair is 6 bytes long
            list.add(new InetSocketAddress(InetAddress.getByAddress(
                    new byte[] {sis.readByte(), sis.readByte(), sis.readByte(), sis.readByte()}),
                    sis.readUnsignedShort()));
        }

        return list;
    }

}
