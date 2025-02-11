package org.networking.httpserver.response;

import lombok.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpMessage {
    private static final Pattern PATH_WITH_RESPONSE_TYPE_MATCH = Pattern.compile("(POST|GET) (\\S+) HTTP/1.1$");
    private static final Pattern STATUS_CODE_PATTERN = Pattern.compile("\\d[0-9]+");

    private String body;
    private String path;
    private String method;
    private HttpResponseType responseType;

    public static void sendResponse(HttpResponseType response, OutputStream outputStream) {
        try {
            outputStream.write(response.getResponseValueBytes());
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public String getPathFromRequest(String startPath) {
        String result = null;
        int startIndexOfEcho = path.indexOf(startPath);
        if (startIndexOfEcho != -1) {
            startIndexOfEcho += startPath.length();

            int endIndex = path.indexOf(" ", startIndexOfEcho);

            result = path.substring(startIndexOfEcho, endIndex != -1 ? endIndex : path.length());

        }

        return result;
    }

    public static HttpMessage parseMessage(String string) {
        InputStream inputStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return parseMessage(reader);
    }

    public static HttpMessage parseMessage(BufferedReader reader) {
        var builder = new HttpMessageBuilder();
        try {
            String requestLine = reader.readLine();

            var methodAndPathMatcher = PATH_WITH_RESPONSE_TYPE_MATCH.matcher(requestLine);
            var statusMatcher = STATUS_CODE_PATTERN.matcher(requestLine);
            if (!methodAndPathMatcher.find() && !statusMatcher.find()) {
                throw new IllegalArgumentException("Invalid HTTP Request");
            }


            if (methodAndPathMatcher.matches()) {
                builder.method(methodAndPathMatcher.group(1));
                builder.path(methodAndPathMatcher.group(2));
            } else if (statusMatcher.group(0) != null) {
                Integer status = Integer.valueOf(statusMatcher.group(0));
                builder.responseType(HttpResponseType.fromStatusCode(status));
            }


            StringBuilder content = new StringBuilder();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains("Content-Type:")) {
                    while (reader.ready()) {
                        content.append((char) reader.read());
                    }
                }
            }
            builder.body(content.toString().trim());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }
}