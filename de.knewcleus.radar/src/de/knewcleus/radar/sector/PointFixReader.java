package de.knewcleus.radar.sector;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.DesignatedPoint;
import de.knewcleus.fgfs.navaids.NamedFixDB;

public class PointFixReader extends PointReader {
	@Override
	protected void processLine(NamedFixDB fixDB, Position pos, String id, String rest) {
		DesignatedPoint designatedPoint=new DesignatedPoint(id,pos);
		fixDB.addFix(designatedPoint);
	}
}
