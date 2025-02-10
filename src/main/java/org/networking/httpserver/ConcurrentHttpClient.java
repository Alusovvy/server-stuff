package org.networking.httpserver;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.networking.httpserver.HttpServerResponse.*;
import static org.networking.utils.FileHandler.getFile;
import static org.networking.utils.FileHandler.saveFile;

public class ConcurrentHttpClient implements Runnable {
    private static Pattern PATH_MATCH = Pattern.compile("(POST|GET) (\\S+) HTTP/1.1");
    private static Pattern HEADER_MATCH = Pattern.compile("User-Agent:");
    private Socket clientSocket;
    private String directory;
    private OutputStream outputStream;
    private BufferedReader reader;
    private Matcher matcher;
    private String requestBody;

    public ConcurrentHttpClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public ConcurrentHttpClient(Socket clientSocket, boolean useTmpDirectory) {
        this.clientSocket = clientSocket;
        this.directory = useTmpDirectory ? System.getProperty("java.io.tmpdir") : null;
    }

    @Override
    public void run() {
        try {
            outputStream = clientSocket.getOutputStream();
            setup();
            matchRequest();
        } catch (Exception e) {
            if(outputStream != null) sendResponse(SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void setup() throws IOException {
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        matcher = PATH_MATCH.matcher(reader.readLine());
        requestBody = "";
    }
    private void matchRequest() throws IOException {

        if (!matcher.matches()) {
            sendResponse(NOT_FOUND);
        }
        String requestType = matcher.group(1);
        String path = matcher.group(2);

        if (requestType.equalsIgnoreCase("POST")) {
            StringBuilder content = new StringBuilder();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains("Content-Type:")) {
                    while (reader.ready())
                        content.append((char)reader.read());
                }

            }
            requestBody = content.toString().trim();
        }

        if (path.contains("/echo/")) {
            buildEchoResponse(path, outputStream);
        } else if (path.equalsIgnoreCase("/")) {
            sendResponse(ACCEPTED);
        } else if (path.equalsIgnoreCase("/user-agent")) {
            buildUserAgentResponse(reader, outputStream);
        } else if (path.contains("/files/")) {
            if(requestType.equalsIgnoreCase("POST")) {
               saveFile(path.substring(6), requestBody, outputStream, directory);
            } else {
               getFile(getStringFromGet(path, "/files/"), outputStream, directory);
            }

        } else {
            sendResponse(NOT_FOUND);
        }

        outputStream.close();
    }

    private void sendResponse(HttpServerResponse response) {
        try {
            outputStream.write(response.getResponseValueBytes());
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    private void buildEchoResponse(String path, OutputStream output) throws IOException {
        String res = getStringFromGet(path, "/echo/");
        int length = res.length();

        String bodyResponse = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: %d\r\n" +
                        "\r\n" +
                        "%s\r\n",
                length, res
        );

        output.write(bodyResponse.getBytes());
        output.flush();
    }



    private String getStringFromGet(String input, String path) {
        String result = null;
        int startIndexOfEcho = input.indexOf(path);
        if (startIndexOfEcho != -1) {
            startIndexOfEcho += path.length();

            int endIndex = input.indexOf(" ", startIndexOfEcho);

            result = input.substring(startIndexOfEcho, endIndex != -1 ? endIndex : input.length());

        }

        return result;
    }

    private String parseHeader(BufferedReader in) throws IOException {
        boolean isSearching = true;
        String searchString = "User-Agent:";
        String line = "";
        while (isSearching) {

            line = in.readLine();

            if (line.contains("User-Agent:")) {
                isSearching = false;
            }
        }

        int cutStart = line.indexOf("User-Agent:") + searchString.length();

        return line.substring(cutStart);
    }

    private void buildUserAgentResponse(BufferedReader in, OutputStream output) throws IOException {
        String res = parseHeader(in).trim();
        int length = res.length();

        String bodyResponse = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: %d\r\n" +
                        "\r\n" +
                        "%s",
                length, res
        );

        output.write(bodyResponse.getBytes());
        output.flush();
    }
}
