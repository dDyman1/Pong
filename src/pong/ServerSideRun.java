/**
 * @author dovdm
 */
package pong;


import javax.swing.*;

public class ServerSideRun {
    static PongServer ps;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ps = new PongServer();
        ps.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ps.runServer();
    }
}
