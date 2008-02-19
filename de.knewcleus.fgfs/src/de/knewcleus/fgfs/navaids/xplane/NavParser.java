package de.knewcleus.fgfs.navaids.xplane;

import static de.knewcleus.fgfs.Units.DEG;
import static de.knewcleus.fgfs.Units.FT;
import static de.knewcleus.fgfs.Units.MHZ;
import static de.knewcleus.fgfs.Units.NM;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.NamedFixDB;
import de.knewcleus.fgfs.navaids.VOR;

public class NavParser extends AbstractXPlaneParser {
	protected final NamedFixDB namedFixDB;

	public NavParser(NamedFixDB namedFixDB, double north, double west, double south, double east) {
		super(north, west, south, east);
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
			namedFixDB.addFix(new VOR(id,pos,name,freq*MHZ/100.0,range,parseDouble(tokens[6])*DEG));
			break;
		}
	}
}
