package org.networking.httpserver.handlers;

import org.networking.httpserver.response.HttpMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.networking.httpserver.response.HttpResponseType.CREATED;
import static org.networking.httpserver.response.HttpResponseType.NOT_FOUND;

public class FileHandler implements RequestHandler {

    String directory = System.getProperty("java.io.tmpdir");
    public static void saveFile(String filename, String body, OutputStream output, String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + filename))) {
            writer.write(body);
        }

        output.write(CREATED.getResponseValueBytes());

    }

    public byte[] getFile(HttpMessage httpMessage) throws IOException {
        Path filePath = Path.of(directory + httpMessage.getPathFromRequest("/files/"));
        File file = filePath.toFile();
        if (!file.exists()) {
            return NOT_FOUND.getResponseValueBytes();
        }
        byte[] data = Files.readAllBytes(file.toPath());
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

    @Override
    public byte[] handle(HttpMessage request) throws IOException {
        return getFile(request);
    }
}
