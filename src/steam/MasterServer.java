package steam;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;

public class MasterServer {

    private String server;
    private DatagramSocket socket;

    public MasterServer(String serverHost) {
        this.server = serverHost;
        try {
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(15000);
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

            // Create query
            MasterQuery query = new MasterQuery(RegionCode.EVERYWHERE, last, filter + "\0");

            // Send query to master server
            byte[] sendBuf = encodeQuery(query);
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


    public byte[] encodeQuery(MasterQuery query) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write((byte) 0x31); // Tells the master server that this is a query (?) ...it's an opcode...
        baos.write(query.getRegionCode());

        String addr = query.getHost().getAddress().getHostAddress() + ":" + query.getHost().getPort() + "\0";
        try {
            baos.write(addr.getBytes());
            baos.write(query.getFilter().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return baos.toByteArray();
    }

    public ArrayList<InetSocketAddress> parseResponse(DatagramPacket recv) throws IOException {
        ArrayList<InetSocketAddress> list = new ArrayList<>();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recv.getData()));

        int headerWidth = SteamResponse.MASTER_SERVER_LIST.length;
        dis.skipBytes(headerWidth); // TODO: check if the first 6 bytes contain the correct code

        int len = recv.getLength();
        for (int i = headerWidth; i < len; i += 6) { // each ip + port pair is 6 bytes long
            list.add(new InetSocketAddress(InetAddress.getByAddress(
                    new byte[] {dis.readByte(), dis.readByte(), dis.readByte(), dis.readByte()}),
                    dis.readUnsignedShort()));
        }

        return list;
    }

}
