package org.networking.httpserver.handlers.factory;

import org.networking.httpserver.handlers.*;
import org.networking.httpserver.response.HttpMessage;
import org.networking.httpserver.response.HttpResponseType;

public class HandlerFactory {
    public static RequestHandler getHandler(HttpMessage request) {
        String path = request.getPath();

        if (path.startsWith("/echo/")) return new EchoHandler();
        if (path.equals("/user-agent")) return new AgentResponseHandler();
        if (path.startsWith("/files/")) return new FileHandler();
        if (path.equals("/")) return (HttpMessage) -> HttpResponseType.ACCEPTED.getResponseValueBytes();

        return (HttpMessage) -> HttpResponseType.SERVER_ERROR.getResponseValueBytes();
    }

}
