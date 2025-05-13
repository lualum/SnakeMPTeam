import java.awt.Color;
import java.io.Serializable;

/**
 * Abstract Gfuel Class
 *
 * @author Alvin Siamwalla
 * @version 29 April, 2025
 */
public class Fruit implements Block, Serializable {
    private static final long serialVersionUID = 1L;

    private double speedFactor = 1;
    private final int xCoord;
    private final int yCoord;
    private int lengthBoost = 1;
    private final Color color;

    public Fruit(int x, int y, int lengthBoost, double speedFactor, Color color) {
        this.xCoord = x;
        this.yCoord = y;
        this.lengthBoost = lengthBoost;
        this.speedFactor = speedFactor;
        this.color = color;
    }

    /**
     * Method that returns the row of the GFuel.
     *
     * @return the row of the GFuel.
     */
    @Override
    public int getX() {
        return this.xCoord;
    }

    /**
     * Method that returns the column of the GFuel.
     * 
     * @return the column of the GFuel.
     */
    @Override
    public int getY() {
        return this.yCoord;
    }

    /**
     * Method that returns the increase in length from Gfuel.
     * 
     * @return the length increase
     */
    public int getLengthIncrease() {
        return this.lengthBoost;
    }

    /**
     * Method that returns the speed factor from Gfuel.
     * 
     * @return the speed factor
     */
    @Override
    public double getSpeedFactor() {
        return this.speedFactor;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public String getType() {
        return "GFuel";
    }
}
