package pl.edu.agh.sr.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Created by Julia Samól on 14.03.2017.
 */
public class UDPMulticastHandler extends Thread {
    private Client client;
    private MulticastSocket multicastSocket;

    public UDPMulticastHandler(Client client, MulticastSocket multicastSocket) {
        this.client = client;
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                multicastSocket.receive(receivePacket);
                String message = new String(receivePacket.getData());
                if (!message.startsWith(client.getUsername()))
                    System.out.println(message.split("#", 2)[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
