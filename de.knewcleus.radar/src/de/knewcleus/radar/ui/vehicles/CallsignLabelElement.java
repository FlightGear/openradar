package de.knewcleus.radar.ui.vehicles;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;

public class CallsignLabelElement extends AbstractTextLabelElement {
	protected final Aircraft aircraft;
	
	public CallsignLabelElement(Aircraft aircraft) {
		this.aircraft=aircraft;
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

				popupMenu.show(getDisplayComponent(), event.getX(), event.getY());
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
