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
package de.knewcleus.fgfs.navaids.ead;

import java.awt.Shape;

import org.w3c.dom.Element;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Airway;
import de.knewcleus.fgfs.navaids.AirwayDB;
import de.knewcleus.fgfs.navaids.AirwaySegment;
import de.knewcleus.fgfs.navaids.DBParserException;

public class AirwayParser extends AbstractSDOParser {
	protected final AirwayDB airwayDB;

	public AirwayParser(AirwayDB airwayDB, Shape geographicBounds) {
		super(geographicBounds);
		this.airwayDB=airwayDB;
	}

	@Override
	public void processRecord(Element record) throws DBParserException {
		Element routeRecord=getSubrecord(record, "Rte",true);
		Element startPointRecord=getSubrecord(record, "SpnSta",true);
		Element endPointRecord=getSubrecord(record, "SpnEnd",true);
		
		double startLat=getLatitude(startPointRecord, "geoLat");
		double startLon=getLongitude(startPointRecord, "geoLong");
		double endLat=getLatitude(endPointRecord, "geoLat");
		double endLon=getLongitude(endPointRecord, "geoLong");
		
		Position startPointPos=new Position(startLon,startLat,0.0);
		Position endPointPos=new Position(endLon,endLat,0.0);
		
		String startPointName=getFieldValue(startPointRecord, "codeId");
		String endPointName=getFieldValue(endPointRecord,"codeId");
		
		String designator=getFieldValue(routeRecord, "txtDesig");
		
		Airway airway=airwayDB.getOrAddAirway(designator);
		AirwaySegment airwaySegment=new AirwaySegment(startPointName, startPointPos, endPointName, endPointPos);
		airway.addSegment(airwaySegment);
	}

}
