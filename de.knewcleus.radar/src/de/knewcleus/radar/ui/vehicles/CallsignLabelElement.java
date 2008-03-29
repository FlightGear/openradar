package de.knewcleus.radar.ui.vehicles;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPopupMenu;

import de.knewcleus.radar.aircraft.AircraftState;
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
	
	protected String getDisplayedCallsign() {
		if (!aircraft.getTrack().getSSRMode().hasSSRCode()) {
			return "****";
		}
		String callsign=aircraft.getCallsign();
		if (callsign!=null)
			return callsign;
		return "A"+aircraft.getTrack().getSSRCode();
	}

	@Override
	public void processMouseEvent(MouseEvent event) {
		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			if (event.getButton()==MouseEvent.BUTTON1) {
				final AircraftState aircraftState=aircraft.getAircraftState();
				assert(aircraftState!=null);
				List<Action> availableActions=aircraftState.getAvailableActions();
				JPopupMenu popupMenu=new JPopupMenu(aircraft.getCallsign());
				for (Action action: availableActions) {
					popupMenu.add(action);
				}

				popupMenu.show(getDisplayComponent(), event.getX(), event.getY());
				event.consume();
			}
			break;
		}
	}
	
	@Override
	public boolean isEnabled() {
		return aircraft.isCorrelated();
	}
}
