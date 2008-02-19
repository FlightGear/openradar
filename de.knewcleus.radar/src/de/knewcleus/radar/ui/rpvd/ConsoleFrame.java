package de.knewcleus.radar.ui.rpvd;

import javax.swing.JFrame;

import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.radar.Scenario;

public class ConsoleFrame extends JFrame {
	private static final long serialVersionUID = -1297013164857514584L;
	
	protected final ICoordinateTransformation transform;
	protected final RadarPlanViewPanel radarPlanViewPanel;

	public ConsoleFrame(String title, Scenario scenario, ICoordinateTransformation transform) {
		super(title);
		this.transform=transform;
		radarPlanViewPanel=new RadarPlanViewPanel(scenario, transform);
		add(radarPlanViewPanel);
		pack();
	}
}
