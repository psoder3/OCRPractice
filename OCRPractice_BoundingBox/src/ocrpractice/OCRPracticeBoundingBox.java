/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocrpractice;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author psoderquist
 */
public class OCRPracticeBoundingBox {

    public static ArrayList<Integer> runLengths = new ArrayList();
    public static ArrayList<Point> allVisitedPoints = new ArrayList();
    public static String GetPixelColor(File file, int x, int y)
    {
        String color = "";
        try {
            BufferedImage image = ImageIO.read(file);
            // Getting pixel color by position x and y
            int clr=  image.getRGB(x,y);
            int  red   = (clr & 0x00ff0000) >> 16;
            int  green = (clr & 0x0000ff00) >> 8;
            int  blue  =  clr & 0x000000ff;
            color += "Red Color value = "+ red + "\n";
            color += "Green Color value = "+ green + "\n";
            color += "Blue Color value = "+ blue + "\n";
        } catch (IOException ex) {
            Logger.getLogger(OCRPracticeBoundingBox.class.getName()).log(Level.SEVERE, null, ex);
        }
      return color;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("What image file would you like to try character recognition on?");
        Scanner s = new Scanner(System.in);
        String filename = s.nextLine();
        File f = new File(filename);
        File grayscale = changeToGrayScale(f);
        //ArrayList<Point> edgePoints = findEdges(grayscale);
        //drawEdgeFile(edgePoints, grayscale);
    }

    private static File changeToGrayScale(File file) 
    {
        try {
            BufferedImage image = ImageIO.read(file);
            
            for(int i = 0; i < image.getHeight(); i++)
            {
                for(int j = 0; j < image.getWidth(); j++){
                    
                    int clr=  image.getRGB(j,i);
                    int  red   = (clr & 0x00ff0000) >> 16;
                    int  green = (clr & 0x0000ff00) >> 8;
                    int  blue  =  clr & 0x000000ff;
                    
                    int average = (red + green + blue) / 3;
                    
                    Color grayScalePixel = new Color(average, average, average); // Color grayscaled
                    int rgb = grayScalePixel.getRGB();
                    image.setRGB(j,i,rgb);
                }
            }
            

            File outputfile = new File("grayscale.png");
            ImageIO.write(image, "png", outputfile);
            return outputfile;
        } catch (IOException ex) {
            Logger.getLogger(OCRPracticeBoundingBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    private static void drawEdgeFile(ArrayList<Point> points, File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            Color white = new Color(255, 255, 255);
            Color black = new Color(0, 0, 0); // Color grayscaled
            int blackrgb = black.getRGB();
            int whitergb = white.getRGB();
            for(int i = 0; i < image.getHeight(); i++)
            {
                for(int j = 0; j < image.getWidth(); j++){                    
                    image.setRGB(j,i,whitergb);
                }
            }
            for (Point p : points)
            {
                if (p.x < 0 || p.y < 0 || p.x > image.getWidth()-1 || p.y > image.getHeight()-1)
                {
                    continue;
                }
                image.setRGB(p.x,p.y,blackrgb);
            }

            File outputfile = new File("thinnedEdges.png");
            ImageIO.write(image, "png", outputfile);
            
        } catch (IOException ex) {
            Logger.getLogger(OCRPracticeBoundingBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /*
    Iterate. 
    At each non white point create a list of points.
    continuously add all touching non white points to list. 
    Find bounding box of points in list. 
    Save bounding points into a character object. 
    Then search for the greatest average pixel value over a square bounding box. 
    Use that pixel as starting point. 
    Check bounding box 5 pixels up, 5 down, right, left, 
    4 in diagonals for greatest average pixel value. (Greedy algorithm to choose next pixel direction). 
    Don't include direction of point we just came from in possible next directions. 
    Finish when we reach a point already included, or when we reach a box above with an all white top row, ect.
    */
    public static void findEdges(File f)
    {
        try {
            BufferedImage image = ImageIO.read(f);
            
            for (int i = 0; i < image.getHeight(); i++)
            {
                for (int j = 0; j < image.getWidth(); j++)
                {
                    if (getPixelValue(j,i, image) < 255)
                    {
                        ArrayList<Point> visitedPoints = new ArrayList();
                        visitedPoints.add(new Point(j,i));
                        int previousNumberPoints;
                        do
                        {
                            previousNumberPoints = visitedPoints.size();
                            visitedPoints = addNeighborPoints(visitedPoints, image);
                        } while (visitedPoints.size() > previousNumberPoints);
                        PixeledCharacter pc = createPixeledCharacterObject(visitedPoints);
                        allVisitedPoints.addAll(visitedPoints);
                    }
                }
            }       
            
        } catch (IOException ex) {
            Logger.getLogger(OCRPracticeBoundingBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static PixeledCharacter createPixeledCharacterObject(ArrayList<Point> visitedPoints) {
        PixeledCharacter pc = new PixeledCharacter();
        for (int ) 
    }
    
    private static ArrayList<Point> addNeighborPoints(ArrayList<Point> visitedPoints, BufferedImage image) {
        
        for (Point p : visitedPoints)
        {
            Point pLeft = new Point(p.x-1,p.y);
            Point pRight = new Point(p.x+1,p.y);
            Point pUp = new Point(p.x,p.y-1);
            Point pDown = new Point(p.x,p.y+1);
            if (!visitedPoints.contains(pLeft) && getPixelValue(pLeft.x,pLeft.y,image) < 255)
            {
                visitedPoints.add(pLeft);
            }
            if (!visitedPoints.contains(pRight) && getPixelValue(pRight.x,pRight.y,image) < 255)
            {
                visitedPoints.add(pRight);
            }
            if (!visitedPoints.contains(pUp) && getPixelValue(pUp.x,pUp.y,image) < 255)
            {
                visitedPoints.add(pUp);
            }
            if (!visitedPoints.contains(pDown) && getPixelValue(pDown.x,pDown.y,image) < 255)
            {
                visitedPoints.add(pDown);
            }
        }
        return visitedPoints;
    }
    
    
    private static int getPixelValue(int x, int y, BufferedImage image) {
        //System.out.println(x + " " + y);
        if (x < 0 || y < 0)
        {
            return 0;
        }
        int currRGB = image.getRGB(x, y);
        return (currRGB & 0x00ff0000) >> 16;
    }

    private static boolean containsPoint(ArrayList<Point> points, int x, int y) {
        for (Point p : points)
        {
            if (p.x == x && p.y == y)
            {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<Point> filterNoise(ArrayList<Point> points) {
        ArrayList<Point> filteredPoints = new ArrayList();
        for (Point p : points)
        {
            boolean hasNeighbor = false;
            int numNeighbors = 0;
            for (Point p2 : points)
            {
                if (p.x == p2.x && p.y == p2.y)
                {
                    continue;
                }
                else if (p.x == p2.x+1 && p.y == p2.y)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
                else if (p.x == p2.x-1 && p.y == p2.y)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
                else if (p.x == p2.x && p.y == p2.y+1)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
                else if (p.x == p2.x && p.y == p2.y-1)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
                else if (p.x == p2.x+1 && p.y == p2.y+1)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
                else if (p.x == p2.x+1 && p.y == p2.y-1)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
                else if (p.x == p2.x-1 && p.y == p2.y+1)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
                else if (p.x == p2.x-1 && p.y == p2.y-1)
                {
                    hasNeighbor = true;
                    numNeighbors++;
                }
            }
            if (hasNeighbor)
            {
                p.neighbers = numNeighbors;
                filteredPoints.add(p);
            }
        }
        return filteredPoints;
    }

    

    

          
    
}
