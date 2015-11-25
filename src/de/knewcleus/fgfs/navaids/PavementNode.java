package de.knewcleus.fgfs.navaids;

import java.awt.geom.Point2D;

public class PavementNode {
    
    public volatile boolean isEndNode;
    public final boolean isCloseLoop;
    public final boolean isBezierNode;

    public final String rowCode;
    public final Point2D point;
    public final Point2D bezierPoint;
    
    
    public PavementNode(String[] def) {
        isCloseLoop = def[0].equals("113") || def[0].equals("114") ;
        isEndNode = def[0].equals("113") || def[0].equals("114") || def[0].equals("115") || def[0].equals("116");
        isBezierNode = def[0].equals("112") || def[0].equals("114") || def[0].equals("116");
       
        rowCode=def[0];
        point = new Point2D.Double(Double.parseDouble(def[2]),Double.parseDouble(def[1]));
        if(isBezierNode) {
            bezierPoint = new Point2D.Double(Double.parseDouble(def[4]),Double.parseDouble(def[3]));
        } else {
            bezierPoint=null;
        }
    }

}
