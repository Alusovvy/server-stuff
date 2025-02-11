package org.networking.httpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.networking.httpserver.request.HttpRequestType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.networking.utils.HttpRequestSender.sendHttpRequest;

class HttpServerTest {
    private ServerSocket serverSocket;


    @BeforeEach
    void prepare() {
        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            var thread = new Thread(() -> {
                try {
                    Socket client = serverSocket.accept();
                    Thread clientThread = new Thread(new ConcurrentHttpClient(client));
                    clientThread.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    @AfterEach
    void cleanup() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getResponseTest() {
        var response = sendHttpRequest(HttpRequestType.GET);
        assertThat(response.getResponseType().getStatusCode()).isEqualTo(200);
    }


    @Test
    void getWithEchoResponseTest() {
        var response = sendHttpRequest(HttpRequestType.GET, "/echo/test");
        assertThat(response.getBody()).contains("test");
    }






}
