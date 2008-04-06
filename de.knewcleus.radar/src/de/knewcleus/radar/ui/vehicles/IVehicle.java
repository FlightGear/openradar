package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.radar.targets.Track;

public interface IVehicle {
	public VehicleManager getAircraftManager();
	public Track getTrack();
	public boolean isSelected();
	public void setSelected(boolean isSelected);
	public boolean canSelect();
}
