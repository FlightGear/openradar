package de.knewcleus.fgfs.navaids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.knewcleus.fgfs.location.Position;

public class Aerodrome extends AbstractNamedFix {
	protected final String name;
	protected final List<Runway> runways=new ArrayList<Runway>();

	public Aerodrome(String id, String name, Position position) {
		super(id, position);
		this.name=name;
	}

	public String getName() {
		return name;
	}
	
	public void addRunway(Runway runway) {
		runways.add(runway);
	}
	
	public List<Runway> getRunways() {
		return Collections.unmodifiableList(runways);
	}
}
