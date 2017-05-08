/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocrpractice;

/**
 *
 * @author psoderquist
 */
class Point {
    int x;
    int y;
    int value;
    int runLength;
    int neighbers = 0;
    Point previousPoint;
    
    public Point(int x, int y, Point previous)
    {
        this.x = x;
        this.y = y;
        this.previousPoint = previous;
    }
    
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public void setPreviousPoint(Point p)
    {
        this.previousPoint = p;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!o.getClass().equals(this.getClass()))
        {
            return false;
        }
        Point p = (Point)o;
        if (this.x == p.x && this.y == p.y)
        {
            return true;
        }
        return false;
    }
}
