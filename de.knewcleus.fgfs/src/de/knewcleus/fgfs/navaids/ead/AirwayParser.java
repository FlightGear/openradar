package de.knewcleus.fgfs.navaids.ead;

import org.w3c.dom.Element;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Airway;
import de.knewcleus.fgfs.navaids.AirwayDB;
import de.knewcleus.fgfs.navaids.AirwaySegment;
import de.knewcleus.fgfs.navaids.DBParserException;

public class AirwayParser extends AbstractSDOParser {
	protected final AirwayDB airwayDB;

	public AirwayParser(AirwayDB airwayDB, double north, double west,
			double south, double east) {
		super(north, west, south, east);
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
