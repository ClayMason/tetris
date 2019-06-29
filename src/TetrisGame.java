import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.Random;

public class TetrisGame extends JPanel {

    Image background;
    Image[] blocks;
    int[][] grid;

    public TetrisGame () {

        this.setup ();
        System.out.print (background.getWidth(null) + ", " + background.getHeight(null));
    }

    private void setup () {
        System.out.print ("Setting up...\n");
        // setting up sprites
        try {
            this.background = ImageIO.read ( new File("sprites/background.png") );

            this.blocks = new Image[6];
            blocks[0] = ImageIO.read ( new File("sprites/blue.png") );
            blocks[1] = ImageIO.read ( new File("sprites/green.png") );
            blocks[2] = ImageIO.read ( new File("sprites/orange.png") );
            blocks[3] = ImageIO.read ( new File("sprites/purple.png") );
            blocks[4] = ImageIO.read ( new File("sprites/red.png") );
            blocks[5] = ImageIO.read ( new File("sprites/yellow.png") );

            System.out.print ("Images loaded successfully.\n");
        }
        catch (java.io.IOException e) {
            System.out.print("Error reading image files.\n");
        }

        // set the grid
        grid = new int[16][10];
        // set random values for grid
        Random rand = new Random();
        for ( int i = 0; i < grid.length; ++i ) {
            for ( int j = 0; j < grid[0].length; ++j ) {
                grid[i][j] = rand.nextInt(6);
                grid[i][j] += 1;
                // grid values b/w [1-6] ( 0 is reserved for empty block )
            }
        }

        // set frame
        JFrame frame = new JFrame("Tetris Game");
        frame.add(this);
        frame.setSize(450,  639);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void start (){// throws java.io.IOException {

        // start the tetris game
        System.out.print("Game started\n");

    }

    @Override
    public void paint (Graphics g) {
        super.paintComponent (g);

        Graphics2D g2d = (Graphics2D) g;

        // draw background
        g2d.drawImage (background, 0, 0, null);

        // draw the blocks
        for ( int i = 0; i < grid.length; ++i ) {
            for ( int j = 0; j < grid[0].length; ++j ) {

                int x_pos = 100 + (j * 25);
                int y_pos = 100 + (i * 25);

                Image block = blocks[grid[i][j] - 1];
                g2d.drawImage(block, x_pos, y_pos, 25, 25, null);

            }
        }
    }

}
