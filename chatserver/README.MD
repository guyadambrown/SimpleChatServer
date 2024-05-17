# Programming 4 - Java chat server

A socket based chat server built with java and maven.

## How to build
Ensure Maven in propperly instaled and navigate to the source code of the part of the application that you want to build e.g "/ChatClient/chatclient/" and run ``` mvn package ```

Prebuilt jar files are included in the releases section

## How to run
Ensure java is installed and run the jar files with ```java -jar ```  followed by the file name, for example ```java -jar chatserver-1.0-SNAPSHOT.jar ```

Command line arguments are also available for this component. Theese define what ports the chat server and the http server will use an example of usage would be ```java -jar chatserver-1.0-SNAPSHOT.jar 9090 9095``` where the chat server will run on port 9090 and the web server will run on port 9095.
