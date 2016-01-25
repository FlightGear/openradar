/**
 * Copyright (C) 2012,2013,2015 Wolfram Wagner
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
package de.knewcleus.openradar.rpvd.contact;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import de.knewcleus.fgfs.location.Vector2D;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.rpvd.TrackDisplayState;
import de.knewcleus.openradar.view.Converter2D;
/**
 * This class paints the radar contact information into the radar screen.
 *
 * @author Wolfram Wagner
 */
public class RadarContactTextPainter {

    private enum Case { TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT }

    private TrackDisplayState trackDisplayState;
    private final RadarDataBlockHelper textHelper;
    private Point2D currentDevicePosition;
    private Line2D line;
    private volatile RoundRectangle2D background;
    private volatile Rectangle2D displayExtents;

    private final static double LENGTH = 70d;
    private final static double SPACE = 2d;
    private double symbolSpace = 0d;

    private volatile Point2D anchor = null;
    private volatile double newX ;
    private volatile double newY ;
    private volatile Rectangle2D boundsText = null;

    protected volatile Path2D textContainer = new Path2D.Double();
    protected volatile Point2D textOrigin = new Point2D.Double();
    protected volatile FontMetrics fontMetrics = null;
    protected volatile Rectangle2D lastTextDisplayExtents = new Rectangle2D.Double();

    public RadarContactTextPainter(GuiMasterController master, TrackDisplayState trackDisplayState) {
        this.trackDisplayState = trackDisplayState;
        textHelper = new RadarDataBlockHelper(master,trackDisplayState,SPACE);
    }

    public synchronized Rectangle2D getDisplayExtents(Point2D currentDevicePosition) {
        this.currentDevicePosition = currentDevicePosition;
        if(boundsText==null) {
            // intial case, we don't know about the fontMetricts,so we construct a dummy
            background = new RoundRectangle2D.Double(currentDevicePosition.getX()-34d,currentDevicePosition.getY()-16d, 60+8, 24+8,10,10);
            line = new Line2D.Double(currentDevicePosition.getX(),currentDevicePosition.getY(),currentDevicePosition.getX()+24d,currentDevicePosition.getY()-24d);
        } else {
            // construct the objects at their new place
            constructBackgroundShapes(currentDevicePosition);
        }
        // merge background display with the line
        displayExtents = background.getBounds2D();
        Rectangle2D.union(displayExtents, line.getBounds2D(), displayExtents);
        return displayExtents;
    }

    public synchronized void paint(Graphics2D g2d, boolean hightlighted) {
        Font formerFont = g2d.getFont();
        g2d.setFont(textHelper.getFont());

        if(fontMetrics==null) {
            fontMetrics = g2d.getFontMetrics();
        }


        textHelper.initializeDisplay();
        if(!textHelper.isTextEmpty()) {

            boundsText = textHelper.getBounds(g2d);

            boolean fgComSupport = trackDisplayState.getGuiContact().hasFgComSupport();
            symbolSpace = fgComSupport ? 14 : 0; // only reserve space for headphone, if it is enabled
            double vspeed = trackDisplayState.getGuiContact().getVerticalSpeedD();
            symbolSpace +=  12; // always reserve space for the arrow, otherwise it becomes to lively

            if(anchor==null) constructBackgroundShapes(currentDevicePosition);

            // create composite for transparency of background
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,hightlighted ? 1f : 0.80f);
            g2d.setComposite(ac);
            g2d.setColor(textHelper.getBackgroundColor(trackDisplayState.getGuiContact(),hightlighted));
            g2d.fill(background);
            lastTextDisplayExtents = background.getBounds2D();

            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
            g2d.setComposite(ac);

//            g2d.setColor(textHelper.getColor(trackDisplayState.getGuiContact()));

            double dx = anchor.getX()-currentDevicePosition.getX();
            double dy = currentDevicePosition.getY() - anchor.getY();
            
            Vector2D vDistance = new Vector2D(dx, dy);
            Double angle = vDistance.getAngle();
            
            Point2D newStartPoint = Converter2D.getMapDisplayPoint(currentDevicePosition, angle, 10);

            Color dbColor = textHelper.getDataBlockColor(trackDisplayState.getGuiContact());
            if(trackDisplayState.isSelected()) {
                dbColor = dbColor.brighter().brighter();
            }
            g2d.setColor(dbColor);
            
            line = new Line2D.Double(newStartPoint.getX(),newStartPoint.getY(),anchor.getX(),anchor.getY());
            g2d.draw(line);

            if(hightlighted || trackDisplayState.getGuiContact().isIdentActive()) {
                // SELECTED
                g2d.setColor(Color.black);
            }
            
            for(int i=0;i<textHelper.getLineCount(g2d);i++) {
                g2d.drawString(textHelper.getLine(i),(float)(newX+SPACE),(float)(newY+textHelper.getLineYOffset(g2d, i)));

            }

            // the symbols
            if(textHelper.getAltSpeedLineIndex()>-1 && textHelper.displayVSpeedArrow()) {
                drawArrow(g2d, vspeed, newX+textHelper.getAltitudeTextWidth(g2d, trackDisplayState.getGuiContact()) + 6 , newY+textHelper.getLineYOffset(g2d, textHelper.getAltSpeedLineIndex()-1) + 2 , textHelper.getLineHeight(g2d)-2);
            }

            if(fgComSupport) {
                drawFgComSymbol(g2d, newX + textHelper.getLineWidth(g2d, 0) + (fgComSupport?4:4), newY +2);
            }
        }
        g2d.setFont(formerFont);
    }

    private void constructBackgroundShapes(Point2D currentDevicePosition) {

        double trueCourse = trackDisplayState.getGuiContact().getGroundSpeedD()>1 ? trackDisplayState.getGuiContact().getTrueCourseD() : 0d;
        Case layout = getCase(trueCourse);


        // first text line will start at (dx, dy)
        anchor = Converter2D.getMapDisplayPoint(currentDevicePosition, trueCourse + 135, LENGTH);
        newX = -1;
        newY = -1;

        switch(layout) {
        case TOP_LEFT:
            newX = anchor.getX()+SPACE;
            newY = anchor.getY()+SPACE;
            break;
        case TOP_RIGHT:
            newX = anchor.getX()-boundsText.getWidth()-symbolSpace-SPACE;
            newY = anchor.getY()+SPACE;
            break;
        case BOTTOM_RIGHT:
            newX = anchor.getX()-boundsText.getWidth()-symbolSpace+SPACE;
            newY = anchor.getY()-boundsText.getHeight()-2*SPACE;
            break;
        case BOTTOM_LEFT:
            newX = anchor.getX()+SPACE;
            newY = anchor.getY()-boundsText.getHeight()-2*SPACE;
            break;
        }

        background = new RoundRectangle2D.Double(newX-SPACE,newY-SPACE, boundsText.getWidth()+2*SPACE+symbolSpace, boundsText.getHeight()+2*SPACE,10d,10d);
    }

    private Case getCase(double trueCourseD) {
        if(trueCourseD>0 && trueCourseD<=90) return Case.TOP_LEFT;
        if(trueCourseD>90 && trueCourseD<=180) return Case.TOP_RIGHT;
        if(trueCourseD>180 && trueCourseD<=270) return Case.BOTTOM_RIGHT;
        return Case.BOTTOM_LEFT;
    }

    public synchronized boolean contains(Point2D devicePoint) {
        return background.contains(devicePoint) || line.contains(devicePoint);
    }

    private void drawArrow(Graphics2D g2d, double vSpeed, double x, double y, double length) {
        int xOffset = 0;

        Point2D tipPoint = new Point2D.Double(Math.round(x+xOffset), Math.round(vSpeed<0 ? y+length : y));
        Point2D otherPoint = new Point2D.Double(Math.round(x+xOffset), Math.round(vSpeed<0 ? y : y+length));
        double heading = vSpeed>0 ? 0 : 180;
        Point2D point1 = Converter2D.getMapDisplayPoint(tipPoint, heading-180+25, 5);
        Point2D point2 = Converter2D.getMapDisplayPoint(tipPoint, heading-180-25, 5);

        g2d.draw(new Line2D.Double(otherPoint, tipPoint));

        Path2D path = new Path2D.Double();
        path.append(new Line2D.Double(tipPoint, point1),false);
        path.append(new Line2D.Double(point1, point2),true);
        path.append(new Line2D.Double(point2, tipPoint),true);
        g2d.draw(path);
        g2d.fill(path);
    }

    private void drawFgComSymbol(Graphics2D g2d, double x, double y) {

        Point2D tipPoint = new Point2D.Double(Math.round(x),Math.round(y));
        // headset
        Point2D center = new Point2D.Double(Math.round(x+4),Math.round(y+5));
        Point2D point2 = Converter2D.getMapDisplayPoint(center, 90, 3);
        Point2D point3 = Converter2D.getMapDisplayPoint(center, 270, 3);

        g2d.draw(new Arc2D.Double(tipPoint.getX(), tipPoint.getY(),8,10,0,270,Arc2D.OPEN));
        g2d.fill(new Ellipse2D.Double(point2.getX()-1, point2.getY()-1,3,3));
        g2d.fill(new Ellipse2D.Double(point3.getX()-1, point3.getY()-1,3,3));
    }

    public boolean isTextEmpty() {
        return textHelper.isTextEmpty();
    }

    public Color getColor(GuiRadarContact guiContact) {
        return textHelper.getColor(guiContact);
    }

}
