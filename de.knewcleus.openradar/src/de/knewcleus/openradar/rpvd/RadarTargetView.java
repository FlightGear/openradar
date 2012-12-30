package de.knewcleus.openradar.rpvd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
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
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
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
 * The radar target view displays the symbols for a radar target, the radar
 * trail and the heading line.
 * 
 * @author Ralf Gerlich
 * 
 */
public class RadarTargetView implements IBoundedView, INotificationListener, IFocusableView, IMouseTargetView, ITrackDisplay {
    protected final static double targetDotRadius = 3.0;

    protected final static GeodesicUtils geodesicUtils = new GeodesicUtils(Ellipsoid.WGS84);

    protected static final double headingLineVicinity = 3.0;

    protected final IRadarMapViewerAdapter radarMapViewAdapter;
    protected final TrackDisplayState trackDisplayState;

    protected final List<Point2D> logicalDotPositions = Collections.synchronizedList(new ArrayList<Point2D>());
    protected final List<Shape> displayDotShapes = Collections.synchronizedList(new ArrayList<Shape>());
    protected volatile Point2D currentGeoPosition = new Point2D.Double();
    protected volatile Point2D futureGeoPosition = new Point2D.Double();
    protected volatile Point2D currentLogicalPosition = new Point2D.Double();
    protected volatile Point2D futureLogicalPosition = new Point2D.Double();
    protected volatile Point2D currentDevicePosition = new Point2D.Double();
    protected volatile Point2D futureDevicePosition = new Point2D.Double();
    protected volatile Line2D displayHeadingLine = new Line2D.Double();
    protected volatile Rectangle2D displayExtents = new Rectangle2D.Double();

    protected RadarContactTextPainter contactTextPainter;

    public RadarTargetView(IRadarMapViewerAdapter radarMapViewAdapter, TrackDisplayState trackDisplayState) {
        this.radarMapViewAdapter = radarMapViewAdapter;
        this.trackDisplayState = trackDisplayState;
        contactTextPainter = new RadarContactTextPainter(trackDisplayState);
        radarMapViewAdapter.registerListener(this);
        trackDisplayState.registerListener(this);
        trackDisplayState.getTrack().registerListener(this);
        updateGeographicPositions();
    }

    @Override
    public synchronized boolean contains(Point2D devicePoint) {
        for (Shape shape : displayDotShapes) {
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
            contactTextPainter.paint(g2d); // paint infos into background

            final Color baseColor = (trackDisplayState.isSelected() ? Palette.RADAR_SELECTED : Palette.BLACK);
            Color color = baseColor;
            final int count = radarMapViewAdapter.getTrackHistoryLength();
            int i = count;
            for (Shape displayDotShape : displayDotShapes) {
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255 * i / count);
                g2d.setColor(color);
                g2d.fill(displayDotShape);
                --i;
            }
            g2d.setColor(baseColor);
            if (trackDisplayState.guiContact.isActive()) {
                g2d.draw(displayHeadingLine);
            }
        }
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

    protected synchronized void repaint() {
        radarMapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
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

        for (int i = 0; i <= trackHistoryLength && radarDataIterator.hasNext(); ++i) {
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
        displayDotShapes.clear();
        logical2device.transform(currentLogicalPosition, currentDevicePosition);
        // logical2device.transform(futureLogicalPosition,
        // futureDevicePosition);
        // displayHeadingLine = new Line2D.Double(currentDevicePosition,
        // futureDevicePosition);
        
        //double trueCourse = trackDisplayState.getTrack().getCurrentState().getCalculatedTrueCourse();
        double trueCourse = (trackDisplayState.guiContact!=null) ? trackDisplayState.guiContact.getHeadingD() : 0; 
                
        Point2D headingLineEnd = Converter2D.getMapDisplayPoint(currentDevicePosition, trueCourse, 
                5d + trackDisplayState.getTrack().getCurrentState().getCalculatedVelocity()/2 );
        displayHeadingLine = new Line2D.Double(currentDevicePosition, headingLineEnd);
        displayExtents = displayHeadingLine.getBounds2D();
        

        for (Point2D logicalPosition : logicalDotPositions) {
            final Point2D displayPosition = logical2device.transform(logicalPosition, null);
            final Ellipse2D displayDotShape = new Ellipse2D.Double(displayPosition.getX() - targetDotRadius, displayPosition.getY() - targetDotRadius,
                    2.0 * targetDotRadius, 2.0 * targetDotRadius);
            displayDotShapes.add(displayDotShape);
            Rectangle2D.union(displayDotShape.getBounds2D(), displayExtents, displayExtents);
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
}
