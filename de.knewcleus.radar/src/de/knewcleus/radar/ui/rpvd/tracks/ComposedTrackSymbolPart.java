package de.knewcleus.radar.ui.rpvd.tracks;

import javax.swing.JComponent;

import de.knewcleus.radar.Symbol;
import de.knewcleus.radar.vessels.Vessel;

public abstract class ComposedTrackSymbolPart extends Symbol {
	protected final ComposedTrackSymbol parent;

	public ComposedTrackSymbolPart(ComposedTrackSymbol parent) {
		this.parent=parent;
	}
	
	@Override
	public JComponent getDisplayComponent() {
		return parent.getDisplayComponent();
	}

	public ComposedTrackSymbol getParent() {
		return parent;
	}

	@Override
	public Vessel getAssociatedObject() {
		return parent.getAssociatedObject();
	}

}