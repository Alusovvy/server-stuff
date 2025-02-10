package org.networking.httpserver;

public enum RequestType {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    RequestType(String responseValue) {
        this.value = responseValue;
    }

    public String getResponseValue() {
        return value;
    }

    public byte[] getResponseValueBytes() {
        return value.getBytes();
    }
}
