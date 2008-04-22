package de.knewcleus.radar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.map.RadarMapPanel;

public class TrackSymbol extends ComposedTrackSymbolPart {
	protected Point2D devicePosition;

	public TrackSymbol(ComposedTrackSymbol parent) {
		super(parent);
	}
	
	@Override
	public Rectangle2D getBounds() {
		// TODO: if we have an correlated vessel, ask the vessel for the bounds
		return TrackDisplayHelper.getTrackSymbolBounds(devicePosition);
	}

	@Override
	public void paint(Graphics2D g) {
		// TODO: if we have a correlated vessel, tell the vessel to paint
		g.setColor(Palette.BLACK);
		TrackDisplayHelper.drawTrackSymbol(g, devicePosition);
	}

	@Override
	public void validate() {
		final RadarMapPanel mapPanel=getParent().getDisplayComponent();
		final ICoordinateTransformation mapTransformation=mapPanel.getMapTransformation();
		final IDeviceTransformation deviceTransformation=mapPanel.getDeviceTransformation();
		
		final Position realPosition=getParent().getAssociatedTrack().getPosition();
		final Position mapPosition=mapTransformation.forward(realPosition);
		devicePosition=deviceTransformation.toDevice(mapPosition);
	}
}
