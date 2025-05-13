import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L; // Add serialVersionUID for Serializable
    Board board;
    int score1;
    int score2;
    int[] snake1Direction;
    int[] snake2Direction;
    ArrayList<SnakeBlock>[] snake1Blocks;
    ArrayList<SnakeBlock>[] snake2Blocks;
    int gameStopped;
    boolean receivedInput;

    public GameState(Board board, int score1, int score2, int[] snake1Direction, int[] snake2Direction,
            ArrayList<SnakeBlock>[] snake1Blocks,
            ArrayList<SnakeBlock>[] snake2Blocks, int gameStopped, boolean receivedInput) {
        this.board = board;
        this.score1 = score1;
        this.score2 = score2;
        this.snake1Direction = snake1Direction;
        this.snake2Direction = snake2Direction;
        this.snake1Blocks = snake1Blocks;
        this.snake2Blocks = snake2Blocks;
        this.gameStopped = gameStopped;
        this.receivedInput = receivedInput;
    }

    public Board getBoard() {
        return board;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public int[] getSnake1Direction() {
        return snake1Direction;
    }

    public int[] getSnake2Direction() {
        return snake2Direction;
    }

    public ArrayList<SnakeBlock>[] getSnake1Blocks() {
        return snake1Blocks;
    }

    public ArrayList<SnakeBlock>[] getSnake2Blocks() {
        return snake2Blocks;
    }

    public int getGameStopped() {
        return gameStopped;
    }
}
