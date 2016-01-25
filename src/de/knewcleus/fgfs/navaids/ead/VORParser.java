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
package de.knewcleus.fgfs.navaids.ead;

import static java.lang.Double.parseDouble;

import java.awt.Shape;

import org.w3c.dom.Element;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.NamedFixDB;
import de.knewcleus.fgfs.navaids.VOR;

public class VORParser extends AbstractSDOParser {
	protected final NamedFixDB namedFixDB;

	public VORParser(NamedFixDB namedFixDB, Shape geographicBounds) {
		super(geographicBounds);
		this.namedFixDB=namedFixDB;
	}

	@Override
	public void processRecord(Element record) throws DBParserException {
		requireField(record, "codeId");
		requireField(record, "txtName");
		requireField(record, "valFreq");
		double lat=getLatitude(record, "geoLat");
		double lon=getLongitude(record, "geoLong");
		
		if (!isInRange(lon, lat))
			return;
		
		String id=getFieldValue(record, "codeId");
		String name=getFieldValue(record, "txtName");
		String frequency=getFieldValue(record, "valFreq");
		Position pos=new Position(lon,lat,0.0);
		
		// TODO: determine range and variation
		
		namedFixDB.addFix(new VOR(id,pos,name,parseDouble(frequency)*Units.MHz,100.0,0.0));
	}

}
