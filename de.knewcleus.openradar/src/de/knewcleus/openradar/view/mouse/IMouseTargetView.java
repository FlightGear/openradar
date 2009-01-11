package de.knewcleus.openradar.view.mouse;

import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.IView;

public interface IMouseTargetView extends IPickable, IView {
	public void processMouseInteractionEvent(MouseInteractionEvent e);
}
