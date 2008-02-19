package de.knewcleus.fgfs.navaids.ead;

import org.w3c.dom.Element;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.DesignatedPoint;
import de.knewcleus.fgfs.navaids.NamedFixDB;

public class DesignatedPointsParser extends AbstractSDOParser {
	protected final NamedFixDB namedFixDB;

	public DesignatedPointsParser(NamedFixDB namedFixDB,
			double north, double west, double south, double east) {
		super(north, west, south, east);
		this.namedFixDB=namedFixDB;
	}

	@Override
	public void processRecord(Element record) throws DBParserException {
		requireField(record, "codeId");
		double lat=getLatitude(record, "geoLat");
		double lon=getLongitude(record, "geoLong");
		
		if (!isInRange(lon, lat))
			return;
		
		String name=getFieldValue(record, "codeId");
		Position pos=new Position(lon,lat,0.0);
		namedFixDB.addFix(new DesignatedPoint(name, pos));
	}

}
