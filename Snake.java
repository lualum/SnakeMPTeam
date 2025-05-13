import java.awt.*;
import java.io.Serializable;
import java.util.*;

/**
 * Snake Class
 *
 * @author Aarav Mann
 * @version 29 April, 2025
 */
public final class Snake implements Serializable {
    private static final long serialVersionUID = 1L;

    public final ArrayList<Integer> turns;
    public ArrayList<SnakeBlock> SnakeBlocks;
    private final Board board;
    public int direction;
    public final Color color;
    public static int UP = 90;
    public static int LEFT = 180;
    public static int DOWN = 270;
    public static int RIGHT = 0;
    private int frontRow = 0;
    private int frontCol = 0;
    private Snake[] opp;

    /**
     * Constructor method
     * 
     * @param b   the board of the snake
     * @param row the row of the snake's location
     * @param col the column of the snake's location
     */
    public Snake(Board b, int row, int col, Snake[] o) {
        turns = new ArrayList<>();
        this.color = new Color(59, 59, 59);
        this.board = b;
        this.direction = 0;
        this.putSelfInGrid(b, 0, row, col);
        opp = o;
    }

    /**
     * Second constructor for Snake Class
     * 
     * @param b     the board of the snake
     * @param dir   the direction of the original placement of snake (originally
     *              defaulted to 0)
     * @param row   the row of the snake's location
     * @param the   column of the snake's location
     * @param color the color of snake (originally defaulted to RED)
     */
    public Snake(Board b, int dir, int row, int col, Color color) {
        turns = new ArrayList<>();
        this.color = color;
        this.board = b;
        this.direction = dir;
        this.putSelfInGrid(b, dir, row, col);
    }

    /**
     * Method that puts the snake inside a board at a given location
     * 
     * @param board the board of the snake
     * @param dir   the direction where the snake is pointing
     * @param row   the row of the location
     * @param col   the column of the location
     */
    public void putSelfInGrid(Board board, int dir, int row, int col) {
        SnakeBlocks = new ArrayList<>();
        int dr = 0;
        int dc = 0;
        switch (dir) {
            case 0 -> dc = 1;
            case 90 -> dr = -1;
            case 180 -> dc = -1;
            case 270 -> dr = 1;
            default -> throw new RuntimeException("bruh, invalid direction");
        }
        for (int i = 3; i >= 0; i--) {
            if (board.validLocation(row - dr * i, col - dc * i)) {
                SnakeBlock part = new SnakeBlock(color);
                board.put((Block) part, row - dr * i, col - dc * i);
                part.putSelfInGrid(row - dr * i, col - dc * i, board);
                SnakeBlocks.add(part);
            } else
                System.out.println("FAILED!");
            // else throw new RuntimeException("bruh, snake be out of grid");
        }
        frontRow = row;
        frontCol = col;

    }

    /**
     * Method that moves the snake forward depending on its
     * orientation
     */
    public boolean move() {
        boolean recievedMove = false;
        if (!turns.isEmpty()) {
            direction = turns.get(0);
            recievedMove = true;
            turns.remove(0);
        }
        int dr = 0;
        int dc = 0;
        switch (direction) {
            case 0 -> dc = 1;
            case 90 -> dr = -1;
            case 180 -> dc = -1;
            case 270 -> dr = 1;
            default -> {
            }
        }
        frontRow += dr;
        frontCol += dc;
        if (Main.LOOPBOUNDS) {
            frontRow = (frontRow + board.getRows()) % board.getRows();
            frontCol = (frontCol + board.getCols()) % board.getCols();
        }
        if (!board.validLocation(frontRow, frontCol)) {
            board.stopGame(this);
            return recievedMove;
        }
        if (board.get(frontRow, frontCol) != null) {
            if (!board.validLocation(frontRow, frontCol)
                    || board.get(frontRow, frontCol).getType().equals("Snake Block")) {
                for (Snake s : opp) {
                    if (s.frontRow == frontRow && s.frontCol == frontCol
                            && direction == (s.direction + 180) % 360) {
                        board.stopGame(null);
                        return recievedMove;
                    }
                }

                board.stopGame(this);
                return recievedMove;
            } else {
                Main.placeFruit();
            }

        }
        if (board.get(frontRow, frontCol) == null || board.get(frontRow, frontCol).getType().equals("Snake Block")) {
            SnakeBlocks.get(0).setBoard(board);
            SnakeBlocks.get(0).removeSelfFromGrid();
            SnakeBlocks.remove(0);
        } else {
            board.multiplySpeed(this, board.get(frontRow, frontCol).getSpeedFactor());
        }
        SnakeBlock extra = new SnakeBlock(color);
        if (board.validLocation(frontRow, frontCol)) {
            extra.putSelfInGrid(frontRow, frontCol, board);
            SnakeBlocks.add(extra);
        }
        return recievedMove;
    }

    public void setOpp(Snake[] newOpp) {
        opp = newOpp;
    }

    /**
     * Method that turns the snake to a given direction. The
     * snake is not allowed to turn 180 degrees; it only can turn
     * 90 degrees.
     * 
     * @param newDir the direction that the snake is turning
     */
    @SuppressWarnings("NullableProblems")
    public boolean turn(int newDir) {
        if (turns.size() > 2)
            return false;
        int oldDir = turns.isEmpty() ? direction : turns.get(turns.size() - 1);
        if (oldDir % 180 != newDir % 180) {
            turns.add(newDir);
            return true;
        }
        return false;
    }

    public SnakeBlock getHead() {
        if (SnakeBlocks.isEmpty())
            return null;
        return SnakeBlocks.get(0);
    }

    public ArrayList<SnakeBlock> getSnakeBlocks() {
        return SnakeBlocks;
    }

    public void setSnakeBlocks(ArrayList<SnakeBlock> snakeBlocks) {
        SnakeBlocks = snakeBlocks;
    }

    public int getDirection() {
        return direction;
    }
}