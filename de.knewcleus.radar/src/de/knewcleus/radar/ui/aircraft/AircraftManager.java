package de.knewcleus.radar.ui.aircraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.radar.targets.ITargetUpdateListener;
import de.knewcleus.radar.targets.Target;
import de.knewcleus.radar.ui.RadarWorkstation;

public class AircraftManager implements ITargetUpdateListener {
	private final static Logger logger=Logger.getLogger(AircraftManager.class.getName());
	protected final RadarWorkstation radarWorkstation;
	protected final Map<Target, Aircraft> aircraftMap=new HashMap<Target, Aircraft>();
	protected final Set<IAircraftUpdateListener> aircraftUpdateListeners=new HashSet<IAircraftUpdateListener>();
	protected Aircraft selectedAircraft=null;
	
	public AircraftManager(RadarWorkstation radarWorkstation) {
		this.radarWorkstation=radarWorkstation;
	}
	
	public RadarWorkstation getRadarWorkstation() {
		return radarWorkstation;
	}
	
	public void registerAircraftUpdateListener(IAircraftUpdateListener listener) {
		aircraftUpdateListeners.add(listener);
	}
	
	public void unregisterAircraftUpdateListener(IAircraftUpdateListener listener) {
		aircraftUpdateListeners.remove(listener);
	}
	
	@Override
	public synchronized void targetsUpdated(Set<Target> targets) {
		Set<Aircraft> updatedAircraft=new HashSet<Aircraft>();
		for (Target target: targets) {
			Aircraft aircraft=aircraftMap.get(target);
			if (aircraft==null) {
				aircraft=new Aircraft(this,target);
				aircraftMap.put(target,aircraft);
			}
			updatedAircraft.add(aircraft);
		}
		fireAircraftUpdated(updatedAircraft);
	}
	
	@Override
	public synchronized void targetLost(Target target) {
		Aircraft aircraft=aircraftMap.get(target);
		if (aircraft==null)
			return; // ignore, we do not know that target
		aircraftMap.remove(target);
		if (aircraft==selectedAircraft) {
			deselect();
		}
		fireAircraftLost(aircraft);
	}
	
	private void fireAircraftUpdated(Set<Aircraft> updatedAircraft) {
		for (IAircraftUpdateListener listener: aircraftUpdateListeners) {
			listener.aircraftUpdated(updatedAircraft);
		}
	}
	
	private void fireAircraftLost(Aircraft lostAircraft) {
		for (IAircraftUpdateListener listener: aircraftUpdateListeners) {
			listener.aircraftLost(lostAircraft);
		}
	}
	
	public synchronized void select(Aircraft aircraft) {
		if (selectedAircraft!=null) {
			logger.fine("Deselecting aircraft "+selectedAircraft);
			selectedAircraft.setSelected(false);
		}
		selectedAircraft=aircraft;
		if (selectedAircraft!=null) {
			logger.fine("Selecting aircraft "+selectedAircraft);
			selectedAircraft.setSelected(true);
		}
	}
	
	public void deselect() {
		select(null);
	}
	
	public Aircraft getSelectedAircraft() {
		return selectedAircraft;
	}
}
