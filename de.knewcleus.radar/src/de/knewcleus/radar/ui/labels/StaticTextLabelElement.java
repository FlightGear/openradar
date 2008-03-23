package de.knewcleus.radar.ui.labels;

public class StaticTextLabelElement extends AbstractTextLabelElement {
	protected String text="";

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
