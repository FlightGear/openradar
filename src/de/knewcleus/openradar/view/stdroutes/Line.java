/**
 * Copyright (C) 2013 Wolfram Wagner
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.awt.geom.Point2D;


/**
 * This class exists to ease geometric constructions.
 * It bases on a line in format
 *                  f(x) = y = m * x + n
 * and provides useful utility methods to construct without writing formulas...
 *
 * @author Wolfram Wagner
 *
 */
public class Line {

    /** One Point2D lying on that line */
    private final Point2D point;
    /**
     * The angle of that line in relation to the x-axis
     * If the line is vertical, the angle is null.
     */
    private final Double angle;
    /**
     * The slope of the line deltaY / deltaX
     */
    private final Double m;
    /**
     * The y value where the line crosses the y-axis: => f(0)
     */
    private final Double n;

    public synchronized Point2D getPoint() {
        return point;
    }

    public synchronized Double getAngle() {
        return angle;
    }

    public synchronized Double getM() {
        return m;
    }

    public synchronized Double getN() {
        return n;
    }

    /**
     * Constructs a line out of a Point2D laying on that line and its angle.
     *
     * @param Point2D
     */
    public Line(Point2D point, Double angle) {
        this.point = point;
        this.angle = angle!=null ? normalizeLineAngle180(angle) : null;
        if(this.angle != null && this.angle!=90 && this.angle!=-90) {
            if(this.angle==0 || this.angle==180) {
                // horizontal
                this.m=0d;
                this.n=point.getY();
            } else {
                this.m = -1 * Math.tan(Math.toRadians(this.angle)); // -1 because our y-values grow downwards
                this.n = point.getY() - m * point.getX();
            }
        } else {
            // vertical
            this.m=null;
            this.n=0d;
        }
    }

    /**
     * Constructs a line out of two Point2Ds laying on that line
     *
     * @param Point2D
     */
    public Line(Point2D point, Point2D point2) {
        // sort them
        if(point.getX()>point2.getX()) {
            Point2D tp = point;
            point = point2;
            point2 = tp;
        }
        this.point = point;
        double deltaX = point2.getX()-point.getX();
        double deltaY = point2.getY()-point.getY();
        if(deltaX<0.0001) {
            // vertical line
            this.angle = null;
            this.m=null;
            this.n=0d;
        } else {
            if(deltaY==0) {
                // horizontal line
                this.angle = 0d;
                this.m=0d;
                this.n=point.getY();
            } else {
                this.m = deltaY/deltaX;
                this.angle = normalizeLineAngle180(Math.atan( m )*360/2*Math.PI);
                this.n = point.getY() - m * point.getX();
            }
        }
    }

    /**
     * Returns the line that leaves runs through p (can be anywhere) and crosses this line in a 90 degree angle.
     *
     * @param p Any Point2D.
     * @return
     */
    public Line getNormal(Point2D p) {
        return new Line( p , normalizeLineAngle180(angle + 90d));
    }

    public Point2D getIntersectionWith(Line line2) {

        if( (m==null && line2.m==null) || //both are vertical
            ( m!=null && line2.m!=null && (Math.abs(m-line2.m)<0.01) ) ) { // check parallel
            // parallel
            return null;
        }


        if(m==null) {
            // vertical
            return new Point2D.Double(point.getX(),line2.getF(point.getX()));
        }
        if(line2.m==null) {
            // vertical
            return new Point2D.Double(line2.point.getX(),getF(line2.point.getX()));
        }

        double x = (line2.n - n)/(m - line2.m);

        double y = getF(x);

        return new Point2D.Double(x,y);
    }

    /**
     * Returns the line that runs in the middle between the lines to through the intersection.
     *
     * @param line2 The other line on the far side of the result.
     * @return
     */
    public Line getMidAngleLine(Line line2) {

        Point2D intersection = getIntersectionWith(line2);
        if(intersection==null) {
            // parallel: construct parallel line in the middle between them

            final Point2D pointMiddleBetweenLines;
            if(m==null) {
                // vertical lines => use any point in the middle
                pointMiddleBetweenLines = new Point2D.Double((point.getX()+line2.point.getX())/2,point.getY());
            } else if( m==0 ) {
                // horizontal lines => use any point in the middle
                pointMiddleBetweenLines = new Point2D.Double(point.getX(), (point.getY()+line2.point.getY())/2);
            } else {
                // both parallel lines are not vertical and not horizontal
                // => construct the normal, measure distance and take its middle
                Line normal = getNormal(getPoint());
                Point2D oppositePoint = line2.getIntersectionWith(normal);
                // oppositePoint can be null if line2 and normal are parallel
                if(oppositePoint!=null) {
                    pointMiddleBetweenLines = new Point2D.Double((getPoint().getX() + oppositePoint.getX())/2,(getPoint().getY() + oppositePoint.getY())/2);
                } else {
                    // take any line, that is not parallel with line2
                    Line anyLine = new Line(line2.point , line2.angle+45);
                    Point2D p1 = getIntersectionWith(anyLine);
                    Point2D p2 = line2.getIntersectionWith(anyLine);
                    pointMiddleBetweenLines = new Point2D.Double((p2.getX() + p1.getX())/2,(p2.getY() + p1.getY())/2);
                   // System.out.println(pointMiddleBetweenLines);
                }
            }
            return new Line(pointMiddleBetweenLines , angle);
        }

        return new Line( intersection , (angle + line2.getAngle())/2);
    }

    /**
     * Returns the distance between the Point2Ds on this line, given by their x-values
     */
    public Double getDistance(Point2D point1, Point2D point2) {
        double deltaX = point2.getX() -point1.getX();
        double deltaY = point2.getY() -point1.getY();
        return Math.sqrt( Math.pow(deltaX, 2) + Math.pow(deltaY,2));
    }

    /**
     * Returns the x value at x
     */
    public Double getF(Double x) {
        return m*x+n;
    }

    public Point2D getPointOnLine(double x) {
        if(m==0) {
            // vertical
            return null;
        } else {
            return new Point2D.Double(x,getF(x));
        }
    }

    /**
     * Brings the angle between -180= < alpha <= 180 range
     */
    public static Double normalizeLineAngle180(double angle) {
        while(angle>=180) {
            angle = angle - 360;
        }
        while(angle<=-180) {
            angle = angle + 360;
        }
        return angle;
    }

    @Override
    public String toString() {
        if(m!=null) {
            return String.format("Line: %1.3f * x + %1.1f  (angle: %1.0f)", m,n,angle);
        } else {
            return "Line: angle: 90°";
        }
    }

    public Line getParallelLine(double angleToNewLine, Double distance) {
        if(m!=null) {
            // not vertical
            Double shiftY = distance * Math.cos(Math.toRadians(getAngle()))
                           * -1 * Math.signum(angleToNewLine); // direction to parallel line;
            Double shiftX = distance * Math.sin(Math.toRadians(getAngle()))
                    * -1 * Math.signum(angleToNewLine); // direction to parallel line;

            Point2D newPoint = new Point2D.Double(this.getPoint().getX()+shiftX,this.getPoint().getY()+shiftY);
            return new Line(newPoint,this.getAngle());
        } else {
            // vertical, move horizontally
            Double shift = distance
                           * Math.signum(getAngle()) //direction of line
                           * -1 * Math.signum(angleToNewLine); // direction of parallel

            Point2D newPoint = new Point2D.Double(this.getPoint().getX()+shift,this.getPoint().getY());
            return new Line(newPoint,this.getAngle());
        }

    }
}
