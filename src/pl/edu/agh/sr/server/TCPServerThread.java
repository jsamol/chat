package pl.edu.agh.sr.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Julia Samól on 13.03.2017.
 */
public class TCPServerThread extends Thread {
    private Server server;
    private int port;
    private ServerSocket serverSocket = null;

    public TCPServerThread(Server server, int port) {
        this.server = server;
        this.port = port;
        setName("Thread-TCPServer");
    }

    @Override
    public void run() {
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            server.log(Thread.currentThread().getName()
                    + "| Cannot create a ServerSocket object (IOException caught: "
                    + e
                    + ").");
            return;
        }

        server.log(Thread.currentThread().getName() + "| TCP socket created.");
        server.log(Thread.currentThread().getName() + "| TCP socket info: " + serverSocket);

        while(true) {
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                server.log(Thread.currentThread().getName()
                        + "| Error while waiting for connection (IOException caught: "
                        + e
                        + ").");
                onQuit();
                return;
            }

            server.log(Thread.currentThread().getName() + "| New client connected.");
            ServerClientThread client = new ServerClientThread(server, clientSocket);
            server.addClient(client);
            client.start();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        onQuit();
    }

    public void onQuit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            server.log(Thread.currentThread().getName()
                    + "| Error while closing server socket (IOException caught: "
                    + e
                    + ").");
        }
        server.log(getName() + " was interrupted.");
    }
}
