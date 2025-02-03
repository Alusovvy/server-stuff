package org.networking.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;

public class UdpClient implements Runnable {
    private final static int PORT = 5001;

    public void run() {
        DatagramPacket sendPacket;
        byte[] sendData;

        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        Scanner input = new Scanner(System.in);


        while(true) {
            String cmd = input.nextLine();

            if(cmd.equalsIgnoreCase("quit")) {
                clientSocket.close();
                System.exit(1);
            }

            sendData = cmd.getBytes();


            try {
                sendPacket = UDPHelper.createRandomAddressPacket(sendData, PORT);
                clientSocket.send(sendPacket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
