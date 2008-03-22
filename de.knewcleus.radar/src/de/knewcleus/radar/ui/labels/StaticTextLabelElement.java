package de.knewcleus.radar.ui.labels;

import de.knewcleus.radar.ui.aircraft.Aircraft;

public class StaticTextLabelElement extends AbstractTextLabelElement {
	protected String text="";

	public StaticTextLabelElement(ILabelDisplay labelDisplay, Aircraft aircraft) {
		super(labelDisplay, aircraft);
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
