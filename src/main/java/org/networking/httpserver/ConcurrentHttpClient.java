package org.networking.httpserver;

import org.networking.httpserver.handlers.factory.HandlerFactory;
import org.networking.httpserver.handlers.RequestHandler;
import org.networking.httpserver.response.HttpMessage;

import java.io.*;
import java.net.Socket;

import static org.networking.httpserver.response.HttpResponseType.*;

public class ConcurrentHttpClient implements Runnable {
    private Socket clientSocket;

    private OutputStream outputStream;
    private BufferedReader reader;

    public ConcurrentHttpClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            outputStream = clientSocket.getOutputStream();
            setup();
            matchRequest();
        } catch (Exception e) {
            if(outputStream != null) HttpMessage.sendResponse(SERVER_ERROR, outputStream);
            throw new RuntimeException(e);
        }
    }

    private void setup() throws IOException {
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    private void matchRequest() throws IOException {


        while(!reader.ready()) {

        }

        HttpMessage request = HttpMessage.parseMessage(reader);

        RequestHandler requestHandler = HandlerFactory.getHandler(request);

        var response = requestHandler.handle(request);
        outputStream.write(response);
        outputStream.flush();
        outputStream.close();
    }
}
