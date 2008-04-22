package de.knewcleus.radar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.map.RadarMapPanel;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;
import de.knewcleus.radar.vessels.Track;

public class TrailSymbol extends ComposedTrackSymbolPart {
	protected final List<Point2D> trailPointPositions=new ArrayList<Point2D>();
	
	public TrailSymbol(ComposedTrackSymbol parent) {
		super(parent);
	}
	
	@Override
	public JComponent getDisplayComponent() {
		return parent.getDisplayComponent();
	}

	@Override
	public synchronized Rectangle2D getBounds() {
		Rectangle2D bounds=null;
		for (Point2D position: trailPointPositions) {
			final Rectangle2D pointBounds=TrackDisplayHelper.getTrailSymbolBounds(position);
			if (bounds==null) {
				bounds=pointBounds;
			} else {
				Rectangle2D.union(bounds, pointBounds, bounds);
			}
		}
		return bounds;
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		if (trailPointPositions.isEmpty())
			return;
		// TODO: if we have a correlated vessel, ask the vessel for the trail color
		g.setColor(Palette.BLACK);
		
		for (Point2D position: trailPointPositions) {
			TrackDisplayHelper.drawTrailSymbol(g, position);
		}
	}

	@Override
	public synchronized void validate() {
		invalidate();
		trailPointPositions.clear();
		final RadarMapPanel mapPanel=getParent().getDisplayComponent();
		final RadarPlanViewSettings settings=mapPanel.getSettings();
		final Track track=getParent().getAssociatedTrack();
		final Deque<Position> positionBuffer=track.getPositionBuffer();
		if (positionBuffer.size()<2)
			return;
		final Iterator<Position> trailIterator=positionBuffer.descendingIterator();
		trailIterator.next();
		
		final ICoordinateTransformation mapTransformation=mapPanel.getMapTransformation();
		final IDeviceTransformation deviceTransformation=mapPanel.getDeviceTransformation();
		
		for (int i=0;i<settings.getTrackHistoryLength() && trailIterator.hasNext();i++) {
			
			final Position realPosition=trailIterator.next();
			final Position mapPosition=mapTransformation.forward(realPosition);
			final Point2D devicePosition=deviceTransformation.toDevice(mapPosition);
			trailPointPositions.add(devicePosition);
		}
		invalidate();
	}
}
