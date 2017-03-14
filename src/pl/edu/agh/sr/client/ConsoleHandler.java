package pl.edu.agh.sr.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * Created by Julia Samól on 14.03.2017.
 */
public class ConsoleHandler extends Thread {
    private Client client;
    private Socket socket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;

    public ConsoleHandler(Client client, Socket socket, DatagramSocket datagramSocket, MulticastSocket multicastSocket) {
        this.client = client;
        this.socket = socket;
        this.datagramSocket = datagramSocket;
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        PrintWriter out = null;
        InetAddress address = null;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in)
        );

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            address = InetAddress.getByName(client.getHost());

            out.println("\\u#" + client.getUsername());

            byte[] sendInfo = "\\info#".getBytes();
            DatagramPacket infoPacket = new DatagramPacket(sendInfo, sendInfo.length, address, client.getPort());
            datagramSocket.send(infoPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                String userInput = in.readLine();
                if (userInput == null || "\\quit".equals(userInput)) {
                    byte[] sendBuffer = "\\rm#".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, client.getPort());
                    datagramSocket.send(sendPacket);
                    client.interruptRunning();
                    return;
                }
                else if (userInput.startsWith("\\M")) {
                    if (userInput.length() > 2) {
                        byte[] sendBuffer = (client.getUsername() + ": " + userInput.substring(3)).getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, client.getPort());
                        datagramSocket.send(sendPacket);
                    }
                }
                else if (userInput.startsWith("\\N")) {
                    if (userInput.length() > 2) {
                        byte[] sendBuffer = (client.getUsername() + ": " + userInput.substring(3)).getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(client.getGroup()), client.getMulticastPort());
                        multicastSocket.send(sendPacket);
                    }
                }
                else
                    out.println(client.getUsername() + ": " + userInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
