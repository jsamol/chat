package pl.edu.agh.sr.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port = 8000;
    private final int multicastPort = 8080;
    private final String group = "224.0.0.1";
    private ServerSocket serverSocket = null;
    private List<ServerClientThread> clients;
    private ExecutorService executorService;

    private UDPUnicastThread udpUnicastThread = null;
    private UDPMulticastThread udpMulticastThread = null;

    public Server() {
        clients = new ArrayList<>();
        udpUnicastThread = new UDPUnicastThread(this, port);
        udpMulticastThread = new UDPMulticastThread(this, group, multicastPort);

        executorService = Executors.newFixedThreadPool(15);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        System.out.println("CHAT SERVER");
        Thread.currentThread().setName("Thread-Server");

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

        udpUnicastThread.start();
        udpMulticastThread.start();

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
        System.out.println(message);
    }

    public void addClient(ServerClientThread client) {
        synchronized (this) {
            clients.add(client);
            log(Thread.currentThread().getName() + "| New user added: " + client + ".");
        }
    }

    public void removeClient(ServerClientThread client) {
        synchronized (this) {
            clients.remove(client);
            log(Thread.currentThread().getName() + "| User " + client + " removed.");
        }
    }

    public void broadcastMessage(ServerClientThread broadcastingClient, String message) {
        synchronized (this) {
            for (ServerClientThread client : clients) {
                if (!client.equals(broadcastingClient))
                    client.writeMessage(message);
            }
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

        udpUnicastThread.interrupt();
        udpMulticastThread.interrupt();
        executorService.shutdown();
        log(Thread.currentThread().getName() + " was interrupted.");
    }
}
