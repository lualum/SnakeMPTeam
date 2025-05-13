import java.awt.Color;
/**
 * Block interface
 *
 * @author Alvin Siamwalla
 * @version 21 April 2025
 */
public interface Block
{
    /**
     * Method that returns the color of Block
     * 
     * @return the color
     */
    public Color getColor();
    
    /**
     * Method that returns the x coordinate
     * 
     * @return the x coordinate
     */
    public int getX();

    /**
     * Method that returns the y coordinate
     * 
     * @return the y coordinate
     */
    public int getY();

    /**
     * Method that returns the type of block
     * 
     * @return the type of block
     */
    public String getType();
    
    /**
     * Method that returns the speed factor
     * 
     * @return the speed factor
     */
    public double getSpeedFactor();
}
