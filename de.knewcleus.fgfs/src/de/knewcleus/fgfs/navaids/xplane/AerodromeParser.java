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
package de.knewcleus.fgfs.navaids.xplane;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.NamedFixDB;
import de.knewcleus.fgfs.navaids.Runway;

public class AerodromeParser extends AbstractXPlaneParser {
	protected final NamedFixDB namedFixDB;
	protected String lastID,lastName;
	protected double lastElevation;
	protected double runwayArea;
	protected Vector3D runwayMoment;
	protected List<Runway> runways=new ArrayList<Runway>();

	public AerodromeParser(NamedFixDB namedFixDB, Shape geographicBounds) {
		super(geographicBounds);
		this.namedFixDB=namedFixDB;
	}

	@Override
	protected void processRecord(String line) throws DBParserException {
		String[] tokens=line.split("\\s+",2);
		
		if (tokens[0].equals("1") || tokens[0].equals("16") || tokens[0].equals("17")) {
			processAerodrome(tokens[0],tokens[1].split("\\s+",5));
		}
		
		if (tokens[0].equals("10")) {
			processPavement(tokens[1].split("\\s+",15));
		}
	}
	
	protected void processAerodrome(String code, String tokens[]) {
		if (runwayMoment!=null) {
			runwayMoment=runwayMoment.scale(1.0/runwayArea);
			
			if (isInRange(runwayMoment.getX(), runwayMoment.getY())) {
				runwayMoment=runwayMoment.add(new Vector3D(0,0,lastElevation));
				Position arp=new Position(runwayMoment);
				Aerodrome aerodrome=new Aerodrome(lastID,lastName,arp);
				for (Runway runway: runways)
					aerodrome.addRunway(runway);
				namedFixDB.addFix(aerodrome);
			}
		}
		
		runways.clear();
		lastElevation=Double.parseDouble(tokens[0])*Units.FT;
		lastID=tokens[3];
		lastName=tokens[4];
		runwayMoment=new Position(0,0,0);
		runwayArea=0.0;
	}
	
	protected void processPavement(String tokens[]) {
		if (tokens[2].equals("xxx"))
			return; // skip taxiways
		
		double lat=Double.parseDouble(tokens[0]);
		double lon=Double.parseDouble(tokens[1]);
		double length=Double.parseDouble(tokens[4])*Units.FT;
		double width=Double.parseDouble(tokens[7])*Units.FT;
		double area=length*width;
		
		String designation=tokens[2];
		if (designation.charAt(designation.length()-1)=='x') {
			designation=designation.substring(0, designation.lastIndexOf('x'));
		}
		Position center=new Position(lon,lat,0.0);
		double trueHeading=Double.parseDouble(tokens[3])*Units.DEG;
		
		runwayMoment=runwayMoment.add(new Vector3D(lon*area, lat*area, 0));
		runwayArea+=area;
		
		Runway runway=new Runway(center,designation,trueHeading,length);
		runways.add(runway);
	}
	
	@Override
	protected void endStream() throws DBParserException {
		if (runwayMoment!=null) {
			runwayMoment=runwayMoment.scale(1.0/runwayArea);
			
			if (isInRange(runwayMoment.getX(), runwayMoment.getY())) {
				runwayMoment=runwayMoment.add(new Vector3D(0,0,lastElevation));
				Position arp=new Position(runwayMoment);
				namedFixDB.addFix(new Aerodrome(lastID,lastName,arp));
			}
		}
		super.endStream();
	}

}
