package de.knewcleus.openradar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.ui.Palette;
import de.knewcleus.openradar.ui.core.DisplayElement;
import de.knewcleus.openradar.ui.map.RadarMapPanel;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewSettings;
import de.knewcleus.openradar.vessels.Track;
import de.knewcleus.openradar.vessels.Track.PositionBacklogEntry;

public class TrailSymbol extends DisplayElement {
	protected final Track associatedTrack;
	protected final List<Point2D> trailPointPositions=new ArrayList<Point2D>();
	
	public TrailSymbol(Track associatedTrack) {
		this.associatedTrack=associatedTrack;
	}
	
	public Track getAssociatedTrack() {
		return associatedTrack;
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
	public synchronized void paintElement(Graphics2D g) {
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
		final RadarMapPanel mapPanel=(RadarMapPanel) getDisplayComponent();
		final RadarPlanViewSettings settings=mapPanel.getSettings();
		final Track track=getAssociatedTrack();
		final Deque<PositionBacklogEntry> positionBuffer=track.getPositionBacklog();
		if (positionBuffer.size()<2)
			return;
		final Iterator<PositionBacklogEntry> trailIterator=positionBuffer.descendingIterator();
		trailIterator.next();
		
		final IMapProjection mapTransformation=mapPanel.getProjection();
		final AffineTransform deviceTransformation=mapPanel.getMapTransformation();
		
		for (int i=0;i<settings.getTrackHistoryLength() && trailIterator.hasNext();i++) {
			
			final Position realPosition=trailIterator.next().getPosition();
			final Point2D mapPosition=mapTransformation.forward(realPosition);
			deviceTransformation.transform(mapPosition, mapPosition);
			trailPointPositions.add(mapPosition);
		}
		invalidate();
	}

	@Override
	public boolean isHit(Point2D position) {
		// TODO Auto-generated method stub
		return false;
	}
}
