package pl.edu.agh.sr.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Julia Samól on 14.03.2017.
 */
public class UDPMulticastThread extends Thread {
    private Server server;
    private String group;
    private int port;

    InetAddress groupAddress;

    private MulticastSocket multicastSocket;

    public UDPMulticastThread(Server server, String group, int port) {
        this.server = server;
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        setName("Thread-MulticastUDP");
        multicastSocket = null;
        groupAddress = null;
        try {
            multicastSocket = new MulticastSocket(port);
            groupAddress = InetAddress.getByName(group);
            multicastSocket.joinGroup(groupAddress);

            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                multicastSocket.receive(receivePacket);

                String message = new String(receivePacket.getData());
                server.log(getName() + "| Received message " + message);
            }
        } catch (Exception e) {
            server.log(getName() + "| Exception caught: " + e + ".");
        } finally {
            if (multicastSocket != null)
                try {
                    multicastSocket.leaveGroup(groupAddress);
                } catch (IOException e) {
                    server.log(getName() + "| Error while leaving the group (IOException caught: )" + e + ").");
                }
        }
    }

    @Override
    public void interrupt() {
        if (multicastSocket != null)
            try {
                multicastSocket.leaveGroup(groupAddress);
            } catch (IOException e) {
                server.log(getName() + "| Error while leaving the group (IOException caught: )" + e + ").");
            }
    }
}
