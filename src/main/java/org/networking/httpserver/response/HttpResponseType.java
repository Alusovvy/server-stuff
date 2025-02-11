package org.networking.httpserver.response;

public enum HttpResponseType {
    ACCEPTED(200, "OK"),
    CREATED(201, "Created"),
    NOT_FOUND(404, "Not Found"),
    SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String reasonPhrase;
    private final String responseValue;

    HttpResponseType(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.responseValue = String.format("HTTP/1.1 %d %s\r\n\r\n", statusCode, reasonPhrase);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getResponseValue() {
        return responseValue;
    }

    public byte[] getResponseValueBytes() {
        return responseValue.getBytes();
    }

    public static HttpResponseType fromStatusCode(int code) {
        for (HttpResponseType type : values()) {
            if (type.statusCode == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }
}