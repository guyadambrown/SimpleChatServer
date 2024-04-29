// Import the necessary classes
package xyz.guyb;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class ChatServer {

    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final MessageLogger logger;
    private int clientIdCount = 1;
    private final Map<Integer, String> clientUsernames = new HashMap<>();
    private final List<Integer> connectedUsers = new ArrayList<>();
    private boolean listenForConnections = true;
    private HashMap<Integer, Integer> userRolls = new HashMap<>();


    public ChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        logger = new MessageLogger();
        logger.logMessage("Server is now listening on all interfaces on port " + port);
    }

    public void start() throws IOException {
        Thread consoleInputThread = new Thread(new ConsoleReader(this));
        consoleInputThread.start();

        while (listenForConnections) {
            try {
                Socket socket = serverSocket.accept();
                logger.logMessage("New client connected: " + socket);
                ClientHandler client = new ClientHandler(socket, this);
                clients.add(client);
                client.start();
            } catch (IOException e) {
                logger.logMessage("An error has occurred: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8089;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);


        }

        ChatServer server = new ChatServer(port);
        server.start();
    }

    public void broadcastMessage(String username,int clientID ,String message) throws IOException {
        message = username + "#" + clientID + ": " + message;
        logger.logMessage(message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastMessageFromConsole(String username, String message) throws IOException {
        message = username + ": " + message;
        logger.logMessage(message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastRawMessage(String rawMessage) throws IOException {
        for (ClientHandler client : clients) {
            client.sendMessage(rawMessage);
        }
    }

    public MessageLogger getLogger() {
        return logger;
    }

    // Method to generate a unique client ID
    public synchronized int getClientId() {
        return clientIdCount++;
    }

    // Method to set username for a client
    public synchronized void setUsername(int clientId, String username) {
        clientUsernames.put(clientId, username);
    }

    // Method to get username
    public synchronized String getUsername(int clientId) {
        return clientUsernames.get(clientId);
    }

    // Method to add a user to the connected user list
    public synchronized void addUser(int clientId) {
        connectedUsers.add(clientId);
    }

    // Method to remove a user from the connected user list
    public synchronized void removeUser(int clientId) {
        connectedUsers.remove((Integer) clientId);
    }

    public void cleanupDisconnectedUser(int clientID) {
        clientUsernames.remove(clientID);
    }

    public void toggleListenForConnections() {
        if (listenForConnections) {
            listenForConnections = false;
        } else {
            listenForConnections = true;
        }
    }

    public synchronized List<Integer> getConnectedUsers() {
        return connectedUsers;
    }

    public void addRoll(int userId, int roll) {
        userRolls.put(userId, Math.max(userRolls.getOrDefault(userId, 0), roll));
    }

    public int getHighestRoller() {
        if (userRolls.isEmpty()) {
            return -1; // Or any default value if no rolls yet
        }
        return Collections.max(userRolls.values());
    }


}
