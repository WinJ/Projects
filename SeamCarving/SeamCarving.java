import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class SeamCarving {
    private JFrame frame;
    private BufferedImage image;
    private int[][] energy;
    private int[] verticalSeam;
    private int seamStart = 0;
    private SeamFinder seamFinder;

    public static void main(String[] args) {
        String imagePath = args[0];
        int N = Integer.parseInt(args[1]);
        SeamCarving sc = new SeamCarving(imagePath, N);
    }

    // image as input image, N means how many seams to remove
    public SeamCarving(String imagePath, int N) {
        displayImage(imagePath);

        try {
            Thread.sleep(200);
        } catch(Exception ex) {}

        findSeamCarving(N);
    }

    // display image
    private void displayImage(String imagePath) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        readImage(imagePath);
        frame.getContentPane().setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
        seamFinder = new SeamFinder(image);
        frame.getContentPane().add(seamFinder);
        frame.pack();
        frame.setVisible(true);
    }

    private void findSeamCarving(int N) {
        seamFinder.calEnergy();
        seamFinder.calAccEnergy();
        for(int i = 0; i < N; i++) {
            seamFinder.updateAccEnergy();
        }
    }

    private void readImage(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
        } catch(IOException e) {}

    }

    private class SeamFinder extends JPanel {
        public SeamFinder(BufferedImage image) {
            repaint();
        }

        // calculate energy or grediant
        private void calEnergy() {
            int sizeY = image.getHeight();
            int sizeX = image.getWidth();
            energy = new int[sizeY][sizeX];
            //System.out.println(sizeX + "x" + sizeY);

            for(int i = 0; i < sizeY; i++) {
                for(int j = 0; j < sizeX; j++) {
                    // add the border case
                    Color rgb_in1 = new Color(image.getRGB(j, i == 0 ? 0 : i - 1)), rgb_ip1 = new Color(image.getRGB(j, i == sizeY - 1 ? sizeY - 1 : i + 1));
                    Color rgb_jn1 = new Color(image.getRGB(j == 0 ? 0 : j - 1, i)), rgb_jp1 = new Color(image.getRGB(j == sizeX - 1 ? sizeX - 1 : j + 1, i));

                    int rx = rgb_in1.getRed() - rgb_ip1.getRed();
                    int gx = rgb_in1.getGreen() - rgb_ip1.getGreen();
                    int bx = rgb_in1.getBlue() - rgb_ip1.getBlue();

                    int ry = rgb_jn1.getRed() - rgb_jp1.getRed();
                    int gy = rgb_jn1.getGreen() - rgb_jp1.getGreen();
                    int by = rgb_jn1.getBlue() - rgb_jp1.getBlue();

                    // check if overflow
                    energy[i][j] = rx*rx + gx*gx + bx*bx + ry*ry + gy*gy + by*by;
                }
            }
        }

        // calculate accumulated energy
        private void calAccEnergy() {
            int sizeX = energy[0].length;
            int sizeY = energy.length;

            for(int i = sizeY - 2; i >= 0; i--) {
                for(int j = 0; j < sizeX; j++) {
                    energy[i][j] += j == 0 ? Math.min(energy[i+1][j], energy[i+1][j+1]) : j == sizeX - 1 ? Math.min(energy[i+1][j-1], energy[i+1][j]) : Math.min(energy[i+1][j-1], Math.min(energy[i+1][j], energy[i+1][j+1]));
                }
            }
        }

        // find seam carving path based on accumulated energy
        private void findVerticalSeam() {
            int minStart = Integer.MAX_VALUE;
            for(int j = 1; j < energy[0].length-1; j++) {
                if(energy[0][j] < minStart) {
                    minStart = energy[0][j];
                    seamStart = j;
                }
            }

            verticalSeam = new int[energy.length];
            verticalSeam[0] = seamStart;
            int col = seamStart, minCol = seamStart;

            for(int i = 1; i < energy.length; i++) {
                int min = energy[i][col];
                for(int c = col - 1; c <= col + 1; c += 2) {
                    // what if c is out of boundary
                    if(c >= 1 && c < energy[0].length-1 && energy[i][c] < min) {
                        min = energy[i][c];
                        minCol = c;
                    }
                }
                col = minCol;

                verticalSeam[i] = col;
                energy[i][col] = Integer.MAX_VALUE;
                //System.out.println(verticalSeam[i]);
                // mark color for seam
            }
            energy[0][seamStart] = Integer.MAX_VALUE;
            //repaint();
        }


        // remove seam carving path from image
        private void removeSeamFromEnergy() {
            int[][] updatedEnergy = new int[energy.length][energy[0].length-1];
            for(int i = 0; i < energy.length; i++) {
                for(int j = 0; j < verticalSeam[i]; j++) {
                    updatedEnergy[i][j] = energy[i][j];
                }

                for(int j = verticalSeam[i] + 1; j < energy[0].length; j++) {
                    updatedEnergy[i][j-1] = energy[i][j];
                }
            }
            energy = updatedEnergy;
        }

        private void removeSeamFromImage() {
            BufferedImage newImage = new BufferedImage(image.getWidth() - 1, image.getHeight(), image.getType());
            WritableRaster raster = newImage.getRaster();

            Raster originalImageData = image.getData();
            float[] garbArray = new float[image.getWidth() * 3];

            for (int i = 0; i < verticalSeam.length; i++) {
                int seam = verticalSeam[i];
                if (seam > 0) {
                    raster.setPixels(0, i, seam, 1, originalImageData.getPixels(0, i, seam, 1, garbArray));
                }

                if (seam < image.getWidth() - 1) {
                    int widthAfter = newImage.getWidth() - seam;
                    raster.setPixels(seam, i, widthAfter, 1, originalImageData.getPixels(seam + 1, i, widthAfter, 1, garbArray));
                }
            }

            image = newImage;
        }

        // update accumulated energy matrix by removing seam carving path
        private void updateAccEnergy() {
            findVerticalSeam();
            //removeSeamFromEnergy();
            //removeSeamFromImage();

            repaint();
            try{
                Thread.sleep(200);
            }catch(Exception ex) {}
        }

                @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);

            g.setColor(Color.red);
            for(int i = 0; i < verticalSeam.length; i++) {
                g.fillRect(verticalSeam[i], i, 5, 5);
                //System.out.println(verticalSeam[i] + "," + i);
            }
        }

    }

}