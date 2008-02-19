package de.knewcleus.fgfs.navaids.xplane;


import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.DesignatedPoint;
import de.knewcleus.fgfs.navaids.NamedFixDB;

public class FixParser extends AbstractXPlaneParser {
	protected final NamedFixDB namedFixDB;

	public FixParser(NamedFixDB namedFixDB, double north, double west, double south, double east)
	{
		super(north,west,south,east);
		this.namedFixDB=namedFixDB;
	}
	
	@Override
	protected void processRecord(String line) throws DBParserException {
		String[] tokens=line.split("\\s+",3);
		double lon,lat;
		lat=Double.parseDouble(tokens[0]);
		lon=Double.parseDouble(tokens[1]);
		
		if (!isInRange(lon, lat))
			return;
		
		Position pos=new Position(lon,lat,0.0);
		
		namedFixDB.addFix(new DesignatedPoint(tokens[2], pos));
	}
}
