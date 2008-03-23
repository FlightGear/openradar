package de.knewcleus.radar.ui.vehicles;

import java.util.Set;

public interface IVehicleUpdateListener {
	public abstract void vehicleUpdated(Set<IVehicle> updatedVehicles);
	public abstract void vehicleLost(IVehicle lostVehicles);
}
