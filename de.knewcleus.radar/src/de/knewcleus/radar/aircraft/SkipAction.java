package de.knewcleus.radar.aircraft;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class SkipAction extends AbstractAction {
	private static final long serialVersionUID = -1501115746360788522L;
	protected final AircraftState aircraftState;
	
	public SkipAction(AircraftState aircraftState) {
		super("SKIP");
		this.aircraftState=aircraftState;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		aircraftState.skip();
	}

}
