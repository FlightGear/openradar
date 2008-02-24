package de.knewcleus.radar.ui.rpvd;

import javax.swing.JFrame;

import de.knewcleus.radar.Scenario;

public class ConsoleFrame extends JFrame {
	private static final long serialVersionUID = -1297013164857514584L;
	
	protected final RadarPlanViewSettings radarPlanViewSettings;
	protected final RadarPlanViewPanel radarPlanViewPanel;

	public ConsoleFrame(String title, Scenario scenario, RadarPlanViewSettings radarPlanViewSettings) {
		super(title);
		this.radarPlanViewSettings=radarPlanViewSettings;
		radarPlanViewPanel=new RadarPlanViewPanel(scenario, radarPlanViewSettings);
		add(radarPlanViewPanel);
		pack();
	}
}
