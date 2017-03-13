package pl.edu.agh.sr.server;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Julia Samól on 13.03.2017.
 */
public class UDPServerThread extends Thread {
    private Server server;
    private int port;
    private DatagramSocket datagramSocket = null;

    public UDPServerThread(Server server, int port) {
        this.server = server;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {

    }
}
