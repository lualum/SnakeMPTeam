import java.io.Serializable;
import java.util.Arrays;

/**
 * Board Class
 *
 * @author Jeffery Wang
 * @version 29 April 2025
 */

public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int numRows;
    private final int numCols;
    private Object[][] grid;
    private boolean gameStopped = false;

    public static double team1WaitTime = 1 / Main.INITIALSPEED;
    public static double team2WaitTime = 1 / Main.INITIALSPEED;

    /*
     * Constructor of Board Class.
     * 
     * @param r the number of rows in Board.
     * 
     * @param c the number of columns in Board.
     */

    public Board(int r, int c) {
        numRows = r;
        numCols = c;
        grid = new Object[numRows][numCols];
    }

    /**
     * Method that gets the number of rows in Board.
     * 
     * @return the number of rows in Board
     */
    public int getRows() {
        return this.numRows;
    }

    /**
     * Method that gets the number of columns in Board.
     * 
     * @return the number of columns in Board
     */
    public int getCols() {
        return this.numCols;
    }

    /**
     * Method that sees whether a location is valid given the row and column.
     * 
     * @return true if it is valid, false otherwise
     */
    public boolean validLocation(int row, int col) {
        return ((0 <= row && row < this.getRows()) && (0 <= col && col < this.getCols()));
    }

    /**
     * Method that puts a block into a location on Board given the row and column.
     * 
     * @param fruitToPlace the block.
     * @param row          the row of the location.
     * @param col          the column of the location.
     */
    public void put(Block fruitToPlace, int row, int col) {
        grid[row][col] = fruitToPlace;
    }

    /**
     * Method that removes the block at a given location.
     * 
     * @param row  the row of the location.
     * @param col.
     */
    public void remove(int row, int col) {
        grid[row][col] = null;
    }

    /**
     * A method that gets the block at a certain location.
     * 
     * @param row the row of the location.
     * @param col the column of the location.
     * @return the block at the location.
     */
    public Block get(int row, int col) {
        if (!validLocation(row, col)) {
            return null;
        }
        return (Block) grid[row][col];
    }

    /**
     * A method that stops the game when called.
     */
    public void stopGame(Snake s) {
        gameStopped = true;
        if (s == null) {
            Main.score1++;
            Main.score2++;
            Main.display.showWin(3);
        } else if (Arrays.asList(Main.team1).contains(s)) {
            Main.score2++;
            Main.display.showWin(2);
        } else {
            Main.score1++;
            Main.display.showWin(1);
        }
    }

    public void stopGame(int s) {
        gameStopped = true;
        switch (s) {
            case 3 -> {
                Main.score1++;
                Main.score2++;
                Main.display.showWin(3);
            }
            case 1 -> {
                Main.score2++;
                Main.display.showWin(2);
            }
            default -> {
                Main.score1++;
                Main.display.showWin(1);
            }
        }

    }

    /**
     * A method that sees whether the game is still alive or not.
     * 
     * @return true if game is over, false otherwise.
     */
    public boolean getGameStopped() {
        return gameStopped;
    }

    public void restartGame() {
        gameStopped = false;
        Main.display.victor = 0;
        grid = new Object[numRows][numCols];
    }

    public Object[][] getGrid() {
        return grid;
    }

    public void setGrid(Object[][] grid) {
        this.grid = grid;
    }

    public void multiplySpeed(Snake snake, double factor) {
        new Thread(() -> {
            try {
                if (Arrays.asList(Main.team2).contains(snake)) {
                    team1WaitTime = team1WaitTime / factor;
                    Thread.sleep(8000);
                    team1WaitTime = team1WaitTime * factor;
                } else {
                    team2WaitTime = team2WaitTime / factor;
                    Thread.sleep(8000);
                    team2WaitTime = team2WaitTime * factor;
                }
            } catch (InterruptedException e) {
                System.out.println("bruh: " + e);
            }
        }).start();
    }
}
