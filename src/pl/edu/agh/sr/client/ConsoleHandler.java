package pl.edu.agh.sr.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Julia Samól on 14.03.2017.
 */
public class ConsoleHandler extends Thread {
    private Client client;
    private Socket socket;
    private DatagramSocket datagramSocket;

    public ConsoleHandler(Client client, Socket socket, DatagramSocket datagramSocket) {
        this.client = client;
        this.socket = socket;
        this.datagramSocket = datagramSocket;
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
            address = InetAddress.getByName("localhost");

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
                    byte[] sendBuffer = (client.getUsername() + ": " + userInput.substring(3)).getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, client.getPort());
                    datagramSocket.send(sendPacket);
                }
                else if (userInput.startsWith("\\N")) {

                }
                else
                    out.println(client.getUsername() + ": " + userInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
