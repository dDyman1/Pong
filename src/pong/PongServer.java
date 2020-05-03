package pong;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PongServer extends PongCommon {

    PongCommon cc;
    String className;
    private ServerSocket server;
    private Socket connection;
    private int counter = 1;

    public PongServer() {
        className = getClass().getName();
        cc = new PongCommon(className);
    }

    public void runServer() {
        try {
            server = new ServerSocket(42069, 100);

            while (true) {
                try {
                    waitForConnection();
                    cc.getStreams(connection);
                    cc.processConnection(className);
                } catch (EOFException eofException) {
                    cc.displayMessage("\nServer terminated connection");
                } finally {
                    cc.closeConnection(connection, className);
                    ++counter;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    private void waitForConnection() throws IOException {
        cc.displayMessage("Waiting for connection\n");
        connection = server.accept();
        cc.displayMessage("Connection " + counter + " received from: " +
                connection.getInetAddress().getHostName());
    }
}

