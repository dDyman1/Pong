/**
 *
 * @author dovdm
 */
package pong;


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //evaluate args to set up pong window as client or server
        if (args.equals("Local")) {
            new PongPvPMain();
        } else if (args.equals("Server")) {
            new PongServer();
        } else if (args.equals("Client")) {
            new PongClient();
        }
        else {
            new PongPvPMain();
        }
    }
}
