import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.KeyListener;

public class TetrisGame extends JPanel implements KeyListener {

    Image background;
    Image[] blocks;
    int[][] grid;
    JFrame frame;
    Random rand_;
    ArrayList<int[][]> shapes;

    int x_, y_;

    int active_block_index;
    int[][] shape;

    public TetrisGame () {

        x_ = 0;
        y_ = 0;
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
        frame.addKeyListener(this);

        // testing code ...
        Random rand = new Random();
        shape = shapes.get( rand.nextInt( shapes.size() ) );
        x_ = 3;
        y_ = 3;
        place_shape(shape, x_, y_, 2);
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

            // this.update ();
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

                if ( grid[i][j] - 1 >= 0 ) {
                    Image block = blocks[grid[i][j] - 1];
                    g2d.drawImage(block, x_pos, y_pos, 25, 25, null);
                }

            }
        }
    }

    private void update () {

        // case 1: no blocks chosen yet
        if (active_block_index == -1) {
            // choose a block.
        }
        else {
            // case 2: there's an active block but it's grounded
            // if ( grounded() )
            // case 3: there's an active block but it's not grounded

        }

    }

    int[][] rotate (int[][] shape) {

        int[][] rotated_shape = new int[shape[0].length][shape.length];

        for ( int i = 0; i < rotated_shape.length; ++i ) {
            for ( int j = 0; j < rotated_shape[0].length; ++j ) {


                rotated_shape[i][j] = shape[j][ shape[0].length-1-i ];

            }
        }

        return rotated_shape;
    }

    boolean grounded () {
        // given the block index ( and class variables x_, y_, and grid ), determine if the
        // shape is grounded
        return false;
    }

    void console_shape (int[][] shape) {

        for ( int i = 0; i < shape.length; ++i ) {
            for ( int j = 0; j < shape[0].length; ++j ) {
                System.out.print( (shape[i][j] == 1 ? "X " : "  ") );
            }
            System.out.print("\n");
        }

    }

    void clear_shape (int[][] shape, int x, int y) {
        for ( int i = x; i < x + shape[0].length && i < grid[0].length; ++i ) {
            for ( int j = y; j < y + shape.length && j < grid.length; ++j ) {
                if (shape[j-y][i-x] == 1) {
                    grid[j][i] = 0;
                }
            }
        }
    }

    void place_shape (int[][] shape, int x, int y, int block_index) {

        for ( int i = x; i < x + shape[0].length && i < grid[0].length; ++i ) {
            for ( int j = y; j < y + shape.length && j < grid.length; ++j ) {
                if (shape[j-y][i-x] == 1) {
                    grid[j][i] = block_index;
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                System.out.println("Rotate");

                clear_shape(shape, x_, y_);
                shape = rotate(shape);
                if ( x_ > grid[0].length - shape[0].length )
                    x_ = grid[0].length - shape[0].length;

                place_shape(shape, x_, y_, 2);
                break;

            case KeyEvent.VK_A:
                System.out.println("Left");

                clear_shape(shape, x_, y_);
                x_ = x_ - 1 < 0 ? 0 : x_ - 1;
                place_shape(shape,x_, y_, 2);
                break;

            case KeyEvent.VK_D:
                System.out.println("Right");

                clear_shape(shape, x_, y_);
                x_ = x_ + 1 > grid[0].length - shape[0].length ? x_ : x_ + 1;
                place_shape(shape,x_, y_, 2);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
