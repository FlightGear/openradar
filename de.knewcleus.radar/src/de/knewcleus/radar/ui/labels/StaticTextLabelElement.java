package de.knewcleus.radar.ui.labels;

import de.knewcleus.radar.ui.rpvd.AircraftSymbol;

public class StaticTextLabelElement extends AbstractTextLabelElement {
	protected String text="";

	public StaticTextLabelElement(AircraftSymbol aircraftSymbol) {
		super(aircraftSymbol);
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
