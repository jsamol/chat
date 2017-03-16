package pl.edu.agh.sr.client;

import pl.edu.agh.sr.server.UDPMulticastThread;

import java.io.*;
import java.net.*;

/**
 * Created by Julia Samól on 13.03.2017.
 */
public class Client {
    private final String host = "localhost";
    private final String group = "224.0.0.1";
    private final int port = 8000;
    private final int multicastPort = 8080;

    private ConsoleHandler consoleHandler;
    private UDPUnicastHandler udpUnicastHandler;
    private UDPMulticastHandler udpMulticastHandler;

    private String username;

    public Client() {
        this.consoleHandler = null;
        this.udpUnicastHandler = null;
        this.udpMulticastHandler = null;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        System.out.println("CHAT\n"
                + "commands:\n"
                + "'\\quit' - exit the application\n"
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
        MulticastSocket multicastSocket = null;

        try {
            socket = new Socket(host, port);
            datagramSocket = new DatagramSocket();
            multicastSocket = new MulticastSocket(multicastPort);
            InetAddress groupAddres = InetAddress.getByName(group);
            multicastSocket.joinGroup(groupAddres);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            consoleHandler = new ConsoleHandler(this, socket, datagramSocket, multicastSocket);
            consoleHandler.start();

            udpUnicastHandler = new UDPUnicastHandler(datagramSocket);
            udpUnicastHandler.start();

            udpMulticastHandler = new UDPMulticastHandler(this, multicastSocket);
            udpMulticastHandler.start();

            while (true) {
                String message = in.readLine();
                if (message == null) {
                    System.exit(-1);
                }
                else if (message.startsWith("\\u#")) {
                    setUsername(message.substring(3));
                }
                else
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

    public String getHost() {
        return host;
    }

    public String getGroup() {
        return group;
    }

    public int getPort() {
        return port;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void interruptRunning() {
        if (udpUnicastHandler != null)
            udpUnicastHandler.interrupt();
        if (udpMulticastHandler != null)
            udpMulticastHandler.interrupt();
        System.exit(0);
    }
}
