/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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
package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.TreeMap;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.RunwayData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayEndIlsCone extends AViewObject {

    private final RunwayEnd runwayEnd;
    private final AirportData data;
    private RunwayData rwd;
    private double reverseHeading;

    private Point2D leftVectoringTextOrigin = null;
    private double leftVectoringAngle = 0;
    private double leftVectoringFlyAngle = 0;
    private Point2D leftBaselegTextOrigin = null;
    private double leftBaselegAngle = 0;
    private double leftBaselegFlyAngle = 0;
    private Point2D rightVectoringTextOrigin = null;
    private double rightVectoringAngle = 0;
    private double rightVectoringFlyAngle = 0;
    private Point2D rightBaselegTextOrigin = null;
    private double rightBaselegAngle = 0;
    private double rightBaselegFlyAngle = 0;

    private float  elevation = 0;
    private double distancePerHeight = 0;
    private TreeMap<Integer,Point2D> heightTextPositions = new TreeMap<Integer,Point2D>();

    public RunwayEndIlsCone(AirportData data, RunwayEnd runwayEnd) {
        super(Palette.GLIDESLOPE);
        this.stroke = new BasicStroke(0.3f);
        this.data = data;
        this.runwayEnd = runwayEnd;

        this.font = new Font("Arial", Font.PLAIN, 9);
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mva) {
        if ( ! runwayEnd.isLandingActive()) { //  || runwayEnd.getGlideslope()==null){
            path=null;
            rightVectoringTextOrigin=null;
            rightBaselegTextOrigin=null;
            leftVectoringTextOrigin=null;
            leftBaselegTextOrigin=null;
            heightTextPositions.clear();
            return;
        }
        boolean activeGS = data.getRunways().get(runwayEnd.getRunwayID()).getGlideslope()!=null;
        setColor(activeGS ? Palette.GLIDESLOPE : new Color(150,150,255));

        boolean showHeights = data.getRadarObjectFilterState("GSH") && mva.getLogicalScale() < 170;

        // adaptive display of glideslope heights
        int deltaHeight = 1000;
        if(mva.getLogicalScale()<60) deltaHeight = 500;
        if(mva.getLogicalScale()>100) deltaHeight = 1500; // because we don't want text at the default vectoring / centerline crossing

        if(rwd==null || rwd.isRepaintNeeded()) {
            rwd = data.getRunwayData(runwayEnd.getRunwayID());
            reverseHeading = Converter2D.normalizeAngle(runwayEnd.getTrueHeading()+180);

            rightVectoringAngle =  Math.round(Converter2D.normalizeAngle(reverseHeading-rwd.getRightVectoringAngle())/10)*10;;
            rightVectoringFlyAngle =  Math.round(Converter2D.normalizeAngle(runwayEnd.getTrueHeading()-rwd.getRightVectoringAngle()-data.getMagneticDeclination())/10)*10;
            rightBaselegAngle = Math.round(Converter2D.normalizeAngle(reverseHeading-90)/10)*10;
            rightBaselegFlyAngle = Math.round(Converter2D.normalizeAngle(runwayEnd.getTrueHeading()-90-data.getMagneticDeclination())/10)*10;

            leftVectoringAngle =  Math.round(Converter2D.normalizeAngle(reverseHeading+rwd.getLeftVectoringAngle())/10)*10;
            leftVectoringFlyAngle =  Math.round(Converter2D.normalizeAngle(runwayEnd.getTrueHeading()+rwd.getLeftVectoringAngle()-data.getMagneticDeclination())/10)*10;
            leftBaselegAngle = Math.round(Converter2D.normalizeAngle(reverseHeading+90)/10)*10;
            leftBaselegFlyAngle = Math.round(Converter2D.normalizeAngle(runwayEnd.getTrueHeading()+90-data.getMagneticDeclination())/10)*10;
            rwd.setRepaintNeeded(false);

            elevation = runwayEnd.getRunway().getAerodrome().getElevation()/Units.FT;
        }

        this.font = new Font("Arial", Font.PLAIN, 9);

        // default values 18 SM +/- 10 degrees

        double referenceLength = Units.NM/Units.FT;// one mile;
        double offset = ftd((double) rwd.getExtCenterlineStart() *referenceLength,mva);

        path = new Path2D.Double();

        // center line
        int textspacing = showHeights ? 15 : 0 ; // the dots that are kept free for better text readability


        Point2D textPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, offset);
        Point2D startPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, offset);
        Point2D endPoint = startPoint;
        double distanceToAirport = 0;
        int currentHeight = (int)elevation;
        float angle = runwayEnd.getGlideslope()==null ? 3 : runwayEnd.getGlideslope().getGlideslopeAngle();
        distancePerHeight = deltaHeight / Math.tan(Math.toRadians(angle));

        heightTextPositions.clear();

        while( distanceToAirport < 25 * referenceLength) {
            distanceToAirport += distancePerHeight;

            if(distanceToAirport > rwd.getExtCenterlineStart() * referenceLength
               && distanceToAirport < rwd.getExtCenterlineLength() * referenceLength) {
                startPoint = Converter2D.getMapDisplayPoint(textPoint, reverseHeading, textspacing);
                endPoint = Converter2D.getMapDisplayPoint(textPoint, reverseHeading, ftd((distancePerHeight) ,mva)-textspacing);
                path.append(new Line2D.Double(startPoint, endPoint), false);
            }

            // text positions: heights above ground
            currentHeight += deltaHeight;
            textPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, ftd(distanceToAirport,mva));
            if(showHeights) {
                heightTextPositions.put(currentHeight, textPoint);
            }
        }
        // unbroken center line up to max length
        if(distanceToAirport < rwd.getExtCenterlineLength() *referenceLength) {
            startPoint = Converter2D.getMapDisplayPoint(textPoint, reverseHeading, textspacing);
            endPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, ftd((rwd.getExtCenterlineLength() *referenceLength) ,mva)-textspacing);
            path.append(new Line2D.Double(startPoint, endPoint), false);
        }


        // Distance markers

        // major marker lines
        double currentDistance = 0;
        Point2D centerPoint = currentDisplayPosition;
        int i=0;
        do {
            currentDistance = rwd.getMajorDMStart() + i * rwd.getMajorDMInterval();

            centerPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, ftd(currentDistance *referenceLength,mva));
            startPoint = Converter2D.getMapDisplayPoint(centerPoint, reverseHeading-90, ftd(rwd.getMajorDMTickLength()/2*referenceLength,mva));
            endPoint = Converter2D.getMapDisplayPoint(centerPoint, reverseHeading+90, ftd(rwd.getMajorDMTickLength()/2*referenceLength,mva));
            path.append(new Line2D.Double(startPoint, endPoint), false);

            i++;
        } while(currentDistance<rwd.getMajorDMEnd());

        // minor marker lines

        i=0;
        do {
            currentDistance = rwd.getMinorDMStart() + i * rwd.getMinorDMInterval();

            // ommit, if there is already a major Line
            if(currentDistance>=rwd.getMajorDMStart() && currentDistance<=rwd.getMajorDMEnd()
               && currentDistance%rwd.getMajorDMInterval() == 0 ) {
                i++;
                continue;
            }

            centerPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, ftd(currentDistance *referenceLength,mva));
            startPoint = Converter2D.getMapDisplayPoint(centerPoint, reverseHeading-90, ftd(rwd.getMinorDMTickLength()/2*referenceLength,mva));
            endPoint = Converter2D.getMapDisplayPoint(centerPoint, reverseHeading+90, ftd(rwd.getMinorDMTickLength()/2*referenceLength,mva));
            path.append(new Line2D.Double(startPoint, endPoint), false);

            i++;
        } while(currentDistance<rwd.getMinorDMEnd());

        if(rwd.isRightBaseEnabled()) {
            // sides: right in flight direction

            // first half of the line
            centerPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, ftd(rwd.getRightVectoringCLStart()*referenceLength,mva));
            endPoint = Converter2D.getMapDisplayPoint(centerPoint, rightVectoringAngle, ftd(rwd.getRightVectoringLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(centerPoint, endPoint), false);

            rightVectoringTextOrigin = Converter2D.getMapDisplayPoint(centerPoint, rightVectoringAngle, ftd(rwd.getRightVectoringLength()/2*referenceLength,mva));

            // second half
            startPoint = Converter2D.getMapDisplayPoint(endPoint, rightVectoringAngle, 20);
            endPoint = Converter2D.getMapDisplayPoint(startPoint, rightVectoringAngle, ftd(rwd.getRightVectoringLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(startPoint, endPoint), false);

            // base leg
            // first half
            startPoint = endPoint;
            endPoint = Converter2D.getMapDisplayPoint(startPoint, rightBaselegAngle, ftd(rwd.getRightBaselegLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(startPoint, endPoint), false);

            rightBaselegTextOrigin = Converter2D.getMapDisplayPoint(startPoint, rightBaselegAngle, ftd(rwd.getRightBaselegLength()/2*referenceLength,mva));

            // second half
            startPoint = Converter2D.getMapDisplayPoint(endPoint, rightBaselegAngle, 20);
            endPoint = Converter2D.getMapDisplayPoint(startPoint, rightBaselegAngle, ftd(rwd.getRightBaselegLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(startPoint, endPoint), false);
        } else {
            rightVectoringTextOrigin=null;
            rightBaselegTextOrigin=null;
        }

        if(rwd.isLeftBaseEnabled()) {
            // sides: left in flight direction

            // first half of the line
            centerPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, ftd(rwd.getLeftVectoringCLStart()*referenceLength,mva));
            endPoint = Converter2D.getMapDisplayPoint(centerPoint, leftVectoringAngle, ftd(rwd.getLeftVectoringLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(centerPoint, endPoint), false);

            leftVectoringTextOrigin = Converter2D.getMapDisplayPoint(centerPoint, leftVectoringAngle, ftd(rwd.getLeftVectoringLength()/2*referenceLength,mva));

            // second half
            startPoint = Converter2D.getMapDisplayPoint(endPoint, leftVectoringAngle, 20);
            endPoint = Converter2D.getMapDisplayPoint(startPoint, leftVectoringAngle, ftd(rwd.getLeftVectoringLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(startPoint, endPoint), false);

            // base leg

            // first half
            startPoint = endPoint;
            endPoint = Converter2D.getMapDisplayPoint(startPoint, leftBaselegAngle, ftd(rwd.getLeftBaselegLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(startPoint, endPoint), false);

            leftBaselegTextOrigin = Converter2D.getMapDisplayPoint(startPoint, leftBaselegAngle, ftd(rwd.getLeftBaselegLength()/2*referenceLength,mva));

            // second half
            startPoint = Converter2D.getMapDisplayPoint(endPoint, leftBaselegAngle, 20);
            endPoint = Converter2D.getMapDisplayPoint(startPoint, leftBaselegAngle, ftd(rwd.getLeftBaselegLength()/2*referenceLength,mva)-10);
            path.append(new Line2D.Double(startPoint, endPoint), false);
        } else {
            leftVectoringTextOrigin=null;
            leftBaselegTextOrigin=null;
        }


    }

    private double ftd(double feet, IMapViewerAdapter mapViewerAdapter) {
        return Converter2D.getFeetToDots(feet, mapViewerAdapter);
    }

    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        super.paint(g2d, mapViewAdapter);
        g2d.setColor(color);
        //double currentScale = mapViewAdapter.getLogicalScale();
       // if (minScalePath < currentScale && maxScalePath > currentScale) {
            if (font != null)
                g2d.setFont(font);

            if(rightVectoringTextOrigin!=null) {
                String text = String.format("%3.0f", rightVectoringFlyAngle);
                Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);
                g2d.drawString(text, (float)(rightVectoringTextOrigin.getX()-b.getWidth()/2), (float) (rightVectoringTextOrigin.getY()+b.getHeight()/2-2));
            }
            if(rightBaselegTextOrigin!=null) {
                String text = String.format("%3.0f", rightBaselegFlyAngle);
                Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);
                g2d.drawString(text, (float)(rightBaselegTextOrigin.getX()-b.getWidth()/2), (float) (rightBaselegTextOrigin.getY()+b.getHeight()/2-2));
            }
            if(leftVectoringTextOrigin!=null) {
                String text = String.format("%3.0f", leftVectoringFlyAngle);
                Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);
                g2d.drawString(text, (float)(leftVectoringTextOrigin.getX()-b.getWidth()/2), (float) (leftVectoringTextOrigin.getY()+b.getHeight()/2-2));
            }
            if(leftBaselegTextOrigin!=null) {
                String text = String.format("%3.0f", leftBaselegFlyAngle);
                Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);
                g2d.drawString(text, (float)(leftBaselegTextOrigin.getX()-b.getWidth()/2), (float) (leftBaselegTextOrigin.getY()+(b.getHeight())/2-2));
            }
            for(int height : heightTextPositions.keySet()) {
                String text = ""+(height/100)*100;//String.format("%3.0i", height);
                Point2D point = heightTextPositions.get(height);
                Rectangle2D b = g2d.getFontMetrics().getStringBounds(text, g2d);
                g2d.drawString(text, (float)(point.getX()-b.getWidth()/2), (float) (point.getY()+b.getHeight()/2-2));
            }
        }
    //}

}
