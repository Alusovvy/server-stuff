package org.networking.httpserver.handlers;

import org.networking.httpserver.response.HttpMessage;
import org.networking.httpserver.response.HttpResponseType;

public class IncorrectRequestHandler implements RequestHandler {
    @Override
    public byte[] handle(HttpMessage request) {
        return HttpResponseType.SERVER_ERROR.getResponseValueBytes();
    }
}
