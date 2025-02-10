package org.networking.httpserver;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.networking.httpserver.response.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

class HttpServerTest {
    static ServerSocket serverSocket;

    @BeforeAll
    static void prepServerSocket() {
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

    @Test
    void getResponseTest() {
        var response = sendHttpRequest(RequestType.GET);
        assertThat(response.getHeader()).containsIgnoringCase("HTTP/1.1 200 OK");
    }


    @Test
    void getWithEchoResponseTest() {
        var response = sendHttpRequest(RequestType.GET, "/echo/test");
        assertThat(response.getBody()).contains("test");
    }

    private HttpResponse sendHttpRequest(RequestType requestType) {
        return sendHttpRequest(requestType, "", "/");
    }

    private HttpResponse sendHttpRequest(RequestType requestType, String path) {
        return sendHttpRequest(requestType, "", path);
    }


    private HttpResponse sendHttpRequest(RequestType requestType, String body, String path) {
        try {
            Socket socket = new Socket("localhost", 4221);

            String httpRequest = String.format("%s %s HTTP/1.1\r\n", requestType, path) +
                    "Host: localhost\r\n" +
                    "Connection: close\r\n\r\n";

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(httpRequest);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            await().atMost(Duration.TEN_SECONDS)
                    .until(in::ready);

            assertThat(in.ready()).isTrue();
            var resString =  in.lines().collect(Collectors.joining("\r\n"));

            socket.close();

            var res = HttpResponse.parseResponse(resString);

            assertThat(res.isPresent()).isTrue();

            return res.get();

        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

}
