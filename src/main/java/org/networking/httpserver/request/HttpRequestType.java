package org.networking.httpserver.request;

public enum HttpRequestType {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    HttpRequestType(String responseValue) {
        this.value = responseValue;
    }

    public String getResponseValue() {
        return value;
    }

    public byte[] getResponseValueBytes() {
        return value.getBytes();
    }
}
