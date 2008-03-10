package de.knewcleus.radar.ui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import de.knewcleus.radar.ui.rpvd.RadarPlanViewDisplay;

public class RadarDesktop extends JFrame {
	private static final long serialVersionUID = 8819041738184510314L;
	
	protected final RadarWorkstation workstation;
	
	protected final JDesktopPane desktopPane=new JDesktopPane();
	
	protected final GeneralToolbox generalToolbox=new GeneralToolbox(this);
	
	protected Rectangle lastRPVDBounds;

	public RadarDesktop(GraphicsConfiguration gc, RadarWorkstation workstation) {
		super(gc);
		this.workstation=workstation;
		setContentPane(desktopPane);
		
		desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		desktopPane.setPreferredSize(new Dimension(400,400));
		
		generalToolbox.setVisible(true);
		add(generalToolbox, JLayeredPane.PALETTE_LAYER);
		
		pack();
	}
	
	public RadarWorkstation getWorkstation() {
		return workstation;
	}
	
	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}
	
	public void acquireRadarPlanViewDisplay() {
		RadarPlanViewDisplay radarPlanViewDisplay=workstation.getRadarPlanViewDisplay();

		RadarDesktop oldDesktop=workstation.getRadarPlanViewDesktop();
		if (oldDesktop!=this) {
			if (oldDesktop!=null) {
				oldDesktop.lastRPVDBounds=radarPlanViewDisplay.getBounds();
				radarPlanViewDisplay.setVisible(false);
				oldDesktop.remove(radarPlanViewDisplay);
				oldDesktop.generalToolbox.setRPVDPresent(false);
			}
			if (lastRPVDBounds!=null) {
				radarPlanViewDisplay.setBounds(lastRPVDBounds);
			}
			add(radarPlanViewDisplay);
			radarPlanViewDisplay.setVisible(true);
		}
		
		generalToolbox.setRPVDPresent(true);
		workstation.setRadarPlanViewDesktop(this);
	}
}
