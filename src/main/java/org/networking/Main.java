package org.networking;

import org.networking.udp.UdpClient;
import org.networking.udp.UdpServer;

public class Main {
    public static void main(String[] args) {

        UdpClient client = new UdpClient();
        UdpClient client2 = new UdpClient();

        UdpServer server = new UdpServer();

        Thread clientThreadOne = new Thread(client);
        Thread clientThreadTwo = new Thread(client2);
        Thread serverThread = new Thread(server);


        clientThreadOne.start();
        clientThreadTwo.start();
        serverThread.start();
    }
}
