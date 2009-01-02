package de.knewcleus.openradar.view.map.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.notify.Notifier;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.tracks.ITrack;
import de.knewcleus.openradar.tracks.TrackLossStatusNotification;
import de.knewcleus.openradar.tracks.TrackUpdateNotification;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;

public class RadarTargetView extends Notifier implements IBoundedView, INotificationListener {
	protected final static int targetDotRadius = 5;
	
	protected final IMapViewerAdapter mapViewAdapter;
	protected final ITrack track;
	
	protected Point2D logicalPosition = null;
	protected Ellipse2D displayShape = null;
	protected Rectangle2D displayExtents = null;
	
	public RadarTargetView(IMapViewerAdapter mapViewAdapter, ITrack track) {
		this.mapViewAdapter = mapViewAdapter;
		this.track = track;
		mapViewAdapter.registerListener(this);
		track.registerListener(this);
	}

	@Override
	public Rectangle2D getDisplayExtents() {
		if (displayExtents != null) {
			return displayExtents;
		}
		displayExtents = getDisplayShape().getBounds2D();
		return displayExtents;
	}
	
	protected Shape getDisplayShape() {
		if (displayShape != null) {
			return displayShape;
		}
		final Point2D logicalPosition = getLogicalPosition();
		final AffineTransform logicalToDisplay = mapViewAdapter.getLogicalToDeviceTransform();
		final Point2D displayPosition = logicalToDisplay.transform(logicalPosition, null);
		displayShape = new Ellipse2D.Double(
				displayPosition.getX()-targetDotRadius,
				displayPosition.getY()-targetDotRadius,
				2.0*targetDotRadius, 2.0*targetDotRadius);
		return displayShape;
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}

	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor((track.isLost()?Color.RED:Color.GRAY));
		g2d.fill(displayShape);
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof ProjectionNotification) {
			invalidateLogicalExtents();
		} else if (notification instanceof CoordinateSystemNotification) {
			invalidateDisplayExtents();
		} else if (notification instanceof TrackUpdateNotification) {
			invalidateLogicalExtents();
		} else if (notification instanceof TrackLossStatusNotification) {
			repaint();
		}
	}
	
	protected void invalidateLogicalExtents() {
		invalidateDisplayExtents();
	}
	
	protected void invalidateDisplayExtents() {
		mapViewAdapter.getUpdateManager().invalidateView(this);
		repaint();
	}
	
	@Override
	public void revalidate() {
		displayShape = null;
		displayExtents = null;
		logicalPosition = null;
		repaint();
	}
	
	protected void repaint() {
		mapViewAdapter.getUpdateManager().addDirtyView(this);
	}
	
	protected Point2D getLogicalPosition() {
		if (logicalPosition!=null) {
			return logicalPosition;
		}
		final IRadarDataPacket packet = track.getCurrentState();
		final IProjection projection = mapViewAdapter.getProjection();
		logicalPosition = projection.toLogical(packet.getPosition());
		return logicalPosition;
	}
}
