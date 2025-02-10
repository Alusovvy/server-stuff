package org.networking.httpserver;

public enum HttpServerResponse {
    ACCEPTED("HTTP/1.1 200 OK\r\n\r\n"),
    CREATED("HTTP/1.1 201 Created\r\n\r\n"),
    NOT_FOUND("HTTP/1.1 404 Not Found\r\n\r\n"),
    SERVER_ERROR("HTTP/1.1 500 Internal Server Error\r\n\r\n");

    private final String responseValue;

    HttpServerResponse(String responseValue) {
        this.responseValue = responseValue;
    }

    public String getResponseValue() {
        return responseValue;
    }

    public byte[] getResponseValueBytes() {
        return responseValue.getBytes();
    }
}
