package de.knewcleus.radar.ui.rpvd;

public class BasicTextLabelElement extends AbstractTextLabelElement {
	protected String text="";

	public BasicTextLabelElement(AircraftSymbol aircraftSymbol) {
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
