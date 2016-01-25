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

import static de.knewcleus.fgfs.Units.DEG;
import static de.knewcleus.fgfs.Units.FT;
import static de.knewcleus.fgfs.Units.MHz;
import static de.knewcleus.fgfs.Units.NM;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.awt.Shape;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.NamedFixDB;
import de.knewcleus.fgfs.navaids.VOR;

public class NavParser extends AbstractXPlaneParser {
	protected final NamedFixDB namedFixDB;

	public NavParser(NamedFixDB namedFixDB, Shape geographicBounds) {
		super(geographicBounds);
		this.namedFixDB=namedFixDB;
	}
	
	@Override
	protected void processRecord(String line) throws DBParserException {
		String[] tokens=line.split("\\s+",9);
		double lon,lat;
		lat=parseDouble(tokens[1]);
		lon=parseDouble(tokens[2]);
		
		if (!isInRange(lon, lat))
			return;
		
		double elev=parseDouble(tokens[3])*FT;
		
		Position pos=new Position(lon,lat,elev);
		
		int freq=parseInt(tokens[4]);
		
		double range=parseInt(tokens[5])*NM;
		
		String id=tokens[7];
		String name=tokens[8];
		
		int type=parseInt(tokens[0]);
		
		switch (type) {
		case 3:
			namedFixDB.addFix(new VOR(id,pos,name,freq*MHz/100.0,range,parseDouble(tokens[6])*DEG));
			break;
		}
	}
}
