package de.knewcleus.fgfs.navaids.ead;

import org.w3c.dom.Element;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.NamedFixDB;

public class AerodromeParser extends AbstractSDOParser {
	protected final NamedFixDB namedFixDB;

	public AerodromeParser(NamedFixDB namedFixDB,
			double north, double west, double south, double east) {
		super(north, west, south, east);
		this.namedFixDB=namedFixDB;
	}

	@Override
	public void processRecord(Element record) throws DBParserException {
		requireField(record, "codeIcao");
		requireField(record, "txtName");
		double lat=getLatitude(record, "geoLat");
		double lon=getLongitude(record, "geoLong");
		
		if (!isInRange(lon, lat))
			return;
		
		String id=getFieldValue(record, "codeIcao");
		String name=getFieldValue(record, "txtName");
		Position pos=new Position(lon,lat,0.0);
		namedFixDB.addFix(new Aerodrome(id, name, pos));
	}

}
