package steam;

import tools.Tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public abstract class SteamServer {

    protected InetSocketAddress host;
    protected DatagramSocket socket;

    public SteamServer() {

    }

    public SteamServer(InetSocketAddress host) {
        this.host = host;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
            socket.setTrafficClass(0x04); // set transmission class to reliable. We don't care about speed
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    protected DatagramPacket recieve(byte expected) {
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

}
