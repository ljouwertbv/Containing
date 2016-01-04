/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import nhl.containing.controller.simulation.Node;

/**
 *
 * @author Dudecake
 */
public class PathFinder
{
    private int number = 88;
    
    public int getNumber()
    {
        return number;
    }
    
    public static native void initPath(Dimension dimension);
    
    public static native void initPath(Node[] nodes);
    
    public static void initPath(List<Node> nodes)
    {
        initPath(nodes.toArray(new Node[nodes.size()]));
    }
    
    public static int[] getPath(int from, int to)
    {
        return reverse(getPath(from, to, 5.0f));
    }
    
    private static native int[] getPath(int from, int to, float speed);
    
    private static int[] reverse(int[] arr)
    {
        int len = arr.length;
        for (int i = 0; i < len/2; i++)
        {
        arr[i] = arr[i] ^ arr[len - i  - 1];
        arr[len - i  - 1] = arr[i] ^ arr[len - i  - 1];
        arr[i] = arr[i] ^ arr[len - i  - 1];
        }
        return arr;
    }
    
    public static native void makeConnection(int originId, int destId);
    
    public static native void makeConnection(Point origin, Point dest);
    
    public static native void makeConnection(int orgx, int orgy, int destx, int desty);
    
    public static native void setOccupied(Point loc, boolean occupied);
    
    public static native boolean getOccupied(Point loc);
    /*
    public static native void helloFromC();
    
    public static native double avgFromC(int[] x);
    
    public static native int intFromC(int[] x);
    
    public static native Integer integerFromC(int x);
    
    public static native Point pointInC(int x, int y);
    
    public native void changeNumberInC();
    */
    public static native void cleanup();
}
