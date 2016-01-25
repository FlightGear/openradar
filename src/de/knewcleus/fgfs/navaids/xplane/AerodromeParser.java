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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navaids.xplane;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.NamedFixDB;
import de.knewcleus.fgfs.navaids.Pavement;

public class AerodromeParser extends AbstractXPlaneParser {
	protected final NamedFixDB namedFixDB;
	protected String lastID,lastName;
	protected double lastElevation;
	protected double runwayArea;
	protected Vector3D runwayMoment;
//	protected List<Runway> runways=new ArrayList<Runway>();
	protected List<Pavement> pavements = new ArrayList<>();
	protected Pavement currentPavement = null;

	private final Logger log = Logger.getLogger(AerodromeParser.class);
	
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
		
        if (tokens[0].equals("110") || tokens[0].equals("111")||tokens[0].equals("112")||tokens[0].equals("113")||tokens[0].equals("114")||tokens[0].equals("115")||tokens[0].equals("116")) {
            processPavement(tokens[1].split("\\s+",15));
        }
	}
	
	private void processPavement(String[] def) {
        if(def[0].equals("110")) {
            // pavement header
            currentPavement = new Pavement(def);
        } else {
            // node
            if(currentPavement==null) {
                log.fatal("Trying to add a node to a not existing pavement!");
            } else {
                currentPavement.addNode(def);
                Point2D position = currentPavement.getNodes().get(0)!=null?currentPavement.getNodes().get(0).point : null;
                
                if(position==null || !isInRange(position.getX(),position.getY())) {
                    // not in range
                    return;
                }
                if(def[0].equals("113")||def[0].equals("114")||def[0].equals("115")||def[0].equals("116")) {
                    // close loop or end lines
                    pavements.add(currentPavement);
                    currentPavement=null;
                }
            }
        }
    }

    protected void processAerodrome(String code, String tokens[]) {
		if (runwayMoment!=null) {
			runwayMoment=runwayMoment.scale(1.0/runwayArea);
			
			if (isInRange(runwayMoment.getX(), runwayMoment.getY())) {
				runwayMoment=runwayMoment.add(new Vector3D(0,0,lastElevation));
				Position arp=new Position(runwayMoment);
				Aerodrome aerodrome=new Aerodrome(lastID,lastName,arp);
				aerodrome.addPavements(pavements);
				namedFixDB.addFix(aerodrome);
			}
		}
		
//		runways.clear();
		lastElevation=Double.parseDouble(tokens[0])*Units.FT;
		lastID=tokens[3];
		lastName=tokens[4];
		runwayMoment=new Position(0,0,0);
		runwayArea=0.0;
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
