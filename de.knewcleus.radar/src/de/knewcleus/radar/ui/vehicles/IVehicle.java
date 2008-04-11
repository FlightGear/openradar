package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.radar.targets.Track;
import de.knewcleus.radar.ui.IWorkableObject;

public interface IVehicle extends IWorkableObject {
	public VehicleManager getAircraftManager();
	public Track getTrack();
}
