package de.knewcleus.openradar.aircraft;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class TransferAction extends AbstractAction {
	private static final long serialVersionUID = 7138898951504708500L;
	protected final AircraftState aircraftState;

	public TransferAction(AircraftState aircraftState) {
		super("TRANSFER");
		this.aircraftState=aircraftState;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		aircraftState.transfer();
	}

}
