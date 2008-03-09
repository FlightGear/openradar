package de.knewcleus.radar.ui.rpvd;

import java.awt.Dimension;

import javax.swing.JInternalFrame;

import de.knewcleus.radar.ui.RadarWorkstation;

public class RadarPlanViewDisplay extends JInternalFrame {
	private static final long serialVersionUID = 5923481231980915972L;
	
	protected final RadarWorkstation workstation;
	protected final RadarPlanViewPanel radarPlanViewPanel;

	public RadarPlanViewDisplay(RadarWorkstation workstation) {
		super("RPVD",true,false,true,false);
		this.workstation=workstation;
		radarPlanViewPanel=new RadarPlanViewPanel(workstation);
		
		add(radarPlanViewPanel);
		radarPlanViewPanel.setPreferredSize(new Dimension(400,400));
		pack();
		radarPlanViewPanel.setVisible(true);
	}
}
