package org.networking.httpserver.handlers;

import org.networking.httpserver.response.HttpMessage;

public class EchoHandler implements RequestHandler {
    public byte[] handle(HttpMessage response) {
        String res = response.getPathFromRequest( "/echo/");
        int length = res.length();

        String bodyResponse = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: %d\r\n" +
                        "\r\n" +
                        "%s\r\n",
                length, res
        );

        return bodyResponse.getBytes();
    }

}
