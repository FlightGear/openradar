package de.knewcleus.radar.autolabel;

public interface LabeledObject extends DisplayObject {
	public Label getLabel();
	public boolean isLocked();
}
