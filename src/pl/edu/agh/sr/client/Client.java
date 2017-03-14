package pl.edu.agh.sr.client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Julia Samól on 13.03.2017.
 */
public class Client {
    private final String host = "localhost";
    private final int port = 8000;

    private ConsoleHandler consoleHandler;
    private UDPHandler udpHandler;

    private String username;

    public Client() {
        this.consoleHandler = null;
        this.udpHandler = null;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        System.out.println("CHAT\n"
                + "commands:\n"
                + "'\\quit()' - exit the application\n"
                + "'\\M <input>' - send via UDP (unicast)\n"
                + "'\\N <input>' - send via UDP (multicast)\n"
                + "###############################################\n");

        System.out.println("Enter your username: ");
        try {
            setUsername(
                    new BufferedReader(
                            new InputStreamReader(System.in)
                    ).readLine()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket socket = null;
        DatagramSocket datagramSocket = null;

        try {
            socket = new Socket(host, port);
            datagramSocket = new DatagramSocket();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            consoleHandler = new ConsoleHandler(this, socket, datagramSocket);
            consoleHandler.start();

            udpHandler= new UDPHandler(datagramSocket);
            udpHandler.start();

            while (true) {
                String message = in.readLine();
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (consoleHandler != null)
                consoleHandler.interrupt();
        }
    }

    public int getPort() {
        return port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void interruptRunning() {
        if (udpHandler != null)
            udpHandler.interrupt();
        System.exit(0);
    }
}
