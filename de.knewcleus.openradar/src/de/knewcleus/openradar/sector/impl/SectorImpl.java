package de.knewcleus.openradar.sector.impl;

import de.knewcleus.openradar.sector.Bounds;
import de.knewcleus.openradar.sector.Sector;
import de.knewcleus.openradar.sector.Topology;

public class SectorImpl implements Sector {
	protected String name = null;
	protected String description = null;
	protected Bounds bounds = null;
	protected Topology topology = null;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}

	@Override
	public Topology getTopology() {
		return topology;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}

	@Override
	public void setDescription(String description) {
		this.description=description;
	}

	@Override
	public void setBounds(Bounds bounds) {
		this.bounds=bounds;
	}

	@Override
	public void setTopology(Topology topology) {
		this.topology=topology;
	}
}
