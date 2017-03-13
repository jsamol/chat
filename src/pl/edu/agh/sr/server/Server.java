package pl.edu.agh.sr.server;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int port = 8000;
    private List<ServerClientThread> clients; //OPTIONAL: change List to something concurrent
    private TCPServerThread tcpServer = null;
    private UDPServerThread udpServer = null;

    public Server() {
        clients = new ArrayList<>();
        tcpServer = new TCPServerThread(this, port);
        udpServer = new UDPServerThread(this, port);

        tcpServer.start();
        udpServer.start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        System.out.println("CHAT SERVER");
        Thread.currentThread().setName("Thread-Server");
    }

    public void log(String message) {
        // console log
        System.out.println(message);

        //TODO: file log
    }

    public synchronized void addClient(ServerClientThread client) {
        clients.add(client);
        log(Thread.currentThread().getName() + "| New user added: " + client.getName() + ".");
    }

    public synchronized void removeClient(ServerClientThread client) {
        clients.remove(client);
        log(Thread.currentThread().getName() + "| User " + client.getName() + " removed.");
    }

    public synchronized void broadcastMessage(ServerClientThread broadcastingClient, String message) {
        for (ServerClientThread client : clients) {
            if (!client.equals(broadcastingClient))
                client.writeMessage(broadcastingClient.getName().split("#", 2)[1] + ": " + message);
        }
    }
}
