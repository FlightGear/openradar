package de.knewcleus.radar.ui.vehicles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.knewcleus.radar.targets.ITrackUpdateListener;
import de.knewcleus.radar.targets.Track;
import de.knewcleus.radar.ui.RadarWorkstation;

public class VehicleManager implements ITrackUpdateListener {
	protected final RadarWorkstation radarWorkstation;
	protected final Map<Track, IVehicle> vehicleMap=new HashMap<Track, IVehicle>();
	protected final Set<IVehicleUpdateListener> vehicleUpdateListeners=new HashSet<IVehicleUpdateListener>();
	
	public VehicleManager(RadarWorkstation radarWorkstation) {
		this.radarWorkstation=radarWorkstation;
	}
	
	public RadarWorkstation getRadarWorkstation() {
		return radarWorkstation;
	}
	
	public void registerVehicleUpdateListener(IVehicleUpdateListener listener) {
		vehicleUpdateListeners.add(listener);
	}
	
	public void unregisterVehicleUpdateListener(IVehicleUpdateListener listener) {
		vehicleUpdateListeners.remove(listener);
	}
	
	@Override
	public synchronized void tracksUpdated(Set<Track> targets) {
		Set<IVehicle> updatedVehicles=new HashSet<IVehicle>();
		for (Track target: targets) {
			IVehicle vehicle=vehicleMap.get(target);
			if (vehicle==null) {
				vehicle=new Aircraft(this,target);
				vehicleMap.put(target,vehicle);
			}
			updatedVehicles.add(vehicle);
		}
		fireVehicleUpdated(updatedVehicles);
	}
	
	@Override
	public synchronized void trackLost(Track target) {
		IVehicle aircraft=vehicleMap.get(target);
		if (aircraft==null)
			return; // ignore, we do not know that target
		vehicleMap.remove(target);
		fireVehicleLost(aircraft);
	}
	
	private void fireVehicleUpdated(Set<IVehicle> updatedAircraft) {
		for (IVehicleUpdateListener listener: vehicleUpdateListeners) {
			listener.vehicleUpdated(updatedAircraft);
		}
	}
	
	private void fireVehicleLost(IVehicle lostAircraft) {
		for (IVehicleUpdateListener listener: vehicleUpdateListeners) {
			listener.vehicleLost(lostAircraft);
		}
	}
}
