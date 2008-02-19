package de.knewcleus.fgfs.navaids;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AirwayDB {
	protected final Map<String, Airway> airwayByDesignator=new HashMap<String, Airway>();
	protected final Set<Airway> airways=new HashSet<Airway>();
	
	public void addAirway(Airway airway) {
		airways.add(airway);
		airwayByDesignator.put(airway.getDesignator(),airway);
	}
	
	public Airway getOrAddAirway(String designator) {
		if (airwayByDesignator.containsKey(designator))
			return airwayByDesignator.get(designator);
		Airway airway=new Airway(designator);
		addAirway(airway);
		return airway;
	}
	
	public Set<Airway> getAirways() {
		return Collections.unmodifiableSet(airways);
	}
}
