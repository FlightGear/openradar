package de.knewcleus.radar.ui.labels;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import de.knewcleus.radar.ui.aircraft.AircraftState;
import de.knewcleus.radar.ui.rpvd.AircraftSymbol;

public class CallsignLabelElement extends AbstractTextLabelElement implements IActiveLabelElement {

	public CallsignLabelElement(AircraftSymbol aircraftSymbol) {
		super(aircraftSymbol);
	}

	@Override
	protected String getText() {
		return getAircraftSymbol().getAircraftState().getAircraft().getCallsign();
	}

	@Override
	public void processMouseEvent(MouseEvent event) {
		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			if (event.getButton()==MouseEvent.BUTTON1) {
				AircraftState aircraftState=getAircraftSymbol().getAircraftState();
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
		return "Callsign label "+getAircraftSymbol().getAircraftState().getAircraft().getCallsign();
	}
}
