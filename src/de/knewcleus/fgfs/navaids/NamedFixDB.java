/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
