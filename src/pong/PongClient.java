package pong;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class PongClient extends PongCommon {
    PongCommon cc;
    String className;
    private String chatServer; // host server for this application
    private Socket client; // socket to communicate with server

    // initialize chatServer and set up GUI
    public PongClient(String host)
    {
        chatServer = host; // set server to which this client connects
        className = getClass().getName();
        cc = new PongCommon(className);
        cc.constructorBuild(getClass().getName());
    } // end Client constructor

    // connect to server and process messages from server
    public void runClient()
    {
        try // connect to server, get streams, process connection
        {
            connectToServer(); // create a Socket to make connection
            cc.getStreams(client); // get the input and output streams
            cc.processConnection(className); // process connection
        } // end try
        catch ( EOFException eofException )
        {
            cc.displayMessage( "\nClient terminated connection" );
        } // end catch
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
        finally
        {
            cc.closeConnection(client, className); // close connection
        } // end finally
    } // end method runClient

    // connect to server
    private void connectToServer() throws IOException
    {
        cc.displayMessage( "Attempting connection\n" );

        // create Socket to make connection to server
        client = new Socket( InetAddress.getByName( chatServer ), 42069 );

        // display connection information
        cc.displayMessage( "Connected to: " +
                client.getInetAddress().getHostName() );
    } // end method connectToServer
}
