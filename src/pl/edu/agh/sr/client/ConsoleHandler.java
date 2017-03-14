package pl.edu.agh.sr.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Julia Samól on 14.03.2017.
 */
public class ConsoleHandler extends Thread {
    private Socket socket;

    public ConsoleHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        PrintWriter out = null;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in)
        );

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                String message = in.readLine();
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
