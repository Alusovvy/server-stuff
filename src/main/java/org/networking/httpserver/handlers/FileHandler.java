package org.networking.httpserver.handlers;

import org.networking.httpserver.request.HttpRequestType;
import org.networking.httpserver.response.HttpMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.networking.httpserver.response.HttpResponseType.*;

public class FileHandler implements RequestHandler {

    String directory = System.getProperty("java.io.tmpdir");
    public byte[] saveFile(String body, String path) throws IOException {
        Path localPath = Path.of(directory + path);
        Files.createDirectories(localPath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(localPath)) {
            writer.write(body);
            writer.flush();
        }

       return CREATED.getResponseValueBytes();

    }

    public byte[] getFileResponse(HttpMessage httpMessage) throws IOException {
        var data = getFileAsByteArr(httpMessage);
        int length = data.length;
        String bodyResponse = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/octet-stream\r\n" +
                        "Content-Length: %d\r\n" +
                        "\r\n" +
                        "%s",
                length, new String(data)
        );

        return bodyResponse.getBytes();
    }


    public byte[] updateFile(HttpMessage httpMessage) throws IOException {
        var data = getFileAsByteArr(httpMessage);
        StringBuilder stringBuilder = new StringBuilder(new String(data));
        stringBuilder.append(httpMessage.getBody());
        saveFile(stringBuilder.toString(), httpMessage.getPath());

        return ACCEPTED.getResponseValueBytes();
    }

    @Override
    public byte[] handle(HttpMessage request) throws IOException {
        if (request.getMethod().equalsIgnoreCase(HttpRequestType.GET.toString())) {
            return getFileResponse(request);
        } else if (request.getMethod().equalsIgnoreCase(HttpRequestType.POST.toString())) {
            return saveFile(request.getBody(), request.getPath());
        } else if (request.getMethod().equalsIgnoreCase(HttpRequestType.PUT.toString())) {
            return updateFile(request);
        } else {
            throw new IOException("No valid handler for this request type");
        }
    }


    private byte[] getFileAsByteArr(HttpMessage httpMessage) throws IOException {
        Path filePath = Path.of(directory + httpMessage.getPath());
        File file = filePath.toFile();
        if (!file.exists()) {
            return NOT_FOUND.getResponseValueBytes();
        }
        byte[] data = Files.readAllBytes(file.toPath());

        return data;
    }
}
