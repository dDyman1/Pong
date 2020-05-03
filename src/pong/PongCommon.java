package pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

//privilege identifiers removed to create "package-privacy
class PongCommon extends JFrame {

    //won't hit right panel if ballDiam is ODD val
    private final int BALL_DIAMETER = 24, PADDLE_WIDTH = 30, PADDLE_HEIGHT = 125;
    private GamePanel gamePanel;
    private GameData gameData = new GameData();
    private Point ball = new Point(100, 60), paddClient = new Point(752, 0), paddServer = new Point();
    private int ball_dx = 2, ball_dy = 2;
    private int playerServerPoints = 0, playerClientPoints = 0;

    private final String serverLabelText = "Server Player Points:";
    private final String clientLabelText = "Client Player Points:";
    private JLabel serverLabel, clientLabel, serverPts, clientPts;


    private ObjectOutputStream output; // output stream to server
    private ObjectInputStream input; // input stream from server

    public PongCommon() {
    }

    public PongCommon(String className) {
        //super(className);
        super("Pong V2 --- " + className);
        if (className.contains("PongServer")) {
            gamePanel = new GamePanel(paddServer, className);
        } else {
            gamePanel = new GamePanel(paddClient, className);
        }
        setSize(1000, 450);
        javax.swing.Timer ballUpdater = new javax.swing.Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ball.translate(ball_dx, ball_dy);
                if (ball.x > (gamePanel.getWidth() - BALL_DIAMETER) || ball.x < 5) {
                    ball_dx = -ball_dx;
                    if (ball.x < 5) {
                        playerClientPoints++;
                        clientPts.setText(playerClientPoints + "");
                    } else {
                        playerServerPoints++;
                        serverPts.setText(playerServerPoints + "");
                    }
                }
                if (ball.y > (gamePanel.getHeight() - BALL_DIAMETER) || ball.y < 5) {
                    ball_dy = -ball_dy;
                }
                if (ball.x == (paddServer.getX() + PADDLE_WIDTH)) {
                    if ((ball.y >= (paddServer.getY())) && (ball.y <= (paddServer.getY() + PADDLE_HEIGHT))) {
                        ball_dx = -ball_dx;
                    }
                }
                if ((ball.x + BALL_DIAMETER) == paddClient.x) {
                    if ((ball.y >= (paddClient.getY())) && (ball.y <= (paddClient.getY() + PADDLE_HEIGHT))) {
                        ball_dx = -ball_dx;
                    }
                }
                repaint();
            }
        });

        ballUpdater.start();
        add(gamePanel);
        add(gameData, BorderLayout.EAST);


        // setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {
                try (Scanner scanner = new Scanner(new File("pong.txt"))) {
                    ball.x = scanner.nextInt();
                    scanner.nextLine();
                    ball.y = scanner.nextInt();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try (PrintWriter pw = new PrintWriter("pong.txt")) {
                    pw.println(ball.x);
                    pw.println(ball.y);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.exit(0);

            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });

        setVisible(true);
    }
    class GamePanel extends JPanel{

        GamePanel(Point paddle, String className) {
            setPreferredSize(new Dimension(800, 400));
            setBackground(new Color(0, 250, 0));
            setFocusable(true);
            addKeyListener(new KeyListener() {
                long duration;

                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    keyEvent.consume();
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    switch( keyEvent.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (paddle.y >= 0) {
                                paddle.translate(0, -5);
                                sendPaddleData(paddle.getLocation(), className);
                            } else if ((paddle.y < 0)) {
                                paddle.translate(0, 400);
                                sendPaddleData(paddle.getLocation(), className);
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            if (paddle.y <= 400) {
                                paddle.translate(0, 5);
                                sendPaddleData(paddle.getLocation(), className);
                            } else if ((paddle.y) > 400) {
                                paddle.translate(0, -400);
                                sendPaddleData(paddle.getLocation(), className);
                            }
                            break;
                    }
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                }
            });

        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            g.fillOval(ball.x,ball.y, BALL_DIAMETER, BALL_DIAMETER);
            g.setColor(Color.BLUE);
            g.fillRect(paddServer.x, paddServer.y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.setColor(Color.RED);
            g.fillRect(paddClient.x, paddClient.y, PADDLE_WIDTH, PADDLE_HEIGHT);
        }
    }
    class GameData extends JPanel{

        public GameData(){
            setLayout(new GridLayout(6, 1));
            setBackground(new Color(200,0, 200));
            setPreferredSize(new Dimension(200, 400));
            JLabel title = new JLabel("Game Points");
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setFont(new Font("Tahoma", Font.BOLD, 24));

            serverLabel = new JLabel(serverLabelText);
            serverLabel.setHorizontalAlignment(SwingConstants.CENTER);
            serverLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

            clientLabel = new JLabel(clientLabelText);
            clientLabel.setHorizontalAlignment(SwingConstants.CENTER);
            clientLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

            serverPts = new JLabel();
            serverPts.setHorizontalAlignment(SwingConstants.CENTER);
            serverPts.setFont(new Font("Tahoma", Font.PLAIN, 16));

            clientPts = new JLabel();
            clientPts.setHorizontalAlignment(SwingConstants.CENTER);
            clientPts.setFont(new Font("Tahoma", Font.PLAIN, 16));

            add(title);
            add(serverLabel);
            add(serverPts);
            add(clientLabel);
            add(clientPts);


        }

    }


    // send message to server/client
    private void sendPaddleData(Object paddleData, String className) {
        // send object to server/client
        try {
            output.writeObject(paddleData);
            output.flush(); // flush output to server/client
            // displayMessage("paddle data sent");
        } // end try
        catch (IOException ioException) {
            displayMessage( "\nError writing object" );
        } // end catch
    } // end method sendData

    // manipulates displayArea in the event-dispatch thread
    void displayMessage( final String messageToDisplay )
    {
        SwingUtilities.invokeLater( // call on the EDT - Event Dispatch Thread -- only thread safe to update GUI because Swing is not thread safe
                new Runnable()
                {
                    public void run() // updates displayArea
                    {
                        JOptionPane.showMessageDialog(null, messageToDisplay ); // append message
                    } // end method run
                } // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method displayMessage
    // get streams to send and receive data

    void getStreams(Socket socket) throws IOException
    {
        // set up output stream for objects
        output = new ObjectOutputStream( socket.getOutputStream() ); // Decorator DP
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream( socket.getInputStream() );

        displayMessage( "\nGot I/O streams\n" );
    } // end method getStreams

    // process connection with server/client
    void processConnection(String className) throws IOException, ClassNotFoundException {
        String message = "l";
        Scanner in = new Scanner(input.readObject().toString()).useDelimiter("[^0-9]+");
        int x = in.nextInt();
        int y = in.nextInt();

        do // process messages sent from server/client
        {
            try // read message and display it
            {
                displayMessage("\n" + x + " " + y); // display message
            } // end try
            finally {
                displayMessage("\nUnknown object type received");
            }

        } while ( !message.contains(">>> TERMINATE" ));
    } // end method processConnection

    // close streams and socket
    void closeConnection(Socket socket, String className)
    {
        if (className.contains("Server")) {
            displayMessage("\nTerminating connection\n");
        } else if (className.contains("Client")) {
            displayMessage("\nClosing connection\n");
        }
        try
        {
            output.close(); // close output stream
            input.close(); // close input stream
            socket.close(); // close socket
        } // end try
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
    }
}