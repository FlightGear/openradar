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
		
		namedFixDB.addFix(new VOR(id,pos,name,parseDouble(frequency)*Units.MHZ,100.0,0.0));
	}

}
