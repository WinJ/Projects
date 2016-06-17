import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class Pingpong {
    // starting point and size of pingpong
    int x;
    int y;
    int xsize = 300;
    int ysize = 300;
    int xmouse;
    int radius = 10;
    int tennisWidth = 60;
    int tennisHeight = 8;
        
    JFrame frame;
    MyDrawPanel ppTable;
        
    public static void main(String[] args) {
        boolean again = true;
        Pingpong pp = new Pingpong();
        
        // initialize a JFrame
        pp.init(300,300);
        while (again) {
            again = false;
            
            // start again
            pp.go();
            
            System.out.println("try again? (y/n)");
            Scanner s = new Scanner(System.in);
            String answer = s.next();            
            if (answer.equals("y")) again = true;
        }
        System.exit(0);
    }
    
    public void init(int xmax, int ymax) {
        // initialize the panel and set x, y scale
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ppTable = new MyDrawPanel();
        frame.getContentPane().add(ppTable);
        
        frame.getContentPane().setPreferredSize(new Dimension(xmax,ymax)); // this sets the panel area and doesn't include window decoration.
        // frame.setSize(xmax,ymax); // the size includes window decoration like borderlines, menu bars.
        // frame.setResizable(false);
        frame.pack(); // fit the window size to PreferredSize; not with setSize().
        frame.setVisible(true);
    }    
    
    public void go() {
        // velocity by distance
        x = 100;
        y = 100;
        
        int vx = 1;
        int vy = -3;
        
        while(true) {
            if (y > ysize - radius && (x > xmouse + tennisWidth/2 || x < xmouse - tennisWidth/2)) {
                System.out.println("you are dead!");
                break;
            }
            //System.out.println("y=" + y + ", x=" + x + ", xmouse=" + xmouse);
            if (x < radius || x > xsize - radius) vx = -vx;
            if (y < radius || (y > ysize - radius - tennisHeight && x < xmouse + tennisWidth/2 && x > xmouse - tennisWidth/2)) vy = -vy;
            //if (y <= radius || (y >= 300 - tennisHeight - radius && x >= xmouse-tennisWidth/2 && x <= xmouse+tennisWidth/2) || (y >= 300 - tennisHeight - radius && y <= 300 - radius && Math.pow(x - xmouse - tennisWidth/2, 2) + Math.pow(300 - y - tennisHeight, 2) == Math.pow(radius, 2))) vy = -vy;
            
            x = x + vx;
            y = y + vy;
            
            ppTable.repaint();
  
            try {
                Thread.sleep(10);
            } catch(Exception ex) { }
            
        }
        
    }
        
    class MyDrawPanel extends JPanel implements MouseMotionListener {
        public MyDrawPanel() {
            addMouseMotionListener(this);
        }
        
        public void mouseMoved(MouseEvent e) {
            xmouse = e.getX();
        }
        
        public void mouseDragged(MouseEvent e) {}

    
        public void paintComponent(Graphics g) {
            xsize = this.getWidth();
            ysize = this.getHeight();
            
            g.setColor(Color.white);
            //g.fillRect(0,0,300,300);
            g.fillRect(0,0,xsize,ysize);

            g.setColor(Color.black);
            g.fillOval(x-radius,y-radius,2*radius,2*radius);
            
            g.setColor(Color.blue);
            g.fillRect(xmouse - tennisWidth/2, ysize-tennisHeight, tennisWidth, tennisHeight);
        }
       
    }
}
