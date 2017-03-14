package pl.edu.agh.sr.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private static final int port = 8000;
    private ServerSocket serverSocket = null;
    private List<ServerClientThread> clients; //OPTIONAL: change List to something concurrent
    private ExecutorService executorService;

    private UDPServer udpServer = null;

    public TCPServer() {
        clients = new ArrayList<>();
        udpServer = new UDPServer(this, port);

        executorService = Executors.newFixedThreadPool(15);
    }

    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.start();
    }

    public void start() {
        System.out.println("CHAT SERVER");
        Thread.currentThread().setName("Thread-TCPServer");

        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log(Thread.currentThread().getName()
                    + "| Cannot create a ServerSocket object (IOException caught: "
                    + e
                    + ").");
            return;
        }

        log(Thread.currentThread().getName() + "| TCP socket created.");
        log(Thread.currentThread().getName() + "| TCP socket info: " + serverSocket);

        udpServer.start();

        while(true) {
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                log(Thread.currentThread().getName()
                        + "| Error while waiting for connection (IOException caught: "
                        + e
                        + ").");
                onQuit();
                return;
            }

            log(Thread.currentThread().getName() + "| New client connected.");
            ServerClientThread client = new ServerClientThread(this, clientSocket);
            addClient(client);
            executorService.execute(client);
        }
    }

    public void log(String message) {
        // console log
        System.out.println(message);

        //TODO: file log
    }

    public synchronized void addClient(ServerClientThread client) {
        clients.add(client);
        log(Thread.currentThread().getName() + "| New user added: " + client + ".");
    }

    public synchronized void removeClient(ServerClientThread client) {
        clients.remove(client);
        log(Thread.currentThread().getName() + "| User " + client + " removed.");
    }

    public synchronized void broadcastMessage(ServerClientThread broadcastingClient, String message) {
        for (ServerClientThread client : clients) {
            if (!client.equals(broadcastingClient))
                client.writeMessage(message);
        }
    }

    public void onQuit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            log(Thread.currentThread().getName()
                    + "| Error while closing server socket (IOException caught: "
                    + e
                    + ").");
        }
        log(Thread.currentThread().getName() + " was interrupted.");
    }
}
