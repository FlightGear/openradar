/**
 * Copyright (C) 2012,2013,2015 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navaids.Pavement;
import de.knewcleus.fgfs.navaids.PavementNode;
import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class PavementObject extends AViewObject {

    // private final Aerodrome aerodrome;
    private final Pavement pavement;

    public PavementObject(Aerodrome aerodrome, Pavement pavement) {
        super(Color.lightGray);

        // this.aerodrome = aerodrome;
        this.pavement = pavement;

        setMinScalePath(0);
        setMaxScalePath(500);

        switch (pavement.getSurfaceType()) {
        case Concrete:
            setColor(new Color(115, 115, 110));
            break;
        case Asphalt:
            setColor(Palette.TARMAC);
            break;
        case Dirt:
            setColor(new Color(135, 115, 110));
            break;
        case DryLakebed:
            setColor(new Color(135, 115, 110));
            break;
        case Gravel:
            setColor(new Color(115, 115, 110));
            break;
        case SnowIce:
            setColor(new Color(120, 120, 200));
            break;
        case Transparent:
            setColor(new Color(135, 115, 110));
            break;
        case TurfGrass:
            setColor(new Color(40, 70, 40));
            break;
        case Water:
            setColor(new Color(20, 20, 80));
            break;
        default:
            break;
        }
        fillPath = true;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mva) {
        setMaxScalePath(500);
        //setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
        // System.out.println(aerodrome+": "+pavement.getNodes().size());
        path = new Path2D.Double();
        PavementNode start = null;
        for (PavementNode pn : pavement.getNodes()) {
            PavementNode end = pn;
            if (start != null) {
                // Point2D dStart = Converter2D.getMapDisplayPoint(start.point,mva);
                Point2D dControlStart = start.isBezierNode ? Converter2D.getMapDisplayPoint(start.bezierPoint, mva) : null;
                Point2D dControlEnd = end.isBezierNode ? Converter2D.getMapDisplayPoint(end.bezierPoint, mva) : null;
                Point2D dEnd = Converter2D.getMapDisplayPoint(end.point, mva);

                if (start.isBezierNode && end.isBezierNode) {
                    path.curveTo(dControlStart.getX(), dControlStart.getY(), dControlEnd.getX(), dControlEnd.getY(), dEnd.getX(), dEnd.getY());
                } else {
                    path.lineTo(dEnd.getX(), dEnd.getY());
                }
                // nicer but produces gaps
                // if(!start.isBezierNode && !end.isBezierNode) {
                // path.lineTo(dEnd.getX(),dEnd.getY());
                // } else {
                // if(!start.isBezierNode && end.isBezierNode) {
                // path.curveTo(
                // dStart.getX(),dStart.getY(),
                // dControlEnd.getX(),dControlEnd.getY(),
                // dEnd.getX(),dEnd.getY());
                // } else {
                // if(start.isBezierNode && end.isBezierNode) {
                // path.curveTo(
                // dControlStart.getX(),dControlStart.getY(),
                // dControlEnd.getX(),dControlEnd.getY(),
                // dEnd.getX(),dEnd.getY());
                // }
                // }
                // }
            } else {
                Point2D dStart = Converter2D.getMapDisplayPoint(pn.point, mva);
                path.moveTo(dStart.getX(), dStart.getY());
            }
            if (pn.isEndNode || pn.isCloseLoop) {
                start = null;
            } else {
                start = end;
            }
            if (pn.isCloseLoop) {
                path.closePath();
            }
        }
    }

    @Override
    public synchronized void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        // TODO Auto-generated method stub
        super.paint(g2d, mapViewAdapter);
    }

}
