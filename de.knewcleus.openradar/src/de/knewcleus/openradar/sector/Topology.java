package de.knewcleus.openradar.sector;

import java.util.List;

public interface Topology {
	public List<MapLayer> getLandmass();
	void setLandmass(List<MapLayer> landmass);
	
	public List<MapLayer> getWatermass();
	void setWatermass(List<MapLayer> watermass);
	
	public List<MapLayer> getRunways();
	void setRunways(List<MapLayer> runways);
	
	public List<MapLayer> getTarmac();
	void setTarmac(List<MapLayer> tarmac);
}
