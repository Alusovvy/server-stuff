package org.networking.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Random;

public class UDPHelper {
    public static DatagramPacket createRandomAddressPacket(byte[] sendData, int port) throws Exception {
        Random random = new Random();
        int lastOctet = random.nextInt(256); // Generates a number between 0-255
        String randomIP = "127.0.0." + lastOctet;
        InetAddress address = InetAddress.getByName(randomIP);

        return new DatagramPacket(sendData, sendData.length, address, port);
    }
}

