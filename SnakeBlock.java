import java.awt.Color;
import java.io.Serializable;

/**
 * SnakeBlock Class that represents the blocks that build up a snake
 *
 * @author Alvin Siamwalla
 * @version 29 April 2025
 */
public class SnakeBlock implements Block, Serializable {
    private static final long serialVersionUID = 1L;

    private Board board;
    private final Color color;
    private int xPos = -1;
    private int yPos = -1;

    /**
     * Constructor method
     * 
     * @param color the color of the snake block
     */
    public SnakeBlock(Color color) {
        this.color = color;
    }

    /**
     * A method that sets the board
     * 
     * @param b the board that is being set
     */
    public void setBoard(Board b) {
        board = b;
    }

    /**
     * Method that returns the type of block
     * 
     * @return the snake "Snake Block"
     */
    @Override
    public String getType() {
        return "Snake Block";
    }

    /**
     * Method that returns the color of the block
     * 
     * @return the color of the SnakeBlock
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     * Method that returns the row of the Snake Block
     */
    @Override
    public int getX() {
        return this.xPos;
    }

    /**
     * Method that returns the column of the Snake Block
     */
    @Override
    public int getY() {
        return this.yPos;
    }

    /**
     * Method that removes snake from the grid.
     */
    public void removeSelfFromGrid() {
        board.remove(xPos, yPos);
    }

    /**
     * Method that puts a SnakeBlock into a board
     * 
     * @param x     the row of the location
     * @param y     the column of the location
     * @param board the board where the block is placed
     */
    public void putSelfInGrid(int x, int y, Board board) {
        this.board = board;
        if (xPos != -1 && yPos != -1 && board.get(xPos, yPos) != null) {
            removeSelfFromGrid();
        }
        board.put((Block) this, x, y);
        this.yPos = y;
        this.xPos = x;
    }

    /**
     * Method that returns the speed factor
     * 
     * @return 1 because the snake doesn't get a speed boost if it
     *         hits a SnakeBlock
     */
    @Override
    public double getSpeedFactor() {
        return 1;
    }

}
