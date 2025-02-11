package org.networking.httpserver.handlers;

import org.networking.httpserver.response.HttpMessage;

public class AgentResponseHandler implements RequestHandler {

    public byte[] handle(HttpMessage httpMessage) {
        String res = parseHeader(httpMessage);
        int length = res.length();

        String bodyResponse = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: %d\r\n" +
                        "\r\n" +
                        "%s",
                length, res
        );

        return bodyResponse.getBytes();
    }

    private static String parseHeader(HttpMessage request) {
        String searchString = "User-Agent:";
        var containsAgent = request.getBody().contains(searchString);

        if(!containsAgent) return "";

        int cutStart = request.getBody().indexOf("User-Agent:");
        int cutEnds = + searchString.length();;

        return request.getBody().substring(cutStart, cutEnds);
    }
}
