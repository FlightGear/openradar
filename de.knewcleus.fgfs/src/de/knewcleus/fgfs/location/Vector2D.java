package de.knewcleus.fgfs.location;

public class Vector2D {
    
    protected final double x;
    protected final double y;
    
    public Vector2D() {
        x=y=0.0;
    }
    
    public Vector2D(double x, double y) {
        this.x=x;
        this.y=y;
    }
    
    public Vector2D(Vector2D original) {
        this.x=original.x;
        this.y=original.y;
    }

    public static Vector2D createVector2D(double angle, double length) {
        double x,y;
        double angleRad = angle*2*Math.PI/360;
        x = length * Math.cos(angleRad);
        y= length * Math.sin(angleRad);
        return new Vector2D(x,y);
    }

    public static Vector2D createScreenVector2D(double angle, double length) {
        double x,y;
        double angleRad = (-1*angle+90)*2*Math.PI/360;
        x = length * Math.cos(angleRad);
        y= length * Math.sin(angleRad);
        return new Vector2D(x,y);
    }

    public Vector2D add(Vector2D b) {
        return new Vector2D(x+b.x,y+b.y);
    }
    
    public Vector2D subtract(Vector2D b) {
        return new Vector2D(x-b.x,y-b.y);
    }

    public Vector2D scale(double s) {
        return new Vector2D(x*s,y*s);
    }
    
    public Vector2D normalise() {
        double len=getLength();
        
        if (len<1E-22) {
            return new Vector2D();
        }
        
        return new Vector2D(x/len,y/len);
    }

    public double getLength() {
        return Math.sqrt(x*x+y*y);
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Double getAngle() {
        Double angle = 0d;
        double length = getLength();
        if(length!=0) {
            if(x>0 && y>0) angle = (double)Math.round(Math.asin(x/length)/2d/Math.PI*360d); 
            if(x>0 && y<0) angle = (double)180-Math.round(Math.asin(x/length)/2d/Math.PI*360d);
            if(x<0 && y<0) angle = (double)180+-1*Math.round(Math.asin(x/length)/2d/Math.PI*360d);
            if(x<0 && y>0) angle = (double)360+Math.round(Math.asin(x/length)/2d/Math.PI*360d);
        }
        return angle;
    }
    
    public Long getAngleL() {
        Double angleD = getAngle();
        return (angleD!=null) ? Math.round(angleD):null; 
    }
}
