import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Snake implements KeyListener {
    private JFrame frame;
    private SnakeBody snakeBody;
    private SnakePoint head, tail, food;
    private final int bodySize = 10;
    private final int xsize = 300, ysize = 300;
    private int dirX = 0, dirY = 1;
    private boolean noFood, hit;
    private int[][] matrix;

    public static void main(String[] args) {
        Snake snake = new Snake();
    }

    public Snake() {
        // initialize GUI
        // initialize snake (two or three points)
        while(true) {
            init();
            go();

            System.out.println("Try again? (y/n)");
            Scanner s = new Scanner(System.in);
            if (s.next().equals("y")) continue;
            else break;
        }

        System.exit(0);
    }

    private void init() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(xsize,ysize));

        snakeBody = new SnakeBody();
        frame.add(snakeBody);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        
        frame.addKeyListener(this);
    }

    private void go() {
        Random rand = new Random();
        int numOfPoints = xsize / bodySize;

        while(true) {
            if(hit) break;

            while(noFood) {
                // generate a food randomly
                int randX = rand.nextInt(numOfPoints) * bodySize;
                int randY = rand.nextInt(numOfPoints) * bodySize;
                if(matrix[randX][randY] != 0) continue;

                food = new SnakePoint(randX, randY, 1);
                noFood = false;
                matrix[randX][randY] = 1;
                System.out.println("there is food: " + randX + ", " + randY);
            }

            // update head position according to key stroke
            //if new position is empty: updateBody(); // skip if in the same direction of current direction
            
            //else if new position is food: eat(new SnakePoint);

            //else if new position hit body or wall: return dead;

            if(dirX == 1 && food.x - head.x == bodySize && head.y == food.y ||
               dirX == -1 && head.x - food.x == bodySize && head.y == food.y ||
               dirY == 1 && food.y - head.y == bodySize && head.x == food.x ||
               dirY == -1 && head.y - food.y == bodySize && head.x == food.x)
                snakeBody.eat(food);
            else {
                snakeBody.updateBody(new SnakePoint(head.x + dirX * bodySize, head.y + dirY * bodySize, 0));
            }

            try {
                Thread.sleep(200);
            }
            catch (Exception ex) { }
        }
    }

    // 37 - left, 38 - up, 39 - right, 40 - down

    public void keyTyped(KeyEvent e) { }

    public void keyPressed(KeyEvent e) {
        int direction = e.getKeyCode();
        //System.out.println("key pressed:" + direction);
        
        if(direction == 37) {
            if(dirX == 0) {
                dirX = -1; dirY = 0;
            }
        }
        else if(direction == 38) {
            if(dirY == 0) {
                dirX = 0; dirY = -1;
            }
        }
        else if(direction == 39) {
            if(dirX == 0) {
                dirX = 1; dirY = 0;
            }
        }
        else if(direction == 40) {
            if(dirY == 0) {
                dirX = 0; dirY = 1;
            }
        }
    }

    public void keyReleased(KeyEvent e) { }

    private class SnakeBody extends JPanel {
    	public SnakeBody() {
            // start with two or three points as body
            matrix = new int[xsize][ysize];
            hit = false;
            noFood = true;
            dirX = 0; dirY = 1;

            head = new SnakePoint(xsize / 2, ysize / 2, 2);
            tail = new SnakePoint(xsize / 2 - bodySize, ysize / 2, 2);
            head.next = tail;
            tail.prev = head;

            matrix[head.x][head.y] = 2;
            matrix[tail.x][tail.y] = 2;

            repaint();
    	}

    	private void eat(SnakePoint food) {
    		food.status = 2;
    		head.prev = food;
    		food.next = head;
    		head = food;

            food = null;
            noFood = true;

            matrix[head.x][head.y] = 2;

            repaint();
    	}

    	private void updateBody(SnakePoint point) {
            matrix[tail.x][tail.y] = 0;
            tail = tail.prev;
            tail.next.prev = null;
            tail.next.status = 0;
            tail.next = null;

    		head.prev = point;
    		point.next = head;
    		head = point;
            point = null;

            if(head.x == xsize || head.x == -bodySize || 
               head.y == ysize || head.y == -bodySize || matrix[head.x][head.y] == 2) {
                hit = true;
                return;
            }

            matrix[head.x][head.y] = 2;
            head.status = 2;

            repaint(); // repaint the whole body? repaint just head and tail?
    	}

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // seperate food and snake body
            if(!noFood) {
                g.setColor(Color.red);
                g.fillRect(food.x, food.y, bodySize, bodySize);
            }

            g.setColor(Color.black);
            SnakePoint p = head;
            while(p != null) {
                g.fillRect(p.x, p.y, bodySize, bodySize);
                p = p.next;
            }
        }        
    }

    // SnakePoint class for each point on screen. It can be empty/food/body.
    private class SnakePoint {
        int x, y; // record the left top cornor of each point of the snake body
        int status; // status of current point: 0 - empty/safe, 1 - food, 2 - body
        SnakePoint next; // next SnakePoint towards the tail direction
        SnakePoint prev; // previous SnakePoint towards the head direction
        SnakePoint(int x, int y, int status) {
        	this.x = x;
        	this.y = y;
        	this.status = status;
        }
    }
}
