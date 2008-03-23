package de.knewcleus.radar.aircraft;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AssumeAction extends AbstractAction {
	private static final long serialVersionUID = -4816544542360687005L;
	protected final AircraftState aircraftState;
	
	public AssumeAction(AircraftState aircraftState) {
		super("ASSUME");
		this.aircraftState=aircraftState;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		aircraftState.assume();
	}

}
