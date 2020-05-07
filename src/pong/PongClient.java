package pong;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class PongClient extends PongCommon {
    PongCommon cc;
    String className;
    private String chatServer;
    private Socket client;

    public PongClient(String localhost) {
        className = getClass().getName();
        cc = new PongCommon(className);
        chatServer = localhost;
    }

    public void runClient() {
        try {
            connectToServer();
            cc.getStreams(client);
            cc.processConnection(className);
        } catch (EOFException eofException) {
            cc.displayMessage("\nClient terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            cc.closeConnection(client, className);
        }
    }

    private void connectToServer() throws IOException {
        cc.displayMessage("Attempting connection\n");

        client = new Socket(InetAddress.getByName(chatServer), 42069);

        cc.displayMessage("Connected to: " +
                client.getInetAddress().getHostName());
        cc.runCommon(className);
    }
}
