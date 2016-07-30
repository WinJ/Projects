import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//http://stackoverflow.com/questions/15776549/create-rectangle-with-mouse-drag-not-draw

public class ScreenCapture4 {
    
    public static void main(String[] args) {
        new ScreenCapture4();
    }

    public ScreenCapture4() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                /* try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                } */

                JFrame frame = new JFrame("screen capture");
                frame.setUndecorated(true);
                frame.setBackground(new Color(0, 0, 0, 0));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //frame.setLayout(new BorderLayout());
                frame.add(new CapturePane());
		        frame.add(new ZoomPane());
                Rectangle bounds = getVirtualBounds();
                frame.setLocation(bounds.getLocation());
                frame.setSize(bounds.getSize());
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
                
                
            }
        });
    }

    public class CapturePane extends JPanel {
        //private Point clickPoint;
        private Rectangle selectionBounds;
        private int clickX, clickY;

        public CapturePane() {
            setOpaque(false);

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                    //if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                        System.exit(0);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    //clickPoint = e.getPoint();
                    clickX = e.getX();
                    clickY = e.getY();
                    selectionBounds = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    //clickPoint = null;
                    if (SwingUtilities.isRightMouseButton(e)) return;
                    
                    try {
                        Robot robot = new Robot();
                        String imageFormat = "png";
                        String imageName = "screenshot." + imageFormat;
                        
                        BufferedImage fullScreen = robot.createScreenCapture(selectionBounds);
                        ImageIO.write(fullScreen, imageFormat, new File(imageName));
                        System.out.println("Full screenshot is saved.");
                    } catch (AWTException | IOException ex) {
                        System.err.println(ex);
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    //Point dragPoint = e.getPoint();
                    //int x = Math.min(clickPoint.x, dragPoint.x);
                    //int y = Math.min(clickPoint.y, dragPoint.y);
                    //int width = Math.max(clickPoint.x - dragPoint.x, dragPoint.x - clickPoint.x);
                    //int height = Math.max(clickPoint.y - dragPoint.y, dragPoint.y - clickPoint.y);
                    //selectionBounds = new Rectangle(x, y, width, height);
                    
                    int dragX = e.getX();
                    int dragY = e.getY();
                    selectionBounds = new Rectangle(clickX, clickY, dragX - clickX, dragY - clickY);
                    repaint();
                }
            };

            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);  // what does this mean?
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(255, 255, 255, 100));

            Area fill = new Area(new Rectangle(new Point(0, 0), getSize()));
            if (selectionBounds != null) {
                fill.subtract(new Area(selectionBounds));
            }
            g2d.fill(fill);
            if (selectionBounds != null) {
                g2d.setColor(Color.BLACK);
                g2d.draw(selectionBounds);
            }
            g2d.dispose();
        }
    }

    private class ZoomPane extend JPanel {
	private BufferedImage image 
	private Robot robot = new Robot();

	MouseAdapter mouseHandler = new MouseAdapter() {
		public void mouseMoved(MouseEvent e) {
		    int x = e.getX();
		    int y = e.getY();
		    BufferedImage temp = robot.createScreenCapture(new Rectangle(x-size/4, y-size/4, size/2, size/2));
		}
	}
	addMouseMotionListener(mouseHandler);
    }

    public static Rectangle getVirtualBounds() {
        Rectangle bounds = new Rectangle(0, 0, 0, 0);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice lstGDs[] = ge.getScreenDevices();
        for (GraphicsDevice gd : lstGDs) {
            bounds.add(gd.getDefaultConfiguration().getBounds());
        }
        return bounds;
    }
}
