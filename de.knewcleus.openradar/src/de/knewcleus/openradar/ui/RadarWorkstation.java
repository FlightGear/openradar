package de.knewcleus.openradar.ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import de.knewcleus.fgfs.location.LocalSphericalProjection;
import de.knewcleus.fgfs.util.GeometryConversionException;
import de.knewcleus.openradar.aircraft.AircraftStateManager;
import de.knewcleus.openradar.aircraft.ICorrelationDatabase;
import de.knewcleus.openradar.aircraft.ISquawkAllocator;
import de.knewcleus.openradar.sector.Sector;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewDisplay;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewSettings;
import de.knewcleus.openradar.vessels.IPositionDataProvider;
import de.knewcleus.openradar.vessels.TrackManager;

public class RadarWorkstation {
	protected final Sector sector;
	protected final ISquawkAllocator squawkAllocator;
	protected final ICorrelationDatabase correlationDatabase;
	protected final TrackManager targetManager;
	protected final AircraftStateManager aircraftStateManager;
	protected final RadarPlanViewSettings radarPlanViewSettings=new RadarPlanViewSettings();
	
	/* Globally provided windows */
	protected final List<WorkstationGlobalFrame> globalFrames=new ArrayList<WorkstationGlobalFrame>();
	
	/* The set of desktops */
	protected final List<RadarDesktop> desktops=new ArrayList<RadarDesktop>();

	public RadarWorkstation(Sector sector, IPositionDataProvider radarDataProvider, ISquawkAllocator squawkAllocator, ICorrelationDatabase correlationDatabase) throws GeometryConversionException {
		this.sector=sector;
		this.squawkAllocator=squawkAllocator;
		this.correlationDatabase=correlationDatabase;
		radarPlanViewSettings.setRange(sector.getDefaultXRange());
		targetManager=new TrackManager(correlationDatabase);
		targetManager.addPositionDataProvider(radarDataProvider);
		aircraftStateManager=new AircraftStateManager();
		
		/* Make sure to register all global frames before creating the desktops */
		final RadarPlanViewDisplay radarPlanViewDisplay=new RadarPlanViewDisplay(this, new LocalSphericalProjection(sector.getInitialCenter()));
		registerGlobalFrame(radarPlanViewDisplay);
		final FGCOMFrame comFrame=new FGCOMFrame(this);
		registerGlobalFrame(comFrame);
		
		/* Create a desktop on every device */
		GraphicsEnvironment graphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice graphicsDevice: graphicsEnvironment.getScreenDevices()) {
			RadarDesktop desktop;
			
			desktop=new RadarDesktop(graphicsDevice.getDefaultConfiguration(),this);
			desktop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			desktops.add(desktop);
		}
		
		final RadarDesktop primaryDesktop=desktops.get(0);
		
		/* Place the global frames on the first desktop */
		for (WorkstationGlobalFrame frame: globalFrames) {
			frame.acquire(primaryDesktop);
		}
	}
	
	public void setVisible(boolean visible) {
		for (RadarDesktop desktop: desktops) {
			desktop.setVisible(visible);
		}
	}
	
	public void loadPreferences(Preferences prefs) {
		// TODO
	}
	
	public void savePreferences(Preferences prefs) {
		// TODO
	}
	
	public Sector getSector() {
		return sector;
	}
	
	public ICorrelationDatabase getCorrelationDatabase() {
		return correlationDatabase;
	}
	
	public ISquawkAllocator getSquawkAllocator() {
		return squawkAllocator;
	}
	
	public TrackManager getTargetManager() {
		return targetManager;
	}
	
	public AircraftStateManager getAircraftStateManager() {
		return aircraftStateManager;
	}
	
	public RadarPlanViewSettings getRadarPlanViewSettings() {
		return radarPlanViewSettings;
	}
	
	public void registerGlobalFrame(WorkstationGlobalFrame frame) {
		globalFrames.add(frame);
	}
	
	public List<WorkstationGlobalFrame> getGlobalFrames() {
		return globalFrames;
	}
	
	public void unregisterGlobalFrame(WorkstationGlobalFrame frame) {
		globalFrames.remove(frame);
	}
}
