package de.knewcleus.radar.ui.vehicles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.radar.targets.ITrackUpdateListener;
import de.knewcleus.radar.targets.Track;
import de.knewcleus.radar.ui.RadarWorkstation;

public class VehicleManager implements ITrackUpdateListener {
	private final static Logger logger=Logger.getLogger(VehicleManager.class.getName());
	protected final RadarWorkstation radarWorkstation;
	protected final Map<Track, IVehicle> vehicleMap=new HashMap<Track, IVehicle>();
	protected final Set<IVehicleUpdateListener> vehicleUpdateListeners=new HashSet<IVehicleUpdateListener>();
	protected final Set<IVehicleSelectionListener> vehicleSelectionListeners=new HashSet<IVehicleSelectionListener>();
	protected IVehicle selectedVehicle=null;
	
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
	
	public void registerVehicleSelectionListener(IVehicleSelectionListener listener) {
		vehicleSelectionListeners.add(listener);
	}
	
	public void unregisterVehicleSelectionListener(IVehicleSelectionListener listener) {
		vehicleSelectionListeners.remove(listener);
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
		if (aircraft==selectedVehicle) {
			deselect();
		}
		fireVehicleLost(aircraft);
	}
	
	public void select(IVehicle vehicle) {
		if (vehicle==selectedVehicle)
			return;
		IVehicle oldSelection=selectedVehicle;
		if (selectedVehicle!=null) {
			logger.fine("Deselecting aircraft "+selectedVehicle);
			selectedVehicle.setSelected(false);
		}
		selectedVehicle=vehicle;
		if (selectedVehicle!=null) {
			logger.fine("Selecting aircraft "+selectedVehicle);
			selectedVehicle.setSelected(true);
		}
		fireVehicleSelectionChanged(oldSelection, selectedVehicle);
	}
	
	public void deselect() {
		select(null);
	}
	
	public IVehicle getSelectedVehicle() {
		return selectedVehicle;
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
	
	private void fireVehicleSelectionChanged(IVehicle oldSelection, IVehicle newSelection) {
		for (IVehicleSelectionListener listener: vehicleSelectionListeners) {
			listener.vehicleSelectionChanged(oldSelection, newSelection);
		}
	}
}
