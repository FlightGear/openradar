package de.knewcleus.radar.ui.labels;

import java.awt.event.MouseEvent;

public interface IActiveLabelElement extends ILabelElement {
	public void processMouseEvent(MouseEvent event);
	public boolean isEnabled();
}
