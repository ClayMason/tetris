import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class TetrisGame extends JPanel {

    Image background;
    Image[] blocks;
    int[][] grid;
    JFrame frame;
    Random rand_;
    ArrayList<int[][]> shapes;

    public TetrisGame () {

        rand_ = new Random ();

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

        /* setup the shapes
        *
        *  0: X X
        *     X X
        *
        *  1: X X X X
        *
        *  2: X
        *     X
        *     X X
        *
        *  3: X
        *     X X
        *       X
        *
        *  4:   X
        *     X X X
        *
        */
        int[][] shape_0 = new int[][] {{1, 1},{1, 1}};
        int[][] shape_1 = new int[][] {{1, 1, 1, 1}};
        int[][] shape_2 = new int[][] {{1, 0},{1, 0},{1, 1}};
        int[][] shape_3 = new int[][] {{1, 0},{1, 1},{0, 1}};
        int[][] shape_4 = new int[][] {{0, 1, 0},{1, 1, 1}};

        shapes = new ArrayList<int[][]> ();
        shapes.add(shape_0);
        shapes.add(shape_1);
        shapes.add(shape_2);
        shapes.add(shape_3);
        shapes.add(shape_4);
        
        // set frame
        frame = new JFrame("Tetris Game");
        frame.add(this);
        frame.setSize(450,  689);
        // frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }

    public void start (){// throws java.io.IOException {

        // start the tetris game
        System.out.print("Game started\n");
        EventQueue.invokeLater(() -> {
            frame.setVisible(true);
        });

        while ( true ) {
            repaint ();
            try {

                Thread.sleep ( 100 );
            }
            catch (java.lang.InterruptedException e) {
                System.out.print ("interrupted.\n");
            }
        }

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
                int y_pos = 150 + (i * 25);

                Image block = blocks[grid[i][j] - 1];
                g2d.drawImage(block, x_pos, y_pos, 25, 25, null);

                grid[i][j] = rand_.nextInt(6)+1;

            }
        }
    }

}
