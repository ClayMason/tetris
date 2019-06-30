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

    final int MAX_ROLL = 6;
    int roll = 0;

    int x_, y_;

    int active_block_index;
    int next_block_index;
    int[][] shape;
    int[][] next_shape;
    Random rand;

    int score;

    public TetrisGame () {

        shape = null;
        next_shape = null;

        rand = new Random();
        score = 0;
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

        active_block_index = -1;
    }

    public void start (){// throws java.io.IOException {

        // start the tetris game
        System.out.print("Game started\n");
        EventQueue.invokeLater(() -> {
            frame.setVisible(true);
        });

        while ( true ) {

            // choose block

            repaint ();
            try {

                Thread.sleep ( 100 );
            }
            catch (java.lang.InterruptedException e) {
                System.out.print ("interrupted.\n");
            }

            this.update ();

            roll = ( roll + 1 ) % MAX_ROLL;
//            System.out.print(roll + "\n");
            if ( roll == 0 ) {

                lower_shape ( shape );

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

                if ( grid[i][j] - 1 >= 0 ) {
                    Image block = blocks[grid[i][j] - 1];
                    g2d.drawImage(block, x_pos, y_pos, 25, 25, null);
                }

            }
        }

        // draw the "next shape" blocks
        if ( next_shape != null ) {
            for ( int i = 0; i < next_shape.length; ++i ) {
                for ( int j = 0; j < next_shape[0].length; ++j ) {
                    // draw block
                    if (next_shape[i][j] == 1) {
                        int y_pos = (25 * i) + 100;
                        int x_pos = (25 * j) + 40;
                        g2d.drawImage ( blocks[next_block_index], x_pos, y_pos, 25, 25, null);
                    }
                }
            }
        }

        // draw score
        g2d.setColor(Color.WHITE);
        g2d.drawString ("Score: " + this.score, 250, 50);
    }

    private void update () {

        // case 1: no blocks chosen yet
        if (active_block_index == -1) {
            System.out.println ("Choosing new block.");
            // choose a block.
            int shape_index = rand.nextInt( shapes.size() );

            System.out.println ("shape_index: " + shape_index + ", shapes size: " + shapes.size());

            // shape = shapes.get( shape_index );
            if ( next_shape == null ) {
                shape = shapes.get(shape_index);
                next_shape = shapes.get( rand.nextInt (shapes.size()) );
                next_block_index = rand.nextInt(blocks.length);
                active_block_index = rand.nextInt(blocks.length);
            }

            else {
                shape = next_shape;
                next_shape = shapes.get(shape_index);

                active_block_index = next_block_index;
                next_block_index = rand.nextInt(blocks.length);
            }

            console_shape (shape);

            x_ = grid[0].length/2;
            y_ = 0;

            System.out.println ("active block index: " + active_block_index);

            place_shape(shape, x_, y_, active_block_index);
            draw_grid ();
        }
        else {
            // case 2: there's an active block but it's grounded
            if ( y_ == grid.length - shape.length ) {

                lower_shape( shape );

                System.out.println ("Getting new block.");
            }
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

        // if it's at the floor of the grid, then it is grounded
        if ( y_+1 > grid.length - shape.length ) return true;

        // otherwise, check if there is a block under any of the blocks of the shape
        for ( int i = y_; i < y_ + shape.length; ++i ) {
            for ( int j = x_; j < x_ + shape[0].length; ++j ) {

                // if shape has block here and lower block in grid has a block, return true
                boolean current_filled = shape[i-y_][j-x_] == 1;
                boolean lower_empty = true;

                if ( i-y_+1 < shape.length ) {
                    if (shape[i-y_+1][j-x_] == 0 && i+1 < grid.length && grid[i+1][j] != 0 )
                        lower_empty = false;
                }
                else if ( i+1 < grid.length && grid[i+1][j] != 0  ) {
                    lower_empty = false;
                }

                if ( current_filled && !lower_empty ) return true;

            }
        }

        return false;
    }

    void console_shape (int[][] shape_) {

        for ( int i = 0; i < shape_.length; ++i ) {
            for ( int j = 0; j < shape_[0].length; ++j ) {
                System.out.print( (shape_[i][j] == 1 ? "X " : "  ") );
            }
            System.out.print("\n");
        }

    }
    void draw_grid () {
        for ( int i = 0; i < grid.length; ++i ) {
            for ( int j = 0; j < grid[0].length; ++j ) {
                System.out.print(grid[i][j] + " ");
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

    void place_shape (int[][] shape_, int x, int y, int block_index) {

        for ( int i = x; i < x + shape_[0].length && i < grid[0].length; ++i ) {
            for ( int j = y; j < y + shape_.length && j < grid.length; ++j ) {
                if (shape_[j-y][i-x] == 1) {
                    grid[j][i] = block_index + 1;
                }
            }
        }
    }

    void lower_shape ( int[][] shape ) {

        if ( grounded() ) {//y_+1 > grid.length - shape.length ) {
            active_block_index = -1;
            System.out.println ("Done...");

            // check if there are any filled rows
            clear_rows ();
        }
        else {
            clear_shape(shape, x_, y_);
            y_ += 1;
            place_shape(shape, x_, y_, active_block_index);
        }

        System.out.println("shape lowered... (" + x_ + " ," + y_ + ")");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:

                clear_shape(shape, x_, y_);
                shape = rotate(shape);
                if ( x_ > grid[0].length - shape[0].length )
                    x_ = grid[0].length - shape[0].length;

                place_shape(shape, x_, y_, active_block_index);
                break;

            case KeyEvent.VK_A:

                if ( left_clear() ) {
                    clear_shape(shape, x_, y_);
                    x_ = x_ - 1 < 0 ? 0 : x_ - 1;
                    place_shape(shape,x_, y_, active_block_index);
                }
                break;

            case KeyEvent.VK_D:

                if ( right_clear() ) {
                    clear_shape(shape, x_, y_);
                    x_ = x_ + 1 > grid[0].length - shape[0].length ? x_ : x_ + 1;
                    place_shape(shape,x_, y_, active_block_index);
                }
                break;

            case KeyEvent.VK_S:

                lower_shape(shape);
                break;
        }
    }

    boolean right_clear () {
        for ( int i = y_; i < y_ + shape.length; ++i ) {
            for ( int j = x_; j < x_ + shape[0].length; ++j ) {

                // if shape has block here and lower block in grid has a block, return true
                boolean current_filled = shape[i-y_][j-x_] == 1;
                boolean right_empty = true;

                if ( j-x_+1 < shape[0].length ) {
                    if (shape[i-y_][j-x_+1] == 0 && j+1 < grid[0].length && grid[i][j+1] != 0 )
                        right_empty = false;
                }
                else if ( j+1 < grid[0].length && grid[i][j+1] != 0  ) {
                    right_empty = false;
                }

                if ( current_filled && !right_empty ) return false;

            }
        }

        return true;
    }

    boolean left_clear () {
        for ( int i = y_; i < y_ + shape.length; ++i ) {
            for ( int j = x_; j < x_ + shape[0].length; ++j ) {

                // if shape has block here and lower block in grid has a block, return true
                boolean current_filled = shape[i-y_][j-x_] == 1;
                boolean left_empty = true;

                if ( j-x_-1 >= 0 ) {
                    if (shape[i-y_][j-x_-1] == 0 && j-1 >= 0 && grid[i][j-1] != 0 )
                        left_empty = false;
                }
                else if ( j-1 >= 0 && grid[i][j-1] != 0  ) {
                    left_empty = false;
                }

                if ( current_filled && !left_empty ) return false;

            }
        }

        return true;
    }

    void clear_rows () {

        System.out.println ("Clearing rows...");

        for ( int i = 0; i < grid.length; ++i ) {
            boolean row_filled = true;

            //String row_ = "";

            for ( int j = 0; j < grid[0].length; ++j ) {
                if ( grid[i][j] == 0 ) {
                    //row_ += " ";
                    row_filled = false;
                    break;
                }

                //row_ += "X";
            }
            //System.out.println(row_);

            if ( row_filled ) {

                // move down all the parts above
                int row_iterations = 0;
                for ( int a = i - 1; a >= 0; --a ) { // y

                    boolean empty_row = true;

                    for ( int b = 0; b < grid[0].length; ++b ) { //x

                        if (grid[a][b] != 0) empty_row = false;

                        grid[a+1][b] = grid[a][b];
                        grid[a][b] = 0;
                    }

                    row_iterations += 1;

                    // no static blocks can exist above an empty row, so when an empty row
                    // is met while trying to clear the full rows, then stop since, there's
                    // guaranteed to be no more blocks above to check for.
                     if ( empty_row ) break;
                }

                // add score
                this.score += 100;
            }
            //else System.out.println("row isnt filled");
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
