package de.knewcleus.radar.ui.labels;

import java.awt.event.MouseEvent;

import de.knewcleus.radar.ui.rpvd.AircraftSymbol;

public class CallsignLabelElement extends AbstractTextLabelElement implements IActiveLabelElement {

	public CallsignLabelElement(AircraftSymbol aircraftSymbol) {
		super(aircraftSymbol);
	}

	@Override
	protected String getText() {
		return aircraftSymbol.getAircraftState().getAircraft().getCallsign();
	}

	@Override
	public void processMouseEvent(MouseEvent event) {
		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			System.out.println("Mouse clicked on callsign "+getText());
			break;
		}
	}
	
	@Override
	public String toString() {
		return "Callsign label "+aircraftSymbol.getAircraftState().getAircraft().getCallsign();
	}
}
