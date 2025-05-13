import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;

/**
 * Class that implements the Snake Game
 * 
 * @author Jeffery Wang
 * @version 29 April, 2025
 */
public class Main implements ArrowListener {
    public static int playerID = 0;
    public static Snake player;

    public static Snake[] team1;
    public static Snake[] team2;
    public static int score1 = 0;
    public static int score2 = 0;
    public static Board b;
    public static BlockDisplay display;
    public static int teamPlayers;

    public static final int BOARDROWS = 20;
    public static final int BOARDCOLS = 20;
    public static final double FRUITSPACERATIO = 0.05;
    public static final int BLOCKSIZE = 10;
    public static final boolean FULLWIDTH = false;
    public static final double INITIALSPEED = 2;

    public static final boolean LOOPBOUNDS = false;
    public static final double FRUITPOWERSPREAD = 2.0;
    public static final double MAXFRUITSTRENGTH = 0.5;

    public static final boolean CHAOSMODE = false;
    public static final int CHAOSSPEED = 3;

    public static final int PORT = 12345; // Port for network communication
    public static boolean isHost = false; // true if this instance is the host, false if client

    private static boolean[] receivedInputs;

    private static Socket[] serverSocket;
    private static ObjectOutputStream[] serverOutputStream;
    private static ObjectInputStream[] serverInputStream;

    private static Socket clientSocket;
    private static ObjectOutputStream clientOutputStream;
    private static ObjectInputStream clientInputStream;

    public static void main(String[] args) {
        // Prompt user for host/client role
        int playersConnected = 1;

        String role = javax.swing.JOptionPane.showInputDialog("Enter 'host' or 'client':").trim().toLowerCase();
        switch (role) {
            case "host" -> {
                isHost = true;
                teamPlayers = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("Enter number of players:"));
                serverSocket = new Socket[teamPlayers * 2 - 1];
                serverOutputStream = new ObjectOutputStream[teamPlayers * 2 - 1];
                serverInputStream = new ObjectInputStream[teamPlayers * 2 - 1];
                try {
                    ServerSocket socket = NetworkUtils.startServer(PORT);
                    while (playersConnected < teamPlayers * 2) {
                        System.out.println(
                                "Waiting for client to connect... (" + playersConnected + "/" + teamPlayers *
                                        2 + ")");
                        serverSocket[playersConnected - 1] = socket.accept();
                        System.out.println("Client connected!");
                        serverOutputStream[playersConnected - 1] = new ObjectOutputStream(
                                serverSocket[playersConnected - 1].getOutputStream());
                        serverInputStream[playersConnected - 1] = new ObjectInputStream(
                                serverSocket[playersConnected - 1].getInputStream());
                        playersConnected++;
                    }
                    System.out.println("All clients connected! (" + playersConnected + "/" + teamPlayers * 2 + ")");
                } catch (IOException e) {
                    System.err.println("Error starting host: " + e.getMessage());
                    System.exit(1);
                }

                // Send player count and id
                for (int i = 0; i < teamPlayers * 2 - 1; i++) {
                    try {
                        NetworkUtils.sendObject(serverSocket[i], teamPlayers);
                        NetworkUtils.sendObject(serverSocket[i], i + 1);
                    } catch (IOException e) {
                        System.err.println("Error sending player count: " + e.getMessage());
                        System.exit(1);
                    }
                }
            }
            case "client" -> {
                String hostAddress = javax.swing.JOptionPane.showInputDialog("Enter host IP address:");
                try {
                    clientSocket = NetworkUtils.connectToServer(hostAddress, PORT);
                    clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    clientInputStream = new ObjectInputStream(clientSocket.getInputStream());
                    System.out.println("Connected to host!");
                } catch (IOException e) {
                    System.err.println("Error connecting to host: " + e.getMessage());
                    System.exit(1);
                }

                // Receive player count and id
                try {
                    teamPlayers = (int) NetworkUtils.receiveObject(clientSocket);
                    playerID = (int) NetworkUtils.receiveObject(clientSocket);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error receiving player count: " + e.getMessage());
                    System.exit(1);
                }
            }
            default -> {
                System.out.println("Invalid role. Please enter 'host' or 'client'.");
                System.exit(1);
            }
        }

        Main game = new Main();
        if (role.equals("host")) {
            game.serverListen();
            game.serverPlay();
        } else {
            game.clientPlay();
        }
    }

    private Main() {
        b = new Board(BOARDROWS, BOARDCOLS);
        display = new BlockDisplay(b, this);
        team1 = new Snake[teamPlayers];
        team2 = new Snake[teamPlayers];
        resetSnakes();

        if (isHost) {
            for (int i = 0; i < FRUITSPACERATIO * BOARDROWS * BOARDCOLS; i++) {
                placeFruit();
            }
            display.showBlocks();
            receivedInputs = new boolean[teamPlayers * 2 - 1];
            sendGameState();
        } else {
            try {
                receiveGameState();
                display.showBlocks();
            } catch (ClassNotFoundException | IOException e) {
                // class not found exc in setup
                System.err.println("Error during setup: " + e.getMessage());
                System.exit(1);
            }
        }

        player = (playerID < teamPlayers) ? team1[playerID] : team2[playerID - teamPlayers];

        if (CHAOSMODE) {
            Timer timer = new Timer(20, e -> display.moveWindow());
            timer.start();
        }
    }

    private static Color fruitColor(double value) {
        int red = (int) (255 * value);
        int green = 0;
        int blue = (int) (255 * (1 - value));

        return new Color(red, green, blue);
    }

    public static void placeFruit() {
        int index = 0;
        while (index < 1) {
            int randRow = (int) (Math.random() * b.getRows());
            int randCol = (int) (Math.random() * b.getCols());
            if (b.get(randRow, randCol) == null) {
                Fruit fruitToPlace;
                double rand = Math.random();
                int sign = 1;
                double power = 1 + Math.pow(rand, FRUITPOWERSPREAD) * MAXFRUITSTRENGTH;
                if (Math.random() < 0.5) {
                    power = 1 / power;
                    sign = 0;
                }
                fruitToPlace = new Fruit(randRow, randCol, 1, power, fruitColor((rand + sign) / 2));
                b.put(fruitToPlace, randRow, randCol);
                index++;
            }
        }
    }

    /**
     * Method that plays a game between two snakes.
     * 
     * @param snake1 first snake of the game.
     * @param snake2 second snake of the game.
     * 
     */
    private void serverListen() {
        // Thread for listening to client input forever
        for (int i = 0; i < 2 * teamPlayers - 1; i++) {
            final int id = i;
            Thread clientInputThread = new Thread(() -> {
                while (true) {
                    try {
                        receiveClientInput(id);
                    } catch (ClassNotFoundException | IOException e) {
                        // class not found exc in setup
                        System.err.println("Error during setupPROB: " + e.getMessage());
                        System.exit(1);
                    }
                }
            });
            clientInputThread.start();
        }
    }

    private void serverPlay() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable taskA = new Runnable() {
            @Override
            public void run() {
                // If the game is stopped, shut down the scheduler
                if (b.getGameStopped()) {
                    scheduler.shutdown();
                    return;
                }

                // Do your move + display
                receivedInputs = new boolean[teamPlayers * 2 - 1];
                team1[0].move();
                for (int i = 1; i < teamPlayers; i++) {
                    receivedInputs[i - 1] = team1[i].move();
                }

                sendGameState();
                display.showBlocks();
                int nextDelay = (int) (Board.team1WaitTime * 1000);
                scheduler.schedule(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        };

        Runnable taskB = new Runnable() {
            @Override
            public void run() {
                // If the game is stopped, shut down the scheduler
                if (b.getGameStopped()) {
                    scheduler.shutdown();
                    return;
                }

                // Do your move + display
                receivedInputs = new boolean[teamPlayers * 2 - 1];
                for (int i = 0; i < teamPlayers; i++) {
                    receivedInputs[i + teamPlayers - 1] = team2[i].move();
                }

                sendGameState();
                display.showBlocks();
                int nextDelay = (int) (Board.team2WaitTime * 1000);
                scheduler.schedule(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        };

        // Kick off the first execution
        int initialDelay = (int) (Board.team1WaitTime * 1000);
        scheduler.schedule(taskA, initialDelay, TimeUnit.MILLISECONDS);
        initialDelay = (int) (Board.team2WaitTime * 1000);
        scheduler.schedule(taskB, initialDelay, TimeUnit.MILLISECONDS);
    }

    private void clientPlay() {
        // Listens and updates game state & display
        new Thread(() -> {
            try {
                while (true) {
                    try {
                        receiveGameState();
                        display.showBlocks();
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error receiving game state: " + e.getMessage());
                    }
                }

            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }).start();
    }

    @Override
    public void keyPressed(KeyEvent k) {
        int code = k.getKeyCode();

        switch (code) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                turn(90);
                display.showBlocks();
            }
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                turn(270);
                display.showBlocks();
            }
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                turn(0);
                display.showBlocks();
            }
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                turn(180);
                display.showBlocks();
            }
            case KeyEvent.VK_SPACE -> {
                if (isHost && b.getGameStopped()) {
                    resetState();
                    sendGameState();
                    display.showBlocks();
                    serverPlay();
                } else if (!isHost && b.getGameStopped()) {
                    try {
                        sendPlayerInput(-1);
                    } catch (IOException e) {
                        System.err.println("Error attempting restart: " + e.getMessage());
                    }
                }
            }
            default -> {
            }
        }
    }

    private void turn(int dir) {
        if (isHost) {
            player.turn(dir);
        } else {
            if (player.turn(dir)) {
                try {
                    sendPlayerInput(dir);
                } catch (IOException e) {
                    System.err.println("Error sending player input: " + e.getMessage());
                }
            }
        }
    }

    private static void sendGameState() {
        // Package the game state into a single object or send multiple objects.
        // For simplicity, we'll create a GameState object. You'll need to
        // define this class to hold the necessary data.
        int[] team1Directions = new int[teamPlayers];
        int[] team2Directions = new int[teamPlayers];
        for (int i = 0; i < teamPlayers; i++) {
            team1Directions[i] = team1[i].getDirection();
            team2Directions[i] = team2[i].getDirection();
        }

        @SuppressWarnings("unchecked")
        ArrayList<SnakeBlock>[] team1Blocks = new ArrayList[teamPlayers];
        @SuppressWarnings("unchecked")
        ArrayList<SnakeBlock>[] team2Blocks = new ArrayList[teamPlayers];
        for (int i = 0; i < teamPlayers; i++) {
            team1Blocks[i] = team1[i].getSnakeBlocks();
            team2Blocks[i] = team2[i].getSnakeBlocks();
        }

        // Send the game state to all clients
        for (int i = 0; i < 2 * teamPlayers - 1; i++) {
            try {
                NetworkUtils.sendObject(serverSocket[i],
                        new GameState(b, score1, score2, team1Directions, team2Directions, team1Blocks,
                                team2Blocks,
                                display.victor, receivedInputs[i]));
            } catch (IOException e) {
                System.err.println("Error sending game state: " + e.getMessage());
            }
        }
    }

    // Receive the game state from the host. The client uses this to update
    // its local representation of the game.
    private static void receiveGameState() throws IOException, ClassNotFoundException {
        GameState gameState = (GameState) NetworkUtils.receiveObject(clientSocket);
        if (gameState.gameStopped > 0) {
            b.stopGame(gameState.gameStopped);
        } else if (b.getGameStopped()) {
            b.restartGame();
            resetSnakes();
        }
        b.setGrid(gameState.board.getGrid());
        score1 = gameState.score1;
        score2 = gameState.score2;

        for (int i = 0; i < teamPlayers; i++) {
            team1[i].direction = gameState.snake1Direction[i];
            team2[i].direction = gameState.snake2Direction[i];
            team1[i].setSnakeBlocks(gameState.snake1Blocks[i]);
            team2[i].setSnakeBlocks(gameState.snake2Blocks[i]);
        }

        if (gameState.receivedInput) {
            player.turns.remove(0);
        }
    }

    // Send the player's input (e.g., direction change) to the host.
    private static void sendPlayerInput(int playerInput) throws IOException {
        // Send the direction of the snake.
        NetworkUtils.sendObject(clientSocket, playerInput);
    }

    private void receiveClientInput(int id) throws IOException, ClassNotFoundException {
        // Ask client for input
        int move = (int) NetworkUtils.receiveObject(serverSocket[id]);
        Snake s = getSnake(id + 1);
        if (!b.getGameStopped())
            s.turn(move);
        else if (move == -1 && b.getGameStopped()) {
            resetState();
            sendGameState();
            display.showBlocks();
            serverPlay();
        }
    }

    private Snake getSnake(int id) {
        return (id < teamPlayers) ? team1[id] : team2[id - teamPlayers];
    }

    private static void resetSnakes() {
        for (int i = 0; i < teamPlayers; i++) {
            team1[i] = new Snake(b, Snake.RIGHT, (i + 1) * (BOARDROWS + 1) / (teamPlayers + 1) - 1, 5,
                    new Color(59, 59, 59));
            team2[i] = new Snake(b, Snake.LEFT, BOARDROWS - (i + 1) * (BOARDROWS + 1) / (teamPlayers + 1),
                    BOARDROWS - 5,
                    Color.WHITE);
        }
        for (int i = 0; i < teamPlayers; i++) {
            team1[i].setOpp(team2);
            team2[i].setOpp(team1);
        }

        player = (playerID < teamPlayers) ? team1[playerID] : team2[playerID - teamPlayers];
    }

    private static void resetState() {
        b.restartGame();
        resetSnakes();
        for (int i = 0; i < FRUITSPACERATIO * BOARDROWS * BOARDCOLS; i++) {
            placeFruit();
        }
        receivedInputs = new boolean[teamPlayers * 2 - 1];
    }
}
