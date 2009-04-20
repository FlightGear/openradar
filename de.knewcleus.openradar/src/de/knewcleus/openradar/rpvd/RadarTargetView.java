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
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.openradar.Palette;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.tracks.TrackUpdateNotification;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;
import de.knewcleus.openradar.view.mouse.FocusChangeNotification;
import de.knewcleus.openradar.view.mouse.IFocusableView;
import de.knewcleus.openradar.view.mouse.IMouseTargetView;
import de.knewcleus.openradar.view.mouse.MouseInteractionEvent;

/**
 * The radar target view displays the symbols for a radar target, the radar trail and the 
 * heading line.
 * 
 * @author Ralf Gerlich
 *
 */
public class RadarTargetView implements IBoundedView, INotificationListener, IFocusableView, IMouseTargetView, ITrackDisplay {
	protected final static double targetDotRadius = 1.5*Units.MM/Units.PT;
	
	protected final static GeodesicUtils geodesicUtils=new GeodesicUtils(Ellipsoid.WGS84);

	protected static final double headingLineVicinity = 0.5*Units.MM/Units.PT;
	
	protected final IRadarMapViewerAdapter radarMapViewAdapter;
	protected final TrackDisplayState trackDisplayState;
	
	protected final List<Point2D> logicalDotPositions = new ArrayList<Point2D>();
	protected final List<Shape> displayDotShapes = new ArrayList<Shape>();
	protected Point2D currentGeoPosition = new Point2D.Double();
	protected Point2D futureGeoPosition = new Point2D.Double();
	protected Point2D currentLogicalPosition = new Point2D.Double();
	protected Point2D futureLogicalPosition = new Point2D.Double();
	protected Point2D currentDevicePosition = new Point2D.Double();
	protected Point2D futureDevicePosition = new Point2D.Double();
	protected Line2D displayHeadingLine = new Line2D.Double();
	protected Rectangle2D displayExtents = new Rectangle2D.Double();
	
	public RadarTargetView(IRadarMapViewerAdapter radarMapViewAdapter, TrackDisplayState trackDisplayState) {
		this.radarMapViewAdapter = radarMapViewAdapter;
		this.trackDisplayState = trackDisplayState;
		radarMapViewAdapter.registerListener(this);
		trackDisplayState.registerListener(this);
		trackDisplayState.getTrack().registerListener(this);
		updateGeographicPositions();
	}
	
	@Override
	public boolean contains(Point2D devicePoint) {
		for (Shape shape: displayDotShapes) {
			if (shape.contains(devicePoint)) {
				return true;
			}
		}
		
		/* Check heading line */
		final double headX = futureDevicePosition.getX()-currentDevicePosition.getX();
		final double headY = futureDevicePosition.getY()-currentDevicePosition.getY();
		final double posX = devicePoint.getX()-currentDevicePosition.getX();
		final double posY = devicePoint.getY()-currentDevicePosition.getY();
		final double headLen = Math.sqrt(headX*headX+headY*headY);
		final double projection = headX*posY-headY*posX;
		if (Math.abs(projection)<headingLineVicinity*headLen) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void focusChanged(FocusChangeNotification event) {
		trackDisplayState.setSelected(event.getNewOwner()==this);
	}
	
	@Override
	public void processMouseInteractionEvent(MouseInteractionEvent e) {
	}
	
	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public boolean isVisible() {
		/* Radar tracks are always shown */
		return true;
	}
	
	@Override
	public TrackDisplayState getTrackDisplayState() {
		return trackDisplayState;
	}
	
	@Override
	public void validate() {}
	
	@Override
	public void paint(Graphics2D g2d) {
		final Color baseColor = (trackDisplayState.isSelected()?Palette.WHITE:Palette.BLACK);
		Color color = baseColor;
		final int count = radarMapViewAdapter.getTrackHistoryLength();
		int i = count;
		for (Shape displayDotShape: displayDotShapes) {
			color=new Color(color.getRed(), color.getGreen(), color.getBlue(), 255*i/count);
			g2d.setColor(color);
			g2d.fill(displayDotShape);
			--i;
		}
		g2d.setColor(baseColor);
		g2d.draw(displayHeadingLine);
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}
	
	@Override
	public void acceptNotification(INotification notification) {
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
	
	protected void repaint() {
		radarMapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}
	
	protected void updateGeographicPositions() {
		final IRadarDataPacket currentStatus = trackDisplayState.getTrack().getCurrentState();
		currentGeoPosition = currentStatus.getPosition();
		final GeodesicInformation geodInfo = geodesicUtils.direct(
				currentGeoPosition.getX(), currentGeoPosition.getY(),
				currentStatus.getCalculatedTrueCourse(),
				currentStatus.getCalculatedVelocity()*radarMapViewAdapter.getHeadingVectorTime());
		futureGeoPosition = new Point2D.Double(geodInfo.getEndLon(), geodInfo.getEndLat());
		
		updateLogicalPositions();
	}
	
	protected void updateLogicalPositions() {
		final IProjection projection = radarMapViewAdapter.getProjection();
		
		currentLogicalPosition = projection.toLogical(currentGeoPosition);
		futureLogicalPosition = projection.toLogical(futureGeoPosition);
		
		logicalDotPositions.clear();
		
		final Iterator<IRadarDataPacket> radarDataIterator = trackDisplayState.getTrack().iterator();
		final int trackHistoryLength = radarMapViewAdapter.getTrackHistoryLength();
		
		for (int i=0; i<=trackHistoryLength && radarDataIterator.hasNext(); ++i) {
			final IRadarDataPacket radarDataPacket = radarDataIterator.next();
			final Point2D geographicalPosition = radarDataPacket.getPosition();
			final Point2D logicalPosition = projection.toLogical(geographicalPosition);
			logicalDotPositions.add(logicalPosition);
		}
		
		updateDisplayPositions();
	}
	
	protected void updateDisplayPositions() {
		/* Ensure that the formerly occupied region is repainted */
		radarMapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
		
		final AffineTransform logical2device = radarMapViewAdapter.getLogicalToDeviceTransform();
		displayDotShapes.clear();
		logical2device.transform(currentLogicalPosition, currentDevicePosition);
		logical2device.transform(futureLogicalPosition, futureDevicePosition);
		displayHeadingLine = new Line2D.Double(currentDevicePosition, futureDevicePosition);
		displayExtents = displayHeadingLine.getBounds2D();;
		
		for (Point2D logicalPosition: logicalDotPositions) {
			final Point2D displayPosition = logical2device.transform(logicalPosition, null);
			final Ellipse2D displayDotShape = new Ellipse2D.Double(
					displayPosition.getX()-targetDotRadius,
					displayPosition.getY()-targetDotRadius,
					2.0*targetDotRadius, 2.0*targetDotRadius);
			displayDotShapes.add(displayDotShape);
			Rectangle2D.union(displayDotShape.getBounds2D(), displayExtents, displayExtents);
		}
		repaint();
	}
}
