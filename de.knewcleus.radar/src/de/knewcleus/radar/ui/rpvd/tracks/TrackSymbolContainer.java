package de.knewcleus.radar.ui.rpvd.tracks;

import de.knewcleus.radar.DisplayElementContainer;
import de.knewcleus.radar.ui.map.RadarMapPanel;
import de.knewcleus.radar.vessels.Track;
import de.knewcleus.radar.vessels.Vessel;

public class TrackSymbolContainer extends DisplayElementContainer {
	protected final Track associatedTrack;
	protected final TrackSymbol trackSymbol;
	protected final TrailSymbol trailSymbol;
	protected final HeadingLineSymbol headingLineSymbol;
	protected final TrackLabelSymbol trackLabelSymbol;
	
	public TrackSymbolContainer(Track associatedTrack) {
		this.associatedTrack=associatedTrack;
		trackSymbol=new TrackSymbol(associatedTrack);
		trailSymbol=new TrailSymbol(associatedTrack);
		headingLineSymbol=new HeadingLineSymbol(associatedTrack);
		trackLabelSymbol=new TrackLabelSymbol(trackSymbol);
		
		add(headingLineSymbol);
		add(trailSymbol);
		add(trackSymbol);
		add(trackLabelSymbol);
	}
	
	public Track getAssociatedTrack() {
		return associatedTrack;
	}
	
	public TrackSymbol getTrackSymbol() {
		return trackSymbol;
	}
	
	@Override
	public RadarMapPanel getDisplayComponent() {
		return (RadarMapPanel) super.getDisplayComponent();
	}
	
	public Vessel getAssociatedObject() {
		return getAssociatedTrack().getAssociatedVessel();
	}
}
