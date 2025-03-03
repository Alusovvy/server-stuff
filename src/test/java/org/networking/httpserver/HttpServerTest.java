package org.networking.httpserver;

import org.junit.jupiter.api.*;
import org.networking.httpserver.request.HttpRequestType;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;
import static org.networking.utils.HttpRequestSender.sendHttpRequest;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HttpServerTest {
    private ServerSocket serverSocket;


    @BeforeEach
    void prepare() {
        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            var thread = new Thread(() -> {
                try {
                    Socket client = serverSocket.accept();
                    Thread clientThread = new Thread(new ConcurrentHttpClient(client));
                    clientThread.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    @AfterEach
    void cleanup() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @Order(0)
    void addFileResponseTest() throws IOException {
        String pathForFile = "/files/pliczek.txt";
        var response = sendHttpRequest(HttpRequestType.POST, "majtkimarynarza, dupapiekarza",pathForFile );
        assertThat(response.getResponseType().getStatusCode()).isEqualTo(201);

        Path filepath = Path.of(System.getProperty("java.io.tmpdir")+pathForFile);

        var file = Files.readString(filepath);
        assertThat(file).isEqualTo("majtkimarynarza, dupapiekarza");
    }

    @Test
    @Order(1)
    void getFileResponseTest() {
        var getFileResponse = sendHttpRequest(HttpRequestType.GET, "/files/pliczek.txt");
        assertThat(getFileResponse.getBody()).isEqualToIgnoringCase("majtkimarynarza, dupapiekarza");
    }


    @Test
    @Order(2)
    void updateFile() throws IOException {
        String pathForFile = "/files/pliczek.txt";
        var response = sendHttpRequest(HttpRequestType.PUT, ", cosinnego, cosnowego",pathForFile );
        assertThat(response.getResponseType().getStatusCode()).isEqualTo(200);

        Path filepath = Path.of(System.getProperty("java.io.tmpdir")+pathForFile);

        var file = Files.readString(filepath);
        assertThat(file).isEqualTo("majtkimarynarza, dupapiekarza, cosinnego, cosnowego");
    }

    @Test
    void getResponseTest() {
        var response = sendHttpRequest(HttpRequestType.GET);
        assertThat(response.getResponseType().getStatusCode()).isEqualTo(200);
    }


    @Test
    void getWithEchoResponseTest() {
        var response = sendHttpRequest(HttpRequestType.GET, "/echo/test");
        assertThat(response.getBody()).contains("test");
    }






}
