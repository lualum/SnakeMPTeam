import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import javax.swing.*;

/**
 * @author Anu Datar
 * 
 *         Changed block size and added a split panel display for next block and
 *         Score
 * 
 * @author Ryan Adolf
 * @version 29 April, 2025
 * 
 *          Fixed the lag issue with block rendering
 *          Removed the JPanel
 */
// Used to display the contents of a game board
public class BlockDisplay extends JComponent implements KeyListener {
    public static final int BLOCKSIZE = Main.BLOCKSIZE * 3;
    private static final Color BACKGROUND1 = new Color(170, 215, 82);
    private static final Color BACKGROUND2 = new Color(162, 209, 73);
    private static final Color BORDER = new Color(230, 231, 234);
    private static final int BORDERSIZE = 10;
    public static Semaphore semaphore;
    public static Font font;
    private int countDownValue = 0;
    private int dx = Main.CHAOSSPEED;
    private int dy = Main.CHAOSSPEED;

    private Board board;
    private JFrame frame;
    private ArrowListener listener;

    private Image[] img = new Image[2];

    public int victor = 0;

    private CountDownLatch latch = new CountDownLatch(1);

    // Constructs a new display for displaying the given board
    public BlockDisplay(Board board, ArrowListener listener) {
        this.board = board;
        this.listener = listener;
        img[0] = new ImageIcon("trophy.png").getImage();
        img[1] = new ImageIcon("skull.png").getImage();

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
            latch.countDown();
        });

        // Wait until display has been drawn
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        // Create and set up the window.
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setUndecorated(true); // optional: remove title bar
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(this);
        frame.addKeyListener(this);

        // Display the window.
        this.setPreferredSize(new Dimension(
                BLOCKSIZE * board.getCols() + BORDERSIZE * 2,
                BLOCKSIZE * board.getRows() + BORDERSIZE * 2));
        // Center window
        frame.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        ImageIcon icon = new ImageIcon("icon.png");
        frame.setIconImage(icon.getImage());

        frame.setVisible(true);
    }

    public void drawString(String s, int x, int y, Color c, Graphics2D g2d) {
        font = new Font("Monospaced", Font.BOLD, getHeight() / 4);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(c);

        FontMetrics metrics = g2d.getFontMetrics(font);
        int textWidth = metrics.stringWidth(s);
        int textHeight = metrics.getAscent(); // Use ascent only for baseline positioning

        // Draw the string centered at (x, y)
        g2d.drawString(s, x - textWidth / 2, y + textHeight / 2);
    }

    public void drawImage(int id, int x, int y, int w, Graphics2D g2d) {
        int h = w * img[id].getHeight(null) / img[id].getWidth(null);
        g2d.drawImage(img[id], x - w / 2, y - h / 2, w, h, null);
    }

    public void paintSnake(Snake snake, Graphics2D g2d) {
        if (snake == Main.player) {
            if (snake.color == Color.WHITE) {
                g2d.setColor(new Color(204, 233, 252));
            } else {
                g2d.setColor(new Color(139, 0, 0));
            }
        } else {
            g2d.setColor(snake.color);
        }

        g2d.setStroke(new BasicStroke(Main.FULLWIDTH ? BLOCKSIZE : 2 * BLOCKSIZE / 3));
        for (int i = 1; i < snake.SnakeBlocks.size(); i++) {
            int c = snake.SnakeBlocks.get(i).getY();
            int d = snake.SnakeBlocks.get(i).getX();
            int a = snake.SnakeBlocks.get(i - 1).getY();
            int b = snake.SnakeBlocks.get(i - 1).getX();

            if (Math.abs(a - c) <= 1 && Math.abs(b - d) <= 1) {
                g2d.drawLine(
                        a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                        b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                        c * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                        d * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
            } else {
                if (a == c) {
                    if (b < d) {
                        g2d.drawLine(
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                (b - 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                        g2d.drawLine(
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                (d + 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                d * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                    } else {
                        g2d.drawLine(
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                (b + 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                        g2d.drawLine(
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                (d - 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                d * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                    }
                } else {
                    if (a < c) {
                        g2d.drawLine(
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                (a - 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                        g2d.drawLine(
                                (c + 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                c * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                    } else {
                        g2d.drawLine(
                                a * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                (a + 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                        g2d.drawLine(
                                (c - 1) * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                c * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE,
                                b * BLOCKSIZE + BLOCKSIZE / 2 + BORDERSIZE);
                    }
                }
            }
        }
    }

    public void paintSnakes(Snake[] snakes, Graphics2D g2d) {
        for (Snake snake : snakes) {
            paintSnake(snake, g2d);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(BORDER);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if ((row + col) % 2 == 0)
                    g.setColor(BACKGROUND1);
                else
                    g.setColor(BACKGROUND2);
                g.fillRect(col * BLOCKSIZE + BORDERSIZE, row * BLOCKSIZE + BORDERSIZE,
                        BLOCKSIZE, BLOCKSIZE);
                Block square = board.get(row, col);
                if (square != null) {
                    g.setColor(square.getColor());
                    if (Main.FULLWIDTH) {
                        g.fillRect(col * BLOCKSIZE + BORDERSIZE, row * BLOCKSIZE + BORDERSIZE,
                                BLOCKSIZE, BLOCKSIZE);
                    } else {
                        g.fillRect(col * BLOCKSIZE + BLOCKSIZE / 6 + BORDERSIZE,
                                row * BLOCKSIZE + BLOCKSIZE / 6 + BORDERSIZE,
                                BLOCKSIZE * 2 / 3, BLOCKSIZE * 2 / 3);
                    }
                }
            }
        }

        Graphics2D g2d = (Graphics2D) g;
        paintSnakes(Main.team1, g2d);
        paintSnakes(Main.team2, g2d);

        if (board.getGameStopped()) {
            g.setColor(new Color(255, 0, 0, 128));
            g.fillRect(BORDERSIZE, BORDERSIZE, board.getCols() * BLOCKSIZE / 2, getHeight() - BORDERSIZE * 2);
            g.setColor(new Color(0, 0, 255, 128));
            g.fillRect(BORDERSIZE + board.getCols() * BLOCKSIZE / 2, BORDERSIZE, board.getCols() * BLOCKSIZE / 2,
                    getHeight() - BORDERSIZE * 2);
            drawString(String.valueOf(Main.score1), getWidth() / 4, getHeight() / 2 + getHeight() / 24, Color.WHITE,
                    g2d);
            drawString(String.valueOf(Main.score2), 3 * getWidth() / 4, getHeight() / 2 + getHeight() / 24, Color.WHITE,
                    g2d);
            if (victor == 3) {
                drawImage(0, getWidth() / 4,
                        getHeight() * 7 / 20 + getHeight() / 24,
                        getHeight() / 12,
                        g2d);
                drawImage(0, 3 * getWidth() / 4,
                        getHeight() * 7 / 20 + getHeight() / 24,
                        getHeight() / 12,
                        g2d);
            } else {
                drawImage(0, getWidth() / 4 + ((victor == 1) ? 0 : getWidth() / 2),
                        getHeight() * 7 / 20 + getHeight() / 24,
                        getHeight() / 12,
                        g2d);
            }
            // drawImage(1, getWidth() / 4 + ((victor == 2) ? 0 : getWidth() / 2),
            // getHeight() * 7 / 20 + getHeight() / 24,
            // getHeight() * 2 / 11,
            // g2d);
        } else {
            setTitle(Main.score1 + " - " + Main.score2);
        }

        if (countDownValue > 0) {
            drawString(String.valueOf(countDownValue), getWidth() / 2, getHeight() / 2, Color.YELLOW, g2d);
        }
    }

    // Redraws the board to include the pieces and border colors.
    public void showBlocks() {
        repaint();
    }

    public void moveWindow() {
        Point location = frame.getLocation();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int newX = location.x + dx;
        int newY = location.y + dy;
        if (newX < 0 || newX + getWidth() > screenSize.width) {
            dx = -dx;
        }
        if (newY < 40 || newY + getHeight() > screenSize.height) {
            dy = -dy;
        }
        frame.setLocation(location.x + dx, location.y + dy);
    }

    public void showWin(int n) {
        if (n == 3) {
            setTitle("DRAW!");
        } else {
            setTitle("Player " + n + " Wins!");
        }
        victor = n;
        repaint();
    }

    // public void countdown(int x) {
    // countDownValue = x;
    // semaphore = new Semaphore(0);

    // // Schedule the countdown loop off the EDT
    // new Thread(() -> {
    // while (countDownValue > 0) {
    // repaint();

    // try {
    // Thread.sleep(1000);
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // }
    // countDownValue--;
    // }
    // repaint();
    // semaphore.release();
    // System.out.println("Countdown done");
    // }).start();
    // }

    // Sets the title of the window.
    public void setTitle(String title) {
        frame.setTitle((Main.isHost ? "Host: " : "Client: ") + title);
    }

    /**
     * In case when key event e is typed
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * In case when key event e is released
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * In case when key event e is pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (listener == null)
            return;
        listener.keyPressed(e);
    }

    /**
     * This implements and sets arrow listener
     * 
     * @param listener
     */
    public void setArrowListener(ArrowListener listener) {
        this.listener = listener;
    }

    public void getVictor(int n) {
        victor = n;
    }
}
