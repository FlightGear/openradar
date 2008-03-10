package de.knewcleus.radar.ui.labels;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import de.knewcleus.radar.ui.aircraft.AircraftState;

public class CallsignLabelElement extends AbstractTextLabelElement implements IActiveLabelElement {

	public CallsignLabelElement(ILabelDisplay labelDisplay, AircraftState aircraftState) {
		super(labelDisplay, aircraftState);
	}

	@Override
	protected String getText() {
		return getAircraftState().getAircraft().getCallsign();
	}

	@Override
	public void processMouseEvent(MouseEvent event) {
		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			if (event.getButton()==MouseEvent.BUTTON1) {
				final AircraftState aircraftState=getAircraftState();
				JPopupMenu popupMenu=new JPopupMenu(aircraftState.getAircraft().getCallsign());
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
	public String toString() {
		return "Callsign label "+getAircraftState().getAircraft().getCallsign();
	}
}
