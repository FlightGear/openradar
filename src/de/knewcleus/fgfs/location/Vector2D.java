/**
 * Copyright (C) 2012 Wolfram Wagner 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
