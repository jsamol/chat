package pl.edu.agh.sr.server;

import java.net.InetAddress;

/**
 * Created by Julia Samól on 14.03.2017.
 */
public class UDPData {
    private InetAddress address;
    private int port;

    public UDPData(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
