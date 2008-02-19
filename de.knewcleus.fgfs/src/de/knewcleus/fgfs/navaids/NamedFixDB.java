package de.knewcleus.fgfs.navaids;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.knewcleus.fgfs.location.Position;

public class NamedFixDB {
	protected Map<String, Set<NamedFix>> fixIndex=new HashMap<String, Set<NamedFix>>();
	protected Set<NamedFix> fixes=new HashSet<NamedFix>();
	
	public void addFix(NamedFix fix) {
		Set<NamedFix> fixSet;
		if (fixIndex.containsKey(fix.getID())) {
			fixSet=fixIndex.get(fix.getID());
		} else {
			fixSet=new HashSet<NamedFix>();
			fixIndex.put(fix.getID(),fixSet);
		}
		fixSet.add(fix);
		fixes.add(fix);
	}
	
	public Set<NamedFix> getFixes() {
		return Collections.unmodifiableSet(fixes);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends NamedFix> Set<T> getFixes(Class<T> clazz) {
		Set<T> selectedFixes=new HashSet<T>();
		
		for (NamedFix fix: fixes) {
			if (clazz.isInstance(fix))
				selectedFixes.add((T)fix);
		}
		
		return selectedFixes;
	}
	
	public NamedFix findNearestFix(String name, Position reference) {
		return findNearestFix(name, reference, NamedFix.class);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends NamedFix> T findNearestFix(String name, Position reference, Class<T> clazz) {
		if (!fixIndex.containsKey(name))
			return null; // TODO: throw NoSuchFixException
		Set<NamedFix> fixSet=fixIndex.get(name);
		
		NamedFix nearestFix=null;
		double nearestDistSquared=0.0;
		for (NamedFix fix: fixSet) {
			if (nearestFix==null) {
				nearestFix=fix;
				continue;
			}
			
			Position fixPos=fix.getPosition();
			double dx,dy;
			
			dx=fixPos.getX()-reference.getX();
			dy=fixPos.getY()-reference.getY();
			double distSquared=dx*dx+dy*dy;
			
			if (distSquared<nearestDistSquared) {
				nearestFix=fix;
				nearestDistSquared=distSquared;
			}
		}
		
		return (T)nearestFix;
	}
}
