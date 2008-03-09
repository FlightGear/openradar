package de.knewcleus.radar.ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.aircraft.IRadarDataProvider;
import de.knewcleus.radar.sector.Sector;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewDisplay;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;

public class RadarWorkstation {
	protected final Sector sector;
	protected final AircraftStateManager aircraftStateManager;
	protected final RadarPlanViewSettings radarPlanViewSettings=new RadarPlanViewSettings();
	
	/* Globally provided windows */
	protected final RadarPlanViewDisplay radarPlanViewDisplay;
	
	/* The set of desktops */
	protected final List<RadarDesktop> desktops=new ArrayList<RadarDesktop>();

	public RadarWorkstation(Sector sector, IRadarDataProvider<? extends IAircraft> radarDataProvider) {
		this.sector=sector;
		aircraftStateManager=new AircraftStateManager(radarDataProvider);
		radarPlanViewDisplay=new RadarPlanViewDisplay(this);
		
		/* Create a desktop on every device */
		GraphicsEnvironment graphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice graphicsDevice: graphicsEnvironment.getScreenDevices()) {
			RadarDesktop desktop;
			
			desktop=new RadarDesktop(graphicsDevice.getDefaultConfiguration(),this);
			desktop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			desktops.add(desktop);
		}
		
		/* Place the plan view panel on the first desktop */
		radarPlanViewDisplay.setVisible(true);
		desktops.get(0).add(radarPlanViewDisplay);
	}
	
	public void setVisible(boolean visible) {
		for (RadarDesktop desktop: desktops) {
			desktop.setVisible(visible);
		}
	}
	
	public Sector getSector() {
		return sector;
	}
	
	public AircraftStateManager getAircraftStateManager() {
		return aircraftStateManager;
	}
	
	public RadarPlanViewSettings getRadarPlanViewSettings() {
		return radarPlanViewSettings;
	}
	
	public RadarPlanViewDisplay getRadarPlanViewDisplay() {
		return radarPlanViewDisplay;
	}
}
