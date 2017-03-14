package pl.edu.agh.sr.server;

import pl.edu.agh.sr.client.UDPData;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julia Samól on 13.03.2017.
 */
public class UDPThread extends Thread {
    private Server server;
    private List<UDPData> clients;

    private int port;
    private DatagramSocket datagramSocket = null;

    public UDPThread(Server server, int port) {
        this.server = server;
        this.port = port;

        this.clients = new ArrayList<>();
    }

    @Override
    public void run() {
        setName("Thread-UDP");
        try {
            datagramSocket = new DatagramSocket(port);
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                datagramSocket.receive(receivePacket);

                String message = new String(receivePacket.getData());
                server.log(getName() + "| Received message: '" + message + "'.");
                if (message.startsWith("\\info#")) {
                    UDPData client = new UDPData(receivePacket.getAddress(), receivePacket.getPort());
                    clients.add(client);
                    server.log(getName() + "| UDP client added.");
                }
                else if (message.startsWith("\\rm#")) {
                    clients.remove(new UDPData(receivePacket.getAddress(), receivePacket.getPort()));
                    server.log(getName() + "| UDP client removed.");
                }
                else {
                    for (UDPData client : clients) {
                        if (!client.getAddress().equals(receivePacket.getAddress()) || client.getPort() != receivePacket.getPort()) {
                            DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getData().length, client.getAddress(), client.getPort());
                            datagramSocket.send(sendPacket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null)
                datagramSocket.close();
        }
    }
}