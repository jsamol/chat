package pl.edu.agh.sr.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Julia Samól on 13.03.2017.
 */
public class ServerClientThread implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter out;

    public ServerClientThread(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        out = null;

        try {
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            server.log(Thread.currentThread().getName()
                        + "| Error while creating io streams (IOException caught: "
                        + e
                        + ").");
            onQuit();
            return;
        }

        while (true) {
            try {
                String message = in.readLine();
                if (message != null) {
                    server.log(Thread.currentThread().getName() + "| Received message: '" + message + "'.");
                    if (message.startsWith("\\u#")) {
                        String username = message.substring(3);
                        server.log(Thread.currentThread().getName() + "| User set username to " + username);
                        Thread.currentThread().setName(Thread.currentThread().getName() + "#" + username);
                        writeMessage("\nYour username is " + username);
                        writeMessage("#########################################");
                        writeMessage("\\u#" + Thread.currentThread().getName());
                     }
                    else
                        server.broadcastMessage(this, message.split("#", 2)[1]);
                }
                else {
                    onQuit();
                    return;
                }
            } catch (IOException e) {
                server.log(Thread.currentThread().getName() + "| Error while reading from the input stream (IOException caught: "
                        + e
                        + ").");
                onQuit();
                return;
            }
        }

    }


    public void writeMessage(String message) {
        out.println(message);
    }

    public void onQuit() {
        server.removeClient(this);
        server.log(Thread.currentThread().getName() + " was interrupted.");
    }
}
