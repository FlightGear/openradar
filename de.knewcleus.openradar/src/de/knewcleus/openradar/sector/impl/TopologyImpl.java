package de.knewcleus.openradar.sector.impl;

import java.util.List;

import de.knewcleus.openradar.sector.MapLayer;
import de.knewcleus.openradar.sector.Topology;

public class TopologyImpl implements Topology {
	protected List<MapLayer> landmass = null;
	protected List<MapLayer> watermass = null;
	protected List<MapLayer> runways = null;
	protected List<MapLayer> tarmac = null;
	
	@Override
	public List<MapLayer> getLandmass() {
		return landmass;
	}
	
	@Override
	public void setLandmass(List<MapLayer> landmass) {
		this.landmass = landmass;
	}
	
	@Override
	public List<MapLayer> getWatermass() {
		return watermass;
	}
	
	@Override
	public void setWatermass(List<MapLayer> watermass) {
		this.watermass = watermass;
	}
	
	@Override
	public List<MapLayer> getRunways() {
		return runways;
	}
	
	@Override
	public void setRunways(List<MapLayer> runways) {
		this.runways = runways;
	}
	
	@Override
	public List<MapLayer> getTarmac() {
		return tarmac;
	}
	
	@Override
	public void setTarmac(List<MapLayer> tarmac) {
		this.tarmac = tarmac;
	}
}
