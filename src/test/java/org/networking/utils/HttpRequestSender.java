package org.networking.utils;

import org.awaitility.Duration;
import org.networking.httpserver.request.HttpRequestType;
import org.networking.httpserver.response.HttpMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class HttpRequestSender {

    public static HttpMessage sendHttpRequest(HttpRequestType httpRequestType) {
        return sendHttpRequest(httpRequestType, "", "/");
    }

    public static HttpMessage sendHttpRequest(HttpRequestType httpRequestType, String path) {
        return sendHttpRequest(httpRequestType, "", path);
    }
    public static HttpMessage sendHttpRequest(HttpRequestType httpRequestType, String body, String path) {
        try {
            Socket socket = new Socket("localhost", 4221);;
            String httpRequest = String.format("%s %s HTTP/1.1\r\n", httpRequestType, path) +
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

            var res = HttpMessage.parseMessage(resString);

            assertThat(res).isNotNull();

            return res;

        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }
}
