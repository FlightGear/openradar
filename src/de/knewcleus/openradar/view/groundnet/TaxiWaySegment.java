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
package de.knewcleus.openradar.view.groundnet;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.Vector2D;


public class TaxiWaySegment implements ITaxiWayObject{

    private String name;
    private TaxiPoint begin;
    private TaxiPoint end;
    private boolean isPushBackRoute;
    private double dx = 0;
    private double dy = 0;

    
    public TaxiWaySegment(String name, TaxiPoint begin, TaxiPoint end, boolean isPushBackRoute) {
        this.name=name;
        this.begin=begin;
        this.end=end;
        this.isPushBackRoute=isPushBackRoute;
        
        // calculate the normed directions for placing of texts
        
        if(begin instanceof ParkPos) {
            dx = begin.getLon()-end.getLon();
            dy = begin.getLat()-end.getLat();
        } 
        if(end instanceof ParkPos) {
            dx = end.getLon()-begin.getLon();
            dy = end.getLat()-begin.getLat();
        }
        Vector2D v2d = new Vector2D(dx, dy);
        dx = dx/v2d.getLength();
        dy = -1* dy/v2d.getLength(); // because Y coordinates on screen point into other direction
    }

    public String getName() {
        return name;
    }

    public TaxiPoint getBegin() {
        return begin;
    }

    public TaxiPoint getEnd() {
        return end;
    }

    public boolean isPushBackRoute() {
        return isPushBackRoute;
    }

    public Point2D getGeoPoint() {
        return begin.getGeoPoint2D();
    }
    public double getOrientationX() {
        return dx;
    }

    public double getOrientationY() {
        return dy;
    }

}
