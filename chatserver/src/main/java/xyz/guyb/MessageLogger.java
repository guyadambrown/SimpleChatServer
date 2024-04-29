package xyz.guyb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MessageLogger {
    private final BufferedWriter serverLogWriter;
    private final BufferedWriter chatLogWriter;



    public MessageLogger() throws IOException {
        serverLogWriter = new BufferedWriter(new FileWriter("ServerLog.log"));
        chatLogWriter = new BufferedWriter(new FileWriter("ChatLog.log"));
    }

    // Get date and time to add in front of message
    long currentTimeInMillis = System.currentTimeMillis();

    Date currentDate = new Date(currentTimeInMillis);

    String currentDateTime = currentDate.toString();
    String messagePrefix = "[" + currentDateTime + "] ";

    public void logMessage(String message) throws IOException {
        message = messagePrefix + "[Server] " + message;
        // Log message to console
        System.out.println(message);
        // Write the message to the file, add a new line and then flush
        serverLogWriter.write(message);
        serverLogWriter.newLine();
        serverLogWriter.flush();
    }

    public void logChatMessage(String message) throws IOException {
        message = messagePrefix + "[Chat] " + message;
        // Log the message to console
        System.out.println(message);
        // Write the message to the file, add a new line and then flush
        chatLogWriter.write(message);
        chatLogWriter.newLine();
        chatLogWriter.flush();

    }

    public File getChatLog() {
        return new File("ChatLog.log");
    }
    public File getServerLog() {
        return new File("ServerLog.log");
    }
}
