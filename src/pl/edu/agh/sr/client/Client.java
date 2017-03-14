package pl.edu.agh.sr.client;

import java.io.*;
import java.net.Socket;

/**
 * Created by Julia Samól on 13.03.2017.
 */
public class Client {
    public static void main(String[] args) {
        final String host = "localhost";
        final int port = 8000;
        Socket socket = null;
        ConsoleHandler consoleHandler = null;

        try {
            socket = new Socket(host, port);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            consoleHandler = new ConsoleHandler(socket);
            consoleHandler.start();

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
}
