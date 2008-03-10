package de.knewcleus.radar.ui.labels;

import de.knewcleus.radar.ui.aircraft.AircraftState;

public class StaticTextLabelElement extends AbstractTextLabelElement {
	protected String text="";

	public StaticTextLabelElement(ILabelDisplay labelDisplay, AircraftState aircraftState) {
		super(labelDisplay, aircraftState);
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
