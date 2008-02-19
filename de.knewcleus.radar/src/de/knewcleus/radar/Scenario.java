package de.knewcleus.radar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.sector.Sector;

public class Scenario implements IUpdateable {
	protected final Sector sector;
	protected Set<IAircraft> aircraft=new HashSet<IAircraft>();
	
	protected double timewarp=1.0;
	protected double elapsedTime=0.0;
	
	public Scenario(Sector sector) {
		this.sector=sector;
	}
	
	public synchronized void addAircraft(IAircraft acft) {
		aircraft.add(acft);
	}
	
	public synchronized void removeAircraft(IAircraft acft) {
		aircraft.remove(acft);
	}
	
	public synchronized boolean hasAircraft(IAircraft acft) {
		return aircraft.contains(acft);
	}
	
	public synchronized Set<IAircraft> getAircraft() {
		return Collections.unmodifiableSet(aircraft);
	}
	
	public Sector getSector() {
		return sector;
	}
	
	public void setTimewarp(double timewarp) {
		this.timewarp = timewarp;
	}
	
	public double getTimewarp() {
		return timewarp;
	}
	
	public double getElapsedTime() {
		return elapsedTime;
	}
	
	public synchronized void update(double dt) {
		elapsedTime+=dt*timewarp;
		for (IAircraft acft: aircraft)
			acft.update(dt*timewarp);
	}
	
	public synchronized Set<IAircraft> findAircraftInRange(double west, double south, double east, double north) {
		Set<IAircraft> found=new HashSet<IAircraft>();
		for (IAircraft acft: aircraft) {
			Position pos=acft.getPosition();
			if (pos.getX()<east || west<pos.getX())
				continue;
			if (pos.getY()<south || north<pos.getY())
				continue;
			found.add(acft);
		}
		return found;
	}
}
