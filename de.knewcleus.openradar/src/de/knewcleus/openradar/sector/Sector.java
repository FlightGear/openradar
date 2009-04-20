package de.knewcleus.openradar.sector;

public interface Sector {
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public Bounds getBounds();
	public void setBounds(Bounds bounds);
	
	public Topology getTopology();
	public void setTopology(Topology topology);
}
