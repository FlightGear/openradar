package de.knewcleus.radar.ui.aircraft;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.ui.labels.IActiveLabelElement;
import de.knewcleus.radar.ui.labels.ILabelDisplay;

public class CallsignLabelElement extends AbstractTextLabelElement implements IActiveLabelElement {

	public CallsignLabelElement(ILabelDisplay labelDisplay, Aircraft aircraft) {
		super(labelDisplay, aircraft);
	}

	@Override
	protected String getText() {
		return aircraft.getCallsign();
	}

	@Override
	public void processMouseEvent(MouseEvent event) {
		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			if (event.getButton()==MouseEvent.BUTTON1) {
				// FIXME: prepare the menu according to the associatedTarget task state
				JPopupMenu popupMenu=new JPopupMenu(aircraft.getCallsign());
				popupMenu.add("ASSUME");
				popupMenu.add("TRANSFER");
				popupMenu.add("HANDOVER");
				popupMenu.add("RELEASE");
				popupMenu.add("FORCE ACT");

				showPopupMenu(popupMenu, event.getX(), event.getY());
				event.consume();
				
			}
			break;
		}
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}
