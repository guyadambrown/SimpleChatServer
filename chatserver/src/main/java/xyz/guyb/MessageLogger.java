package xyz.guyb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MessageLogger {
    private BufferedWriter logWriter;
    public MessageLogger() throws IOException {
        logWriter = new BufferedWriter(new FileWriter("ChatLog.log"));
    }

    public void logMessage(String message) throws IOException {
        // Get date and time to add in front of message
        long currentTimeInMillis = System.currentTimeMillis();

        Date currentDate = new Date(currentTimeInMillis);

        String currentDateTime = currentDate.toString();

        message = "["+currentDateTime + "] " + message;
        // Log message to console
        System.out.println(message);
        // Write the message to the log file.
        logWriter.write(message);
        // Add a new line to the file
        logWriter.newLine();
        // Flush the changes to the file
        logWriter.flush();
    }
}
