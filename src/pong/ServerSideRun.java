package pong;

public class ServerSideRun {
    static PongServer ps;

    public static void main(String[] args) {
        ps = new PongServer();
        ps.runServer();
    }
}
