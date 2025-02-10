package org.networking.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.networking.httpserver.HttpServerResponse.CREATED;
import static org.networking.httpserver.HttpServerResponse.NOT_FOUND;

public class FileHandler {

    public static void saveFile(String filename, String body, OutputStream output, String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + filename))) {
            writer.write(body);
        }

        output.write(CREATED.getResponseValueBytes());

    }

    public static void getFile(String fileName, OutputStream outputStream, String directory) throws IOException {


        Path filePath = Path.of(directory + fileName);
        File file = filePath.toFile();
        if (!file.exists()) {
            outputStream.write(NOT_FOUND.getResponseValueBytes());
            return;
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

        outputStream.write(bodyResponse.getBytes());
    }
}
