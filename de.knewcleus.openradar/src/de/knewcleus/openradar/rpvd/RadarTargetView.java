package de.knewcleus.openradar.rpvd;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.notify.Notifier;
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
public class RadarTargetView extends Notifier implements IBoundedView, INotificationListener {
	protected final static double targetDotRadius = 3.0;
	
	protected final IRadarMapViewerAdapter radarMapViewAdapter;
	protected final ITrack track;
	
	protected final List<Point2D> logicalDotPositions = new ArrayList<Point2D>();
	protected final List<Shape> displayDotShapes = new ArrayList<Shape>();
	protected Ellipse2D logicalHeadingLine = null;
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
		g2d.setColor(Palette.BLACK);
		for (Shape displayDotShape: displayDotShapes) {
			g2d.fill(displayDotShape);
		}
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
		updateDisplayPositions();
	}
	
	protected void updateDisplayPositions() {
		final AffineTransform logical2device = radarMapViewAdapter.getLogicalToDeviceTransform();
		displayDotShapes.clear();
		displayExtents = null;
		
		for (Point2D logicalPosition: logicalDotPositions) {
			final Point2D displayPosition = logical2device.transform(logicalPosition, null);
			final Ellipse2D displayDotShape = new Ellipse2D.Double(
					displayPosition.getX()-targetDotRadius,
					displayPosition.getY()-targetDotRadius,
					2.0*targetDotRadius, 2.0*targetDotRadius);
			displayDotShapes.add(displayDotShape);
			if (displayExtents==null) {
				displayExtents = displayDotShape.getBounds2D();
			} else {
				Rectangle2D.union(displayDotShape.getBounds2D(), displayExtents, displayExtents);
			}
		}
		repaint();
	}
}
