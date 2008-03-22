package de.knewcleus.radar.ui.labels;

import de.knewcleus.radar.aircraft.Target;

public class StaticTextLabelElement extends AbstractTextLabelElement {
	protected String text="";

	public StaticTextLabelElement(ILabelDisplay labelDisplay, Target aircraftState) {
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
