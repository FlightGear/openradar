/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012,2013 Wolfram Wagner
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
package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navaids.Pavement;
import de.knewcleus.fgfs.navdata.model.IAerodrome;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.xplane.RawFrequency;
import de.knewcleus.fgfs.navdata.xplane.Runway;

public class Aerodrome implements IAerodrome, IIntersection {
	protected final Point2D geographicPosition;
    protected boolean highlighted = false;

	protected final Point2D towerPosition;
	protected final float elevation;
	protected final String identification;
	protected final String name;
	protected final Type type;
	protected volatile List<RawFrequency> frequencies;
	protected final List<Pavement> pavements = new ArrayList<Pavement>();
	protected final List<Runway> runways = new ArrayList<Runway>();
	
	public Aerodrome(Point2D geographicPosition, Point2D towerPosition, float elevation,
			String identification, String name, Type type) {
		this.geographicPosition = geographicPosition;
		this.towerPosition = towerPosition;
		this.elevation = elevation;
		this.identification = identification;
		this.name = name;
		this.type = type;
	}
	
    public synchronized boolean isHighlighted() {
        return highlighted;
    }

    public synchronized void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

	
	@Override
	public Point2D getGeographicPosition() {
		return geographicPosition;
	}
	
    @Override
    public Point2D getTowerPosition() {
        return towerPosition;
    }

    @Override
	public float getElevation() {
		return elevation;
	}
	
	@Override
	public String getIdentification() {
		return identification;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return String.format("Aerodrome %s (%s) %s (%+10.6f,%+11.6f) elev %4.0fft",
				identification,
				name,
				type.toString(),
				geographicPosition.getY() / Units.DEG,
				geographicPosition.getX() / Units.DEG,
				elevation / Units.FT);
	}

    @Override
    public void setFrequencies(List<RawFrequency> frequencies) {
        this.frequencies=frequencies;
    }
    @Override
    public List<RawFrequency> getFrequencies() {
        return frequencies;
    }
    
    public void setPavements(List<Pavement> list) {
        pavements.addAll(list);
    }
    
    public List<Pavement> getPavements() {
        return pavements;
    }
    public void addRunway(Runway rwy) {
        runways.add(rwy);
    }
    public void setRunways(List<Runway> list) {
        runways.addAll(list);
    }
    
    public List<Runway> getRunways() {
        return runways;
    }
}
