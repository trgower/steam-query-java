package com.github.boubari97.serversquery;

import com.github.boubari97.serversquery.tools.Tools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public abstract class SteamServer {

    protected InetSocketAddress host;
    protected DatagramSocket socket;
    private long lastResponse;
    private long responseLatency;
    private long lastSent;

    public SteamServer() {

    }

    public SteamServer(InetSocketAddress host) {
        this.host = host;
        this.lastResponse = -1;
        this.responseLatency = -1;
        this.lastSent = -1;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
            socket.setTrafficClass(0x04); // set transmission class to reliable. We don't care about speed
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Blocks execution until it receives a packet with a 3 second timeout
     * @param expected the expected opcode
     * @return Packet received by the server
     */
    protected DatagramPacket receive(byte expected) {
        byte[] buf = new byte[4096];
        DatagramPacket receive = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(receive);
        } catch (IOException e) {
            String message = host.getAddress().getHostAddress() + ":" + host.getPort() + " did not respond.\n";
            throw new RuntimeException(message + e);
        }

        if (receive.getLength() > 0 && receive.getData()[4] != expected) {
            String message = "ERROR: wrong packet received, expected 0x" + Tools.byteToHex(expected);
            throw new RuntimeException(message);
        }

        lastResponse = System.currentTimeMillis();
        responseLatency = lastResponse - lastSent;
        return receive;
    }

    protected void send(byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, host);
        lastSent = System.currentTimeMillis();
        socket.send(packet);
    }

    public long getLastResponse() {
        return lastResponse;
    }

    public long getResponseLatency() {
        return responseLatency;
    }

    public long getLastSent() {
        return lastSent;
    }
}
