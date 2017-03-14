package pl.edu.agh.sr.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Julia Samól on 14.03.2017.
 */
public class UDPUnicastHandler extends Thread {
    private DatagramSocket datagramSocket;

    public UDPUnicastHandler(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                datagramSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String message = new String(receivePacket.getData());
            System.out.println(message);
        }
    }
}
