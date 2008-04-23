package de.knewcleus.radar.ui.rpvd.tracks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.knewcleus.radar.ComposedSymbol;
import de.knewcleus.radar.Symbol;
import de.knewcleus.radar.ui.map.RadarMapPanel;
import de.knewcleus.radar.vessels.Track;
import de.knewcleus.radar.vessels.Vessel;

public class ComposedTrackSymbol extends ComposedSymbol {
	protected final RadarMapPanel panel;
	protected final Track associatedTrack;
	protected final TrackSymbol trackSymbol;
	protected final TrailSymbol trailSymbol;
	protected final HeadingLineSymbol headingLineSymbol;
	protected final TrackLabelSymbol trackLabelSymbol;
	
	protected final List<Symbol> symbolParts=new ArrayList<Symbol>(2);
	
	public ComposedTrackSymbol(RadarMapPanel panel, Track associatedTrack) {
		this.panel=panel;
		this.associatedTrack=associatedTrack;
		trackSymbol=new TrackSymbol(this);
		trailSymbol=new TrailSymbol(this);
		headingLineSymbol=new HeadingLineSymbol(this);
		trackLabelSymbol=new TrackLabelSymbol(this);
		symbolParts.add(headingLineSymbol);
		symbolParts.add(trailSymbol);
		symbolParts.add(trackSymbol);
		symbolParts.add(trackLabelSymbol);
	}
	
	public Track getAssociatedTrack() {
		return associatedTrack;
	}
	
	public TrackSymbol getTrackSymbol() {
		return trackSymbol;
	}
	
	@Override
	public RadarMapPanel getDisplayComponent() {
		return panel;
	}
	
	@Override
	public Vessel getAssociatedObject() {
		return getAssociatedTrack().getAssociatedVessel();
	}

	@Override
	public Collection<Symbol> getSymbolParts() {
		return symbolParts;
	}
	
	@Override
	public void validate() {
		invalidate();
		super.validate();
		invalidate();
	}
}
