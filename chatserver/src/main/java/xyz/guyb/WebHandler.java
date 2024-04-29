package xyz.guyb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


class WebHandler implements HttpHandler {
    private final ChatServer chatServer;

    WebHandler(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        OutputStream os = exchange.getResponseBody();
        StringBuilder sb = new StringBuilder();
        long currentTimeInMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeInMillis);
        String currentDateTime = currentDate.toString();

        sb.append("<html><body>");
        sb.append("<h1>Chat Server").append(" (Port ").append(chatServer.getPort()).append(")</h1>");
        sb.append("<p>Welcome to the Chat Server!, it is currently ").append(currentDateTime).append("</p>");
        sb.append("<p>There are currently, ").append(chatServer.getConnectedUsers().toArray().length).append(" connected users</p>");
        String[] fileLines = readFile(chatServer.getLogger().getChatLog().getPath());

        if (fileLines != null) {
            sb.append("<div style=\"overflow-y: scroll; height: 200px;\">");
            for (String line : fileLines) {
                sb.append(line).append("<br>"); // Add line break for display
            }
            sb.append("</div>");
        } else {
            sb.append("<p>Error: Could not read file content.</p>");
        }
        sb.append("</body></html>");;

        byte[] responseBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        os.write(responseBytes);
        os.flush();
        os.close();

    }

    private String[] readFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[0]);  // Convert List to String array
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            return null;
        }
    }


}