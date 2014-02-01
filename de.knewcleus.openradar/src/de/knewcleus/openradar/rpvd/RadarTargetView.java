/**
 * Copyright (C) 2008-2009 Ralf Gerlich Copyright (C) 2012,2013 Wolfram Wagner
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
package de.knewcleus.openradar.rpvd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.rpvd.contact.ContactShape;
import de.knewcleus.openradar.rpvd.contact.RadarContactTextPainter;
import de.knewcleus.openradar.tracks.TrackUpdateNotification;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;
import de.knewcleus.openradar.view.mouse.FocusChangeNotification;
import de.knewcleus.openradar.view.mouse.IFocusableView;
import de.knewcleus.openradar.view.mouse.IMouseTargetView;
import de.knewcleus.openradar.view.mouse.MouseInteractionEvent;

/**
 * The radar target view displays the symbols for a radar target, the radar trail and the heading line.
 *
 * @author Ralf Gerlich
 *
 */
public class RadarTargetView implements IBoundedView, INotificationListener, IFocusableView, IMouseTargetView, ITrackDisplay {
    protected final static double targetDotRadius = 3.0;

    protected final static GeodesicUtils geodesicUtils = new GeodesicUtils(Ellipsoid.WGS84);

    protected static final double headingLineVicinity = 3.0;

    protected final GuiMasterController master;
    protected final IRadarMapViewerAdapter radarMapViewAdapter;
    protected final TrackDisplayState trackDisplayState;

    private final List<Point2D> logicalDotPositions = Collections.synchronizedList(new ArrayList<Point2D>());
    private final List<ContactShape> displayDotShapes = Collections.synchronizedList(new ArrayList<ContactShape>());
    protected volatile Point2D currentGeoPosition = new Point2D.Double();
    protected volatile Point2D futureGeoPosition = new Point2D.Double();
    protected volatile Point2D currentLogicalPosition = new Point2D.Double();
    protected volatile Point2D futureLogicalPosition = new Point2D.Double();
    protected volatile Point2D currentDevicePosition = new Point2D.Double();
    protected volatile Point2D futureDevicePosition = new Point2D.Double();
    protected volatile Line2D displayHeadingLine = new Line2D.Double();
    protected volatile Rectangle2D displayExtents = new Rectangle2D.Double();

    protected RadarContactTextPainter contactTextPainter;

    public RadarTargetView(GuiMasterController master, IRadarMapViewerAdapter radarMapViewAdapter, TrackDisplayState trackDisplayState) {
        this.master = master;
        this.radarMapViewAdapter = radarMapViewAdapter;
        this.trackDisplayState = trackDisplayState;
        contactTextPainter = new RadarContactTextPainter(master, trackDisplayState);
        radarMapViewAdapter.registerListener(this);
        trackDisplayState.registerListener(this);
        trackDisplayState.getTrack().registerListener(this);
        updateGeographicPositions();
    }

    @Override
    public synchronized boolean contains(Point2D devicePoint) {
        for (ContactShape shape : displayDotShapes) {
            if (shape.contains(devicePoint)) {
                return true;
            }
        }

        if (contactTextPainter.contains(devicePoint)) {
            return true;
        }

        /* Check heading line */
        final double headX = futureDevicePosition.getX() - currentDevicePosition.getX();
        final double headY = futureDevicePosition.getY() - currentDevicePosition.getY();
        final double posX = devicePoint.getX() - currentDevicePosition.getX();
        final double posY = devicePoint.getY() - currentDevicePosition.getY();
        final double headLen = Math.sqrt(headX * headX + headY * headY);
        final double projection = headX * posY - headY * posX;
        if (Math.abs(projection) < headingLineVicinity * headLen) {
            return true;
        }

        return false;
    }

    @Override
    public synchronized void focusChanged(FocusChangeNotification event, java.awt.event.MouseEvent e) {
        trackDisplayState.setSelected(e, event.getNewOwner() == this);
    }

    @Override
    public void processMouseInteractionEvent(MouseInteractionEvent e) {
    }

    @Override
    public synchronized void accept(IViewVisitor visitor) {
        visitor.visitView(this);
    }

    @Override
    public boolean isVisible() {
        /* Radar tracks are always shown */
        return true;
    }

    @Override
    public synchronized TrackDisplayState getTrackDisplayState() {
        return trackDisplayState;
    }

    @Override
    public void validate() {
    }

    @Override
    public synchronized void paint(Graphics2D g2d) {
        if (!trackDisplayState.guiContact.isExpired()) {
            // the text block
            boolean highlighted = trackDisplayState.guiContact.isHighlighted();
            contactTextPainter.paint(g2d, highlighted); // paint infos into background
            // the position shape
            Color baseColor = /*(trackDisplayState.isSelected() ? Palette.RADAR_SELECTED :*/ contactTextPainter.getColor(trackDisplayState.getGuiContact());
            Color color = baseColor;
            int count = displayDotShapes.size();
            int i = 0;
            
            for (ContactShape displayDotShape : displayDotShapes) {
                // paint every second
                if(i==0 || i%3==0) {
                    if (displayDotShapes.indexOf(displayDotShape) == 0 || displayDotShape.isTailVisible()) {
                        color = i==0 ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), 200 * (count-i) / count);
                        g2d.setColor(color);
                        displayDotShape.paintShape(g2d);
                    }
                }
                ++i;
            }
            g2d.setColor(baseColor);
            // heading line
            if (!contactTextPainter.isTextEmpty() && trackDisplayState.guiContact.isActive()) {
                g2d.draw(displayHeadingLine);
            }
        }
    }

    protected synchronized void repaint() {
        radarMapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
    }

    @Override
    public synchronized Rectangle2D getDisplayExtents() {
        return displayExtents;
    }

    @Override
    public synchronized void acceptNotification(INotification notification) {
        if (notification instanceof SelectionChangeNotification) {
            repaint();
        } else if (notification instanceof TrackUpdateNotification) {
            updateGeographicPositions();
        } else if (notification instanceof ProjectionNotification) {
            updateLogicalPositions();
        } else if (notification instanceof CoordinateSystemNotification) {
            updateDisplayPositions();
        }
    }

    protected synchronized void updateGeographicPositions() {
        final IRadarDataPacket currentStatus = trackDisplayState.getTrack().getCurrentState();
        currentGeoPosition = currentStatus.getPosition();
        final GeodesicInformation geodInfo = geodesicUtils.direct(currentGeoPosition.getX(), currentGeoPosition.getY(),
                currentStatus.getCalculatedTrueCourse(), currentStatus.getCalculatedVelocity() * radarMapViewAdapter.getHeadingVectorTime());
        futureGeoPosition = new Point2D.Double(geodInfo.getEndLon(), geodInfo.getEndLat());

        updateLogicalPositions();
    }

    protected synchronized void updateLogicalPositions() {
        final IProjection projection = radarMapViewAdapter.getProjection();

        currentLogicalPosition = projection.toLogical(currentGeoPosition);
        futureLogicalPosition = projection.toLogical(futureGeoPosition);

        logicalDotPositions.clear();

        final Iterator<IRadarDataPacket> radarDataIterator = trackDisplayState.getTrack().iterator();
        final int trackHistoryLength = radarMapViewAdapter.getTrackHistoryLength();

        for (int i = 0; i < trackHistoryLength && radarDataIterator.hasNext(); ++i) {
            final IRadarDataPacket radarDataPacket = radarDataIterator.next();
            final Point2D geographicalPosition = radarDataPacket.getPosition();
            final Point2D logicalPosition = projection.toLogical(geographicalPosition);
            logicalDotPositions.add(logicalPosition);
        }
        updateDisplayPositions();
    }

    protected synchronized void updateDisplayPositions() {
        /* Ensure that the formerly occupied region is repainted */
        radarMapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);

        final AffineTransform logical2device = radarMapViewAdapter.getLogicalToDeviceTransform();
        logical2device.transform(currentLogicalPosition, currentDevicePosition);

        double trueCourse = (trackDisplayState.guiContact != null) ? trackDisplayState.guiContact.getHeadingD() : 0;

        Point2D headingLineStart = Converter2D.getMapDisplayPoint(currentDevicePosition, trueCourse, 4d);
        Point2D headingLineEnd = Converter2D.getMapDisplayPoint(currentDevicePosition, trueCourse, 7d + trackDisplayState.getTrack().getCurrentState()
                .getCalculatedVelocity() / 2);
        displayHeadingLine = new Line2D.Double(headingLineStart, headingLineEnd);
        displayExtents = displayHeadingLine.getBounds2D();

        // the dots
        int i = 0;
        // shift all shapes backwards and reuse the last as first
        final int maxCount = radarMapViewAdapter.getTrackHistoryLength();
        ContactShape displayDotShape = displayDotShapes.size() == maxCount ? displayDotShapes.remove(maxCount - 1) : new ContactShape();
        master.getAirportData().getDatablockLayoutManager().getActiveLayout().modify(displayDotShape, trackDisplayState.getGuiContact());
        // assign contact and current status
        displayDotShapes.add(0, displayDotShape);

        // update data
        for (Point2D logicalPosition : logicalDotPositions) {
            final Point2D displayPosition = logical2device.transform(logicalPosition, null);
            displayDotShapes.get(i).setCenter(displayPosition);
            Rectangle2D.union(displayDotShapes.get(i).getBounds2D(), displayExtents, displayExtents);
            i++;
        }
        // compose description field for contact
        Rectangle2D.union(contactTextPainter.getDisplayExtents(currentDevicePosition), displayExtents, displayExtents);

        repaint();
    }

    @Override
    public synchronized Point2D getCenterViewCoordinates() {
        return currentDevicePosition;
    }

    @Override
    public double getMilesPerDot() {
        return radarMapViewAdapter.getLogicalScale() / 1590;
    }

    @Override
    public int getAirSpeed() {
        return (int) trackDisplayState.guiContact.getAirSpeedD();
    }

    public Point2D convertToDeviceLocation(Point2D geoPoint) {
        Point2D logicalPoint = radarMapViewAdapter.getProjection().toLogical(geoPoint);
        return radarMapViewAdapter.getLogicalToDeviceTransform().transform(logicalPoint, null);
    }

    public Point2D getPlayersMapPosition() {
        return convertToDeviceLocation(trackDisplayState.getTrack().getCurrentState().getPosition());
    }

    public IMapViewerAdapter getMapViewAdapter() {
        return radarMapViewAdapter;
    }

    public String getTooltipText(Point p) {
        return null; // displayExtents.contains(p) ? getTooltipText() : null;
    }

    // private String getTooltipText() {
    // GuiRadarContact c = trackDisplayState.getGuiContact();
    // return
    // "<html><body>"+c.getCallSign()+"<br>"+c.getAircraft()+"  "+c.getRadarContactDistance()+" NM"+(c.getAtcComment()!=null?"<br>"+c.getAtcComment():"")+"</body></html>";
    // }
}
