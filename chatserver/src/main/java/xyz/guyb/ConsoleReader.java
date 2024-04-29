package xyz.guyb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader implements Runnable {

    private final ChatServer chatServer;


    public ConsoleReader(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String message = reader.readLine();

                if (message == null) {
                    break;
                } else {
                    if (message.contains("/toggleconnection")) {
                        chatServer.toggleListenForConnections();
                    }else {
                        chatServer.broadcastMessageFromConsole("Console", message);
                    }

                }

            }
        } catch (IOException e) {
            try {
                chatServer.getLogger().logMessage("An error has occurred: " + e.getMessage());
            } catch (IOException ex) {
                System.err.println("An error has occurred: " + e.getMessage());
            }
        }
    }
}
