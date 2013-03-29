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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class StdRouteLoop extends AStdRouteElement {

    private final double inboundHeading;
    private Double length = null;
    private Double width = null;
    private final boolean right;
    private final String arrows;
    private String minHeight = null;
    private String maxHeight = null;
    private String misapHeight = null;

    public StdRouteLoop(StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                           String geoReferencPoint, String inboundHeading, String length, String width, String right, String arrows,
                           String minHeight, String maxHeight, String misapHeight, String stroke, String lineWidth, String color) {
        super(mapViewAdapter, route.getPoint(geoReferencPoint,previous),stroke,lineWidth,arrows,color);

        this.inboundHeading = Double.parseDouble(inboundHeading);
        this.length = length !=null ? Double.parseDouble(length) : null;
        this.width = width !=null ? Double.parseDouble(width) : null;
        this.right = !"false".equalsIgnoreCase(right);
        this.arrows = arrows;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.misapHeight = misapHeight;
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        double currentLength =  length !=null ? Converter2D.getFeetToDots(length * Units.NM / Units.FT,mapViewAdapter) : Converter2D.getFeetToDots(2*220/60*Units.NM/Units.FT, mapViewAdapter); // 2min * 220 mph/60h/min
        double currentWidth = width!=null ? Converter2D.getFeetToDots(width * Units.NM / Units.FT,mapViewAdapter) : currentLength * 0.92 ;

        g2d.setFont(g2d.getFont().deriveFont(10f));
        String sInboundHeading = String.format("%03.0f",Converter2D.normalizeAngle(inboundHeading));
        String sOtherHeading = String.format("%03.0f",Converter2D.normalizeAngle(inboundHeading+180));
        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(sInboundHeading, g2d);

        Point2D line1EndPoint = getDisplayPoint(geoReferencePoint);
        Point2D line1StartPoint = Converter2D.getMapDisplayPoint(line1EndPoint, inboundHeading+180, currentLength);
        Point2D line1MiddlePoint2 = Converter2D.getMapDisplayPoint(line1EndPoint, inboundHeading+180, (currentLength-bounds.getWidth()-4)/2);
        Point2D line1MiddlePoint1 = Converter2D.getMapDisplayPoint(line1EndPoint, inboundHeading+180, (currentLength+bounds.getWidth()+4)/2);
        Point2D line1TextPoint = Converter2D.getMapDisplayPoint(line1EndPoint, inboundHeading+180, currentLength/2);
        Point2D line2StartPoint = Converter2D.getMapDisplayPoint(line1EndPoint, right ?inboundHeading+90:inboundHeading-90, currentWidth);;
        Point2D line2EndPoint = Converter2D.getMapDisplayPoint(line2StartPoint, inboundHeading+180, currentLength);
        Point2D line2MiddlePoint1 = Converter2D.getMapDisplayPoint(line2StartPoint, inboundHeading+180, (currentLength-bounds.getWidth()-4)/2);
        Point2D line2MiddlePoint2 = Converter2D.getMapDisplayPoint(line2StartPoint, inboundHeading+180, (currentLength+bounds.getWidth()+4)/2);
        Point2D line2TextPoint = Converter2D.getMapDisplayPoint(line2StartPoint, inboundHeading+180, currentLength/2);

        Point2D centerBow1 = Converter2D.getMapDisplayPoint(line1EndPoint, right ?inboundHeading+90:inboundHeading-90, currentWidth/2);
        Point2D centerBow2 = Converter2D.getMapDisplayPoint(line1StartPoint, right ?inboundHeading+90:inboundHeading-90, currentWidth/2);

        Path2D path = new Path2D.Double();
        path.append(new Arc2D.Double(centerBow1.getX()-currentWidth/2,centerBow1.getY()-currentWidth/2,currentWidth,currentWidth,90-inboundHeading+90,-180,Arc2D.OPEN), false);
        path.append(new Line2D.Double(line2StartPoint,line2MiddlePoint1),true);
        path.append(new Line2D.Double(line2MiddlePoint2,line2EndPoint),false);
        path.append(new Arc2D.Double(centerBow2.getX()-currentWidth/2,centerBow2.getY()-currentWidth/2,currentWidth,currentWidth,90-inboundHeading-90,-180,Arc2D.OPEN), true);
        path.append(new Line2D.Double(line1StartPoint,line1MiddlePoint1),true);
        path.append(new Line2D.Double(line1MiddlePoint2,line1EndPoint),false);
        path.closePath();

        g2d.drawString(sInboundHeading, (int)(line1TextPoint.getX()-bounds.getWidth()/2), (int)(line1TextPoint.getY()+(bounds.getHeight()/2-2)));
        g2d.drawString(sOtherHeading, (int)(line2TextPoint.getX()-bounds.getWidth()/2), (int)(line2TextPoint.getY()+(bounds.getHeight()/2-2)));

        // todo arrows and texts
        if(color!=null) {
            g2d.setColor(color);
        }
        Stroke origStroke = g2d.getStroke();
        if(stroke!=null) {
            g2d.setStroke(stroke);
        }
        g2d.draw(path);

        if(minHeight!=null) {
            int line = 0;
            double x = (line1StartPoint.getX()+line1EndPoint.getX()+line2StartPoint.getX()+line2EndPoint.getX())/4;
            double y = (line1StartPoint.getY()+line1EndPoint.getY()+line2StartPoint.getY()+line2EndPoint.getY())/4;

            g2d.setFont(g2d.getFont().deriveFont((float)(currentWidth/3f*0.4)));
            int textlines = misapHeight!=null ? 1 : 0;
            textlines = minHeight!=null ? textlines+1 : textlines;
            textlines = maxHeight!=null ? textlines+1 : textlines;

            Rectangle2D sampleBounds = g2d.getFontMetrics().getStringBounds("SAMPLE", g2d);
            double totalTextheight = textlines * (sampleBounds.getHeight());
            Point2D textBase = new Point2D.Double(x, y + totalTextheight / 2 -2) ;

            if(misapHeight!=null) {
                bounds = g2d.getFontMetrics().getStringBounds(misapHeight, g2d);
                g2d.drawString(misapHeight, (int)(textBase.getX()-bounds.getWidth()/2), (int)(textBase.getY()-line*(bounds.getHeight())));
                line++;
            }

            if(maxHeight!=null) {
                bounds = g2d.getFontMetrics().getStringBounds(maxHeight, g2d);
                g2d.drawString(maxHeight, (int)(textBase.getX()-bounds.getWidth()/2), (int)(textBase.getY()-line*(bounds.getHeight())));
                line++;
            }
            if(minHeight!=null) {
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
                bounds = g2d.getFontMetrics().getStringBounds(minHeight, g2d);
                g2d.drawString(minHeight, (int)(textBase.getX()-bounds.getWidth()/2), (int)(textBase.getY()-line*(bounds.getHeight())));
                g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
                line++;
            }
        }

        if(arrows!=null) {
            this.paintArrow(g2d, line1EndPoint, inboundHeading, arrowSize, true);
            this.paintArrow(g2d, line2EndPoint, inboundHeading-180, arrowSize, true);
        }

        g2d.setStroke(origStroke);

        return path.getBounds2D();
    }

    @Override
    public Point2D getEndPoint() {
        return geoReferencePoint;
    }
}
