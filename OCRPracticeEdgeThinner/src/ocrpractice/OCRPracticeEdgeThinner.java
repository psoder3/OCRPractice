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
public class OCRPracticeEdgeThinner {

    public static ArrayList<Integer> runLengths = new ArrayList();
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
            Logger.getLogger(OCRPracticeEdgeThinner.class.getName()).log(Level.SEVERE, null, ex);
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
        ArrayList<Point> edgePoints = findEdges(grayscale);
        edgePoints = fillGaps(edgePoints, grayscale);
        drawEdgeFile(edgePoints, grayscale);
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
            Logger.getLogger(OCRPracticeEdgeThinner.class.getName()).log(Level.SEVERE, null, ex);
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

            File outputfile = new File("thinnedEdges4.png");
            ImageIO.write(image, "png", outputfile);
            
        } catch (IOException ex) {
            Logger.getLogger(OCRPracticeEdgeThinner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ArrayList<Point> findEdges(File f)
    {
        ArrayList<Point> points = new ArrayList();
        try {
            BufferedImage image = ImageIO.read(f);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            for (int i = 0; i < imageHeight; i++)
            {
                int j = 0;
                while (j < imageWidth)
                {
                    int value = getPixelValue(j,i,image);
                    
                    // start of a run
                    if (value < 255)
                    {
                        //ArrayList<Point> run = new ArrayList();
                        int firstIndex = j;
                        int lastIndex;
                        while (j < imageWidth-1 && value < 255)
                        {
                            // add value to run
                            //Point p = new Point();
                            //p.x = j;
                            //p.y = i;
                            
                            //run.add(p);
                            
                            j++;
                            value = getPixelValue(j,i,image);
                        }
                        lastIndex = j-1;
                        int averageJ = (firstIndex + lastIndex)/2;
                        int runLength = lastIndex - firstIndex;
                        runLengths.add(runLength);
                        Point averagePoint = new Point(averageJ,i);
                        //averagePoint.x = averageJ;
                        //averagePoint.y = i;
                        averagePoint.runLength = runLength;
                        points.add(averagePoint);
                    }
                    
                    j++;
                }
                
            }
            
            for (int i = 0; i < imageWidth; i++)
            {
                int j = 0;
                while (j < imageHeight)
                {
                    int value = getPixelValue(i,j,image);
                    
                    // start of a run
                    if (value < 255)
                    {
                        //ArrayList<Point> run = new ArrayList();
                        int firstIndex = j;
                        int lastIndex;
                        while (j < imageHeight-1 && value < 255)
                        {
                            // add value to run
                            //Point p = new Point();
                            //p.x = j;
                            //p.y = i;
                            
                            //run.add(p);
                            
                            j++;
                            value = getPixelValue(i,j,image);
                        }
                        lastIndex = j-1;
                        int averageJ = (firstIndex + lastIndex)/2;
                        Point averagePoint = new Point(i, averageJ);
                        //averagePoint.y = averageJ;
                        //averagePoint.x = i;
                        int runLength = lastIndex - firstIndex;
                        runLengths.add(runLength);
                        averagePoint.runLength = runLength;

                        points.add(averagePoint);
                    }
                    
                    j++;
                }
                
            }
                    
                    
            
        } catch (IOException ex) {
            Logger.getLogger(OCRPracticeEdgeThinner.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        points = removeLongRuns(points);
        points = filterNoise(points);
        return points;
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

    private static ArrayList<Point> removeLongRuns(ArrayList<Point> points) {
        ArrayList<Point> removedRuns = new ArrayList();
        int total = 0;
        for (Point p : points)
        {
            total += p.runLength;
            //System.out.println(p.runLength);
        }
        int averageRunLength = total/points.size();
        for (Point p : points)
        {
            if (p.runLength < averageRunLength)
            {
                removedRuns.add(p);
            }
            
        }
        return removedRuns;
    }

    private static ArrayList<Point> fillGaps(ArrayList<Point> edgePoints, File grayscale) {
        
        ArrayList<Point> leafPoints = new ArrayList();
        // look at all points
        for (Point p : edgePoints)
        {
            // if point is leaf
            if (p.neighbers == 1)
            {   
                leafPoints.add(p);
            }
        }
        // look at all leaf points
        for (Point p : leafPoints)
        {
            if (p.neighbers > 1)
            {   
                continue;
            }
            // look at all other leaf points
            for (Point p2 : leafPoints)
            {
                // if it isn't the same point
                if (!p.equals(p2))
                {
                    // if there is a complete path between the points
                    Point finalPoint = pathExists(p,p2,grayscale);
                    if (finalPoint != null)
                    {
                        // include path points as a perfect slope (perhaps later we'll do a weighted gradient path for more precision)
                        includePathPoints(edgePoints,p,p2);
                        // update how many neighbors these points now have so we don't mistake them for a gap later
                        p.neighbers=2;
                        p2.neighbers=2;
                    }
                }
            }
        }
        return edgePoints;
    }

    // returns the final point in the path after it is reached through recursion. It will have a backtrace member
    private static Point recursePath(Point currentPoint, Point targetPoint, int pathLength, BufferedImage image)
    {
        pathLength++;
        if (currentPoint.equals(targetPoint))
        {
            return currentPoint;
        }
        if (pathLength > 20)
        {
            return null;
        }
        if (getPixelValue(currentPoint.x,currentPoint.y,image) >= 255)
        {
            return null;
        }
        Point rightPath = recursePath(new Point(currentPoint.x+1,currentPoint.y,currentPoint),targetPoint, pathLength, image);
        if (rightPath != null)
        {
            return rightPath;
        }
        Point leftPath = recursePath(new Point(currentPoint.x-1,currentPoint.y,currentPoint),targetPoint, pathLength, image);
        if (leftPath != null)
        {
            return leftPath;
        }
        Point downPath = recursePath(new Point(currentPoint.x,currentPoint.y+1,currentPoint),targetPoint, pathLength, image);
        if (downPath != null)
        {
            return downPath;
        }
        Point upPath = recursePath(new Point(currentPoint.x,currentPoint.y-1,currentPoint),targetPoint, pathLength, image);
        if (upPath != null)
        {
            return upPath;
        }
        Point rightDownPath = recursePath(new Point(currentPoint.x+1,currentPoint.y+1,currentPoint),targetPoint, pathLength, image);
        if (rightDownPath != null)
        {
            return rightDownPath;
        }
        Point rightUpPath = recursePath(new Point(currentPoint.x+1,currentPoint.y-1,currentPoint),targetPoint, pathLength, image);
        if (rightUpPath != null)
        {
            return rightUpPath;
        }
        Point leftDownPath = recursePath(new Point(currentPoint.x-1,currentPoint.y+1,currentPoint),targetPoint, pathLength, image);
        if (leftDownPath != null)
        {
            return leftDownPath;
        }
        Point leftUpPath = recursePath(new Point(currentPoint.x-1,currentPoint.y-1,currentPoint),targetPoint, pathLength, image);
        if (leftUpPath != null)
        {
            return leftUpPath;
        }

        return null;
    }
    
    // Returns the final point that this path includes yet all points in path contain a pointer to the previous point
    private static Point pathExists(Point p, Point p2, File grayscale) {
        try
        {
            BufferedImage image = ImageIO.read(grayscale);
            
            return recursePath(p,p2,0,image);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static void includePathPoints(ArrayList<Point> edgePoints, Point p, Point p2) {
        // perform backtrace
        Point currentPoint = p2.previousPoint;
        
        while (currentPoint != null && !currentPoint.equals(p))
        {
            currentPoint.neighbers = 2;
            edgePoints.add(currentPoint);
            currentPoint = currentPoint.previousPoint;
        }
    }

    
    
}
