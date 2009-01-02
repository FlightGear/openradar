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

import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.tracks.ITrack;
import de.knewcleus.openradar.tracks.TrackUpdateNotification;
import de.knewcleus.openradar.ui.Palette;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;

/**
 * The radar target view displays the symbols for a radar target, the radar trail and the 
 * heading line.
 * 
 * @author Ralf Gerlich
 *
 */
public class RadarTargetView implements IBoundedView, INotificationListener {
	protected final static double targetDotRadius = 3.0;
	
	protected final static GeodesicUtils geodesicUtils=new GeodesicUtils(Ellipsoid.WGS84);
	
	protected final IRadarMapViewerAdapter radarMapViewAdapter;
	protected final ITrack track;
	
	protected final List<Point2D> logicalDotPositions = new ArrayList<Point2D>();
	protected final List<Shape> displayDotShapes = new ArrayList<Shape>();
	protected Line2D logicalHeadingLine = null;
	protected Rectangle2D displayExtents = new Rectangle2D.Double();
	
	public RadarTargetView(IRadarMapViewerAdapter radarMapViewAdapter, ITrack track) {
		this.radarMapViewAdapter = radarMapViewAdapter;
		this.track = track;
		radarMapViewAdapter.registerListener(this);
		track.registerListener(this);
	}
	
	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public void revalidate() {
		updateLogicalPositions();
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		Color color=Palette.BLACK;
		final int count = radarMapViewAdapter.getTrackHistoryLength();
		int i = count;
		for (Shape displayDotShape: displayDotShapes) {
			color=new Color(color.getRed(), color.getGreen(), color.getBlue(), 255*i/count);
			g2d.setColor(color);
			g2d.fill(displayDotShape);
			--i;
		}
		g2d.setColor(Palette.BLACK);
		final AffineTransform oldTransform = g2d.getTransform();
		g2d.transform(radarMapViewAdapter.getLogicalToDeviceTransform());
		g2d.draw(logicalHeadingLine);
		g2d.setTransform(oldTransform);
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof ProjectionNotification) {
			invalidate();
		} else if (notification instanceof CoordinateSystemNotification) {
			invalidate();
		} else if (notification instanceof TrackUpdateNotification) {
			invalidate();
		}
	}
	
	protected void invalidate() {
		radarMapViewAdapter.getUpdateManager().invalidateView(this);
		repaint();
	}
	
	protected void repaint() {
		radarMapViewAdapter.getUpdateManager().addDirtyView(this);
	}
	
	protected void updateLogicalPositions() {
		final IProjection projection = radarMapViewAdapter.getProjection();
		logicalDotPositions.clear();
		
		final Iterator<IRadarDataPacket> radarDataIterator = track.iterator();
		final int trackHistoryLength = radarMapViewAdapter.getTrackHistoryLength();
		
		for (int i=0; i<=trackHistoryLength && radarDataIterator.hasNext(); ++i) {
			final IRadarDataPacket radarDataPacket = radarDataIterator.next();
			final Point2D geographicalPosition = radarDataPacket.getPosition();
			final Point2D logicalPosition = projection.toLogical(geographicalPosition);
			logicalDotPositions.add(logicalPosition);
		}
		
		final IRadarDataPacket currentStatus = track.getCurrentState();
		final Point2D currentGeoPosition = currentStatus.getPosition();
		final GeodesicInformation geodInfo = geodesicUtils.direct(
				currentGeoPosition.getX(), currentGeoPosition.getY(),
				currentStatus.getCalculatedTrueCourse(),
				currentStatus.getCalculatedVelocity()*radarMapViewAdapter.getHeadingVectorTime());
		final Point2D futureGeoPosition = new Point2D.Double(geodInfo.getEndLon(), geodInfo.getEndLat());
		
		final Point2D currentLogicalPosition = projection.toLogical(currentGeoPosition);
		final Point2D futureLogicalPosition = projection.toLogical(futureGeoPosition);
		
		logicalHeadingLine = new Line2D.Double(currentLogicalPosition, futureLogicalPosition);
		updateDisplayPositions();
	}
	
	protected void updateDisplayPositions() {
		final AffineTransform logical2device = radarMapViewAdapter.getLogicalToDeviceTransform();
		displayDotShapes.clear();
		final Shape displayHeadingLine = logical2device.createTransformedShape(logicalHeadingLine);
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
