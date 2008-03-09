package de.knewcleus.radar.ui.rpvd;

import javax.swing.JFrame;

import de.knewcleus.radar.aircraft.AircraftStateManager;
import de.knewcleus.radar.sector.Sector;

public class ConsoleFrame extends JFrame {
	private static final long serialVersionUID = -1297013164857514584L;
	
	protected final RadarPlanViewSettings radarPlanViewSettings;
	protected final RadarPlanViewPanel radarPlanViewPanel;

	public ConsoleFrame(String title, AircraftStateManager aircraftStateManager, Sector sector, RadarPlanViewSettings radarPlanViewSettings) {
		super(title);
		this.radarPlanViewSettings=radarPlanViewSettings;
		radarPlanViewPanel=new RadarPlanViewPanel(aircraftStateManager, sector, radarPlanViewSettings);
		add(radarPlanViewPanel);
		pack();
	}
}
