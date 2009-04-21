package de.knewcleus.openradar.sector.impl;

import de.knewcleus.openradar.sector.Bounds;

public class BoundsImpl implements Bounds {
	protected double north = 0.0;
	protected double south = 0.0;
	protected double west = 0.0;
	protected double east = 0.0;
	
	@Override
	public double getNorth() {
		return north;
	}
	
	@Override
	public void setNorth(double north) {
		this.north = north;
	}
	
	@Override
	public double getSouth() {
		return south;
	}
	
	@Override
	public void setSouth(double south) {
		this.south = south;
	}
	
	@Override
	public double getWest() {
		return west;
	}
	
	@Override
	public void setWest(double west) {
		this.west = west;
	}
	
	@Override
	public double getEast() {
		return east;
	}
	
	@Override
	public void setEast(double east) {
		this.east = east;
	}
	
	@Override
	public String toString() {
		return "Bounds[n="+north+", s="+south+", w="+west+", e="+east+"]";
	}
}
