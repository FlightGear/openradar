package de.knewcleus.radar.ui.labels;

import java.awt.Dimension;
import java.awt.Rectangle;

import de.knewcleus.radar.ui.rpvd.AircraftSymbol;

public abstract class AbstractLabelElement implements ILabelElement {
	protected final AircraftSymbol aircraftSymbol;
	
	protected int ascent;
	protected Dimension minimumSize;
	protected Rectangle bounds;

	public AbstractLabelElement(AircraftSymbol aircraftSymbol) {
		this.aircraftSymbol=aircraftSymbol;
	}

	public AircraftSymbol getAircraftSymbol() {
		return aircraftSymbol;
	}
	
	@Override
	public int getAscent() {
		return ascent;
	}

	@Override
	public Dimension getMinimumSize() {
		return minimumSize;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public void setBounds(Rectangle rectangle) {
		bounds=rectangle;
	}

}