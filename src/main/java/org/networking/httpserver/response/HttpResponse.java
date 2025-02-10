package org.networking.httpserver.response;

import lombok.Getter;

import java.util.Optional;


@Getter
public class HttpResponse {
    private String header;
    private String body;

    private HttpResponse(String header, String body) {
        this.header = header;
        this.body = body;
    }

    private static HttpResponse withHeader(String header) {
        return new HttpResponse(header, null);
    }

    private static HttpResponse withHeaderAndBody(String header, String body) {
        return new HttpResponse(header, body);
    }

    public static Optional<HttpResponse> parseResponse(String rawResponse) {
        if(rawResponse.isEmpty()) return Optional.empty();
        String[] parts = rawResponse.split("\r\n\r\n", 2);
        String header = parts[0];
        String body = parts.length > 1 ? parts[1] : null;
        return Optional.of(body == null ? withHeader(header) : withHeaderAndBody(header, body));
    }
}