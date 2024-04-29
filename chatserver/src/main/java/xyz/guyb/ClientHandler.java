package xyz.guyb;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Random;

public class ClientHandler extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int clientID;
    private String username;
    private ChatServer chatServer;
    private String motd;

    public ClientHandler(Socket socket, ChatServer chatServer) throws IOException {
        this.socket = socket;
        this.chatServer = chatServer;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        clientID = chatServer.getClientId();
        username = "Anonymous";
        chatServer.setUsername(clientID, username);
        chatServer.addUser(clientID);
        motd = "Welcome to the chat, use /help for a list of commands.";
        sendMessage("Your username is: " + username + "#" + clientID);
        sendMessage(motd);

        chatServer.broadcastRawMessage(username + "#" + clientID + " joined the chat");

    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    break;
                }
                if (message.startsWith("/")) {
                    handleCommand(message);
                } else {

                  chatServer.broadcastMessage(chatServer.getUsername(clientID), clientID, message);
                }

            }
        } catch (IOException e) {
            if (clientID != 0) {
                String formattedLeaveMessage = username + "#" + clientID + " left the chat";
                try {
                    chatServer.broadcastRawMessage(formattedLeaveMessage);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                chatServer.removeUser(clientID);
            }else {
                try {
                    chatServer.getLogger().logMessage("Client dropped connection");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                try {
                    chatServer.getLogger().logMessage("An error has occurred: " +e.getMessage());
                } catch (IOException ex) {
                    System.err.println("An error has occurred: " + e.getMessage());
                }
            }
        }
    }

    private void handleCommand(String message) throws IOException {
        String[] split = message.substring(1).split(" ");
        String command = split[0];
        String[] args = new String[split.length - 1];
        chatServer.getLogger().logMessage("User "+ username + "#" + clientID + " ran command: " + command);
        System.arraycopy(split, 1, args, 0, args.length);

        switch (command) {
            case "help":
                sendMessage("Available commands: /help, /username, /exit, /motd, /time, /roll");
                break;
            case "username":
                if (args.length == 0) {
                    sendMessage("/username <username>");
                } else {
                    String usernameREGEX = "^[a-zA-Z0-9]*$";
                    if (args[0].matches(usernameREGEX)){
                        String oldUsername = chatServer.getUsername(clientID);
                        chatServer.setUsername(clientID, args[0]);
                        username = args[0];
                        sendMessage("Your username is: " + args[0] + "#" + clientID);
                        chatServer.getLogger().logMessage("Username Change: " + oldUsername + clientID + "has changed their username to " + username);
                    }else {
                        sendMessage("/username <username>");
                        sendMessage("Error: Your username must only contain alphanumeric characters");
                    }

                }
                break;
            case "exit":
                sendMessage("Now closing your connection. Goodbye!");
                socket.close();
                chatServer.cleanupDisconnectedUser(clientID);
                break;
            case "motd":
                sendMessage(motd);
                break;
            case "time":
                sendMessage("The current time is " + LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS));
                break;

            case "roll":
                int limit = 1024;
                Random rand = new Random();
                int randomNumber = rand.nextInt(limit + 1);
                sendMessage("You rolled " + randomNumber);
                chatServer.broadcastRawMessage("Roll: " + username + "#" + clientID + " rolled " + randomNumber);
                if (randomNumber > chatServer.getHighestRoller()) {
                       chatServer.broadcastRawMessage("Roll: " + username + "#" + clientID + " is now the highest roller!");
                }
                chatServer.addRoll(clientID, randomNumber);
               break;

            default:
                sendMessage("Unknown command: " + command);
                break;
        }
    }



    void sendMessage(String s) {
        out.println(s);
    }
}
