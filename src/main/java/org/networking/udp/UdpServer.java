package org.networking.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;

public class UdpServer implements Runnable{
    public void run() {
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(5001);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Server started, listening clients on port 5001");

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket;
        while(true) {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String clientMessage = new String(receivePacket.getData(),0,receivePacket.getLength());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("[" + timestamp + " ,IP: " + IPAddress + " ,Port: " + port +"]  " + clientMessage);
        }
    }
}
