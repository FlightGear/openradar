package de.knewcleus.openradar.view.map.test;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.ISSRData;
import de.knewcleus.openradar.rpvd.IRadarMapViewerAdapter;
import de.knewcleus.openradar.tracks.ITrack;
import de.knewcleus.openradar.tracks.TrackUpdateNotification;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IContainer;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.layout.Alignment;
import de.knewcleus.openradar.view.layout.HorizontalFlowLayoutManager;
import de.knewcleus.openradar.view.layout.ILayoutManager;
import de.knewcleus.openradar.view.layout.ILayoutPartContainer;
import de.knewcleus.openradar.view.layout.ILayoutPartVisitor;
import de.knewcleus.openradar.view.layout.Insets2D;
import de.knewcleus.openradar.view.layout.VerticalFlowLayoutManager;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;

public class LabelView implements IBoundedView, IContainer, INotificationListener, ILayoutPartContainer {
	protected final IRadarMapViewerAdapter radarMapViewerAdapter;
	protected final ITrack track;
	protected final ILayoutManager layoutManager;
	
	protected Point2D logicalTrackPosition;
	protected Point2D displayTrackPosition;
	protected Rectangle2D displayExtents = new Rectangle2D.Double();
	
	protected final LayoutGroup lines[] = new LayoutGroup[2];
	
	protected final TextView callsignView;
	protected final TextView speedView;
	protected final TextView flightLevelView;
	
	public LabelView(IRadarMapViewerAdapter radarMapViewerAdapter, ITrack track) {
		this.radarMapViewerAdapter = radarMapViewerAdapter;
		this.track = track;
		layoutManager = new VerticalFlowLayoutManager(this, Alignment.LEADING);
		
		lines[0] = new LayoutGroup(this);
		lines[1] = new LayoutGroup(this);
		
		lines[0].setLayoutManager(new HorizontalFlowLayoutManager(lines[0], Alignment.TRAILING));
		lines[1].setLayoutManager(new HorizontalFlowLayoutManager(lines[1], Alignment.LEADING, 5));
		
		callsignView = new TextView(radarMapViewerAdapter, lines[0]);
		speedView = new TextView(radarMapViewerAdapter, lines[1]);
		flightLevelView = new TextView(radarMapViewerAdapter, lines[1]);
		
		lines[0].add(callsignView);
		lines[1].add(speedView);
		lines[1].add(flightLevelView);
		
		radarMapViewerAdapter.registerListener(this);
		track.registerListener(this);
	}
	
	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitContainer(this);
	}
	
	@Override
	public boolean isVisible() {
		/* Radar labels are always visible */
		return true;
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			calculateDisplayPosition();
		} else if (notification instanceof ProjectionNotification) {
			calculateLogicalPosition();
		} else if (notification instanceof TrackUpdateNotification) {
			updateLabelContents();
			calculateLogicalPosition();
		}
	}
	
	protected void updateLabelContents() {
		final IRadarDataPacket currentState = track.getCurrentState();
		final int speed10Kts = (int)Math.round(currentState.getCalculatedVelocity() / Units.KNOTS / 10.0);
		final String speedString = String.format("%03d",speed10Kts);
		
		final ISSRData ssrData = currentState.getSSRData();
		
		final String callsignString;
		if (ssrData == null || !ssrData.hasMarkXModeACode()) {
			callsignString ="-----";
		} else {
			callsignString = String.format("A%4s",ssrData.getMarkXModeACode());
		}
		
		final String flightlevelString;
		if (ssrData == null || !ssrData.hasMarkXModeCElevation()) {
			flightlevelString="---";
		} else {
			final int flightlevel = (int)Math.round(ssrData.getMarkXModeCElevation() / Units.FT / 100);
			flightlevelString = String.format("%03d",flightlevel);
		}
		
		callsignView.setText(callsignString);
		speedView.setText(speedString);
		flightLevelView.setText(flightlevelString);
	}
	
	@Override
	public void invalidate() {
		layoutManager.invalidate();
		radarMapViewerAdapter.getUpdateManager().markViewInvalid(this);
	}
	
	protected void calculateLogicalPosition() {
		final IRadarDataPacket currentTrackStatus = track.getCurrentState();
		final Point2D geographicalPosition = currentTrackStatus.getPosition();
		final IProjection projection = radarMapViewerAdapter.getProjection();
		logicalTrackPosition = projection.toLogical(geographicalPosition);
		calculateDisplayPosition();
	}
	
	protected void calculateDisplayPosition() {
		final AffineTransform logical2device = radarMapViewerAdapter.getLogicalToDeviceTransform();
		displayTrackPosition = logical2device.transform(logicalTrackPosition, null);
		
		/* Ensure that our formerly occupied region is repainted */
		radarMapViewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
		displayExtents=new Rectangle2D.Double(
				displayTrackPosition.getX() + 50, displayTrackPosition.getY() - 50,
				displayExtents.getWidth(), displayExtents.getHeight());
		radarMapViewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
		invalidate();
	}
	
	@Override
	public void validate() {
		final Dimension2D labelSize = layoutManager.getPreferredSize();
		radarMapViewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
		displayExtents = new Rectangle2D.Double(
				displayExtents.getX(), displayExtents.getY(),
				labelSize.getWidth(), labelSize.getHeight());
		radarMapViewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
		layoutManager.layout(displayExtents);
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}
	
	@Override
	public void paint(Graphics2D g2d) {}
	
	@Override
	public void traverse(ILayoutPartVisitor visitor) {
		visitor.visit(lines[0]);
		visitor.visit(lines[1]);
	}
	
	@Override
	public void traverse(IViewVisitor visitor) {
		callsignView.accept(visitor);
		speedView.accept(visitor);
		flightLevelView.accept(visitor);
	}
	
	@Override
	public Insets2D getInsets() {
		return new Insets2D();
	}
}
