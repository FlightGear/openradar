package de.knewcleus.fgfs.navaids.xplane;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Airway;
import de.knewcleus.fgfs.navaids.AirwayDB;
import de.knewcleus.fgfs.navaids.AirwaySegment;
import de.knewcleus.fgfs.navaids.DBParserException;

public class AirwayParser extends AbstractXPlaneParser {
	protected final AirwayDB airwayDB;

	public AirwayParser(AirwayDB airwayDB, double north, double west, double south, double east) {
		super(north, west, south, east);
		this.airwayDB=airwayDB;
	}
	
	@Override
	protected void processRecord(String line) throws DBParserException {
		String[] tokens=line.split("\\s+");
		String startName;
		double startLon,startLat;
		
		startName=tokens[0];
		startLat=Double.parseDouble(tokens[1]);
		startLon=Double.parseDouble(tokens[2]);
		
		Position startPos=new Position(startLon, startLat,0);
		
		String endName;
		double endLon,endLat;
		
		endName=tokens[3];
		endLat=Double.parseDouble(tokens[4]);
		endLon=Double.parseDouble(tokens[5]);
		
		Position endPos=new Position(endLon, endLat, 0);

		String awyName=tokens[9];
		
		Airway airway=airwayDB.getOrAddAirway(awyName);
		
		AirwaySegment airwaySegment=new AirwaySegment(startName, startPos, endName, endPos);
		
		airway.addSegment(airwaySegment);
	}
}
