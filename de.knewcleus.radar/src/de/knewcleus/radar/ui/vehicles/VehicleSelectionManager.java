package de.knewcleus.radar.ui.vehicles;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class VehicleSelectionManager implements IVehicleUpdateListener {
	private final static Logger logger=Logger.getLogger(VehicleSelectionManager.class.getName());
	
	protected final VehicleManager vehicleManager;
	protected final Set<IVehicleSelectionListener> vehicleSelectionListeners=new HashSet<IVehicleSelectionListener>();
	protected IVehicle selectedVehicle=null;
	
	public VehicleSelectionManager(VehicleManager vehicleManager) {
		this.vehicleManager=vehicleManager;
		vehicleManager.registerVehicleUpdateListener(this);
	}
	
	public void registerVehicleSelectionListener(IVehicleSelectionListener listener) {
		synchronized(vehicleSelectionListeners) {
			vehicleSelectionListeners.add(listener);
		}
	}
	
	public void unregisterVehicleSelectionListener(IVehicleSelectionListener listener) {
		synchronized(vehicleSelectionListeners) {
			vehicleSelectionListeners.remove(listener);
		}
	}

	@Override
	public void vehicleLost(IVehicle lostVehicle) {
		if (lostVehicle==selectedVehicle) {
			logger.info("Selected vehicle was lost => deselecting");
			deselect();
		}
	}

	@Override
	public void vehicleUpdated(Set<IVehicle> updatedVehicles) {
		// NO-OP
	}
	
	public synchronized void select(IVehicle vehicle) {
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
	
	public synchronized IVehicle getSelectedVehicle() {
		return selectedVehicle;
	}
	
	private void fireVehicleSelectionChanged(IVehicle oldSelection, IVehicle newSelection) {
		synchronized(vehicleSelectionListeners) {
			for (IVehicleSelectionListener listener: vehicleSelectionListeners) {
				listener.vehicleSelectionChanged(oldSelection, newSelection);
			}
		}
	}

}
