package xyz.guyb;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ChatServerWebServer implements Runnable {

    private final ChatServer chatServer;
    private final int port;

    public ChatServerWebServer(ChatServer chatServer, int port) {
        this.chatServer = chatServer;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.start();
        chatServer.getLogger().logMessage("Web server started on port " + port);
        server.createContext("/", new WebHandler(chatServer));


    }



}
