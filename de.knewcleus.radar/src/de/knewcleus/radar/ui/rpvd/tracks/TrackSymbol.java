package de.knewcleus.radar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.autolabel.DisplayObject;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.core.WorkObjectSymbol;
import de.knewcleus.radar.ui.map.RadarMapPanel;
import de.knewcleus.radar.vessels.Track;
import de.knewcleus.radar.vessels.Vessel;

public class TrackSymbol extends WorkObjectSymbol implements DisplayObject {
	protected final Track associatedTrack;
	protected Point2D devicePosition=new Point2D.Double();

	public TrackSymbol(Track associatedTrack) {
		this.associatedTrack=associatedTrack;
	}
	
	public Track getAssociatedTrack() {
		return associatedTrack;
	}
	
	@Override
	public Vessel getAssociatedObject() {
		return associatedTrack.getCorrelatedVessel();
	}
	
	@Override
	public Rectangle2D getBounds() {
		// TODO: if we have an correlated vessel, ask the vessel for the bounds
		return TrackDisplayHelper.getTrackSymbolBounds(devicePosition);
	}

	@Override
	public void paintElement(Graphics2D g) {
		// TODO: if we have a correlated vessel, tell the vessel to paint
		g.setColor(Palette.BLACK);
		TrackDisplayHelper.drawTrackSymbol(g, devicePosition);
	}

	@Override
	public void validate() {
		invalidate();
		final RadarMapPanel mapPanel=(RadarMapPanel) getDisplayComponent();
		final ICoordinateTransformation mapTransformation=mapPanel.getMapTransformation();
		final IDeviceTransformation deviceTransformation=mapPanel.getDeviceTransformation();
		
		final Position realPosition=getAssociatedTrack().getPosition();
		final Position mapPosition=mapTransformation.forward(realPosition);
		devicePosition=deviceTransformation.toDevice(mapPosition);
		validateDependents();
		invalidate();
	}

	@Override
	public boolean isHit(Point2D position) {
		return getBounds().contains(position);
	}

	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	@Override
	public double getPriority() {
		return 10;
	}
	
	@Override
	public Point2D getRelativeHookPoint(double vx, double vy) {
		// TODO: if we have an correlated vessel, ask the vessel for the hook point
		return TrackDisplayHelper.getRelativeTrackSymbolHookPoint(vx, vy);
	}
}
