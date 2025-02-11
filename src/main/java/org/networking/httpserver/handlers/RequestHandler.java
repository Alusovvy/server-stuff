package org.networking.httpserver.handlers;

import org.networking.httpserver.response.HttpMessage;

import java.io.IOException;

@FunctionalInterface
public interface RequestHandler {
    byte[] handle(HttpMessage request) throws IOException;
}
