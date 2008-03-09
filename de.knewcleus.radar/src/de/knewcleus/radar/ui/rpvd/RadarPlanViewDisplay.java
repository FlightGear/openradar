package de.knewcleus.radar.ui.rpvd;

import java.awt.Dimension;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import de.knewcleus.radar.ui.RadarWorkstation;

public class RadarPlanViewDisplay extends JInternalFrame {
	private static final long serialVersionUID = 5923481231980915972L;
	
	protected final RadarWorkstation workstation;
	protected final RadarPlanViewPanel radarPlanViewPanel;
	protected final RadarToolbox radarToolbox;

	public RadarPlanViewDisplay(RadarWorkstation workstation) {
		super("RPVD",true,false,true,false);
		this.workstation=workstation;
		
		radarPlanViewPanel=new RadarPlanViewPanel(workstation);
		radarToolbox=new RadarToolbox(this);
		
		setContentPane(radarPlanViewPanel);
		
		setPreferredSize(new Dimension(400,400));
		
		radarToolbox.setVisible(true);
		add(radarToolbox);
		try {
			radarToolbox.setIcon(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pack();
	}
	
	public RadarWorkstation getWorkstation() {
		return workstation;
	}
}
