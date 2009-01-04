package de.knewcleus.openradar.view.map.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.rpvd.IRadarMapViewerAdapter;
import de.knewcleus.openradar.tracks.ITrack;
import de.knewcleus.openradar.tracks.TrackUpdateNotification;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IContainer;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.layout.ILayoutManager;
import de.knewcleus.openradar.view.layout.ILayoutPartContainer;
import de.knewcleus.openradar.view.layout.ILayoutPartVisitor;
import de.knewcleus.openradar.view.layout.Insets2D;
import de.knewcleus.openradar.view.layout.VerticalFlowLayoutManager;
import de.knewcleus.openradar.view.layout.VerticalFlowLayoutManager.Alignment;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;

public class LabelView implements IBoundedView, IContainer, INotificationListener, ILayoutPartContainer {
	protected final IRadarMapViewerAdapter radarMapViewerAdapter;
	protected final ITrack track;
	protected final ILayoutManager layoutManager;
	
	protected Point2D logicalTrackPosition;
	protected Point2D displayTrackPosition;
	protected Rectangle2D displayExtents = new Rectangle2D.Double();
	
	protected final TextView callsignView;
	protected final TextView speedView;
	
	public LabelView(IRadarMapViewerAdapter radarMapViewerAdapter, ITrack track) {
		this.radarMapViewerAdapter = radarMapViewerAdapter;
		this.track = track;
		layoutManager = new VerticalFlowLayoutManager(this, Alignment.LEADING);
		callsignView = new TextView(radarMapViewerAdapter, this);
		speedView = new TextView(radarMapViewerAdapter, this);
		radarMapViewerAdapter.registerListener(this);
		track.registerListener(this);
	}
	
	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			invalidate();
		} else if (notification instanceof ProjectionNotification) {
			invalidate();
		} else if (notification instanceof TrackUpdateNotification) {
			invalidate();
		}
	}
	
	protected void invalidate() {
		radarMapViewerAdapter.getUpdateManager().invalidateView(this);
		radarMapViewerAdapter.getUpdateManager().addDirtyView(this);
	}
	
	@Override
	public void revalidate() {
		final IRadarDataPacket currentTrackStatus = track.getCurrentState();
		final Point2D geographicalPosition = currentTrackStatus.getPosition();
		final IProjection projection = radarMapViewerAdapter.getProjection();
		logicalTrackPosition = projection.toLogical(geographicalPosition);
		
		final AffineTransform logical2device = radarMapViewerAdapter.getLogicalToDeviceTransform();
		displayTrackPosition = logical2device.transform(logicalTrackPosition, null);
		
		final Dimension2D labelSize = layoutManager.getPreferredSize();
		displayExtents = new Rectangle2D.Double(
				displayTrackPosition.getX() + 50, displayTrackPosition.getY() - 50,
				labelSize.getWidth(), labelSize.getHeight());
		layoutManager.layout(displayExtents);
		radarMapViewerAdapter.getUpdateManager().addDirtyView(this);
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(Color.GRAY);
		g2d.draw(displayExtents);
	}
	
	@Override
	public void invalidateLayout() {
		invalidate();
	}
	
	@Override
	public void traverse(ILayoutPartVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void traverse(IViewVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Insets2D getInsets() {
		return new Insets2D();
	}
}
