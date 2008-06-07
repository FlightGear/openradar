package de.knewcleus.fgfs.navaids.xplane;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.NamedFixDB;
import de.knewcleus.fgfs.navaids.Runway;

public class AerodromeParser extends AbstractXPlaneParser {
	protected final NamedFixDB namedFixDB;
	protected String lastID,lastName;
	protected double lastElevation;
	protected double runwayArea;
	protected Vector3D runwayMoment;
	protected List<Runway> runways=new ArrayList<Runway>();

	public AerodromeParser(NamedFixDB namedFixDB, Shape geographicBounds) {
		super(geographicBounds);
		this.namedFixDB=namedFixDB;
	}

	@Override
	protected void processRecord(String line) throws DBParserException {
		String[] tokens=line.split("\\s+",2);
		
		if (tokens[0].equals("1") || tokens[0].equals("16") || tokens[0].equals("17")) {
			processAerodrome(tokens[0],tokens[1].split("\\s+",5));
		}
		
		if (tokens[0].equals("10")) {
			processPavement(tokens[1].split("\\s+",15));
		}
	}
	
	protected void processAerodrome(String code, String tokens[]) {
		if (runwayMoment!=null) {
			runwayMoment=runwayMoment.scale(1.0/runwayArea);
			
			if (isInRange(runwayMoment.getX(), runwayMoment.getY())) {
				runwayMoment=runwayMoment.add(new Vector3D(0,0,lastElevation));
				Position arp=new Position(runwayMoment);
				Aerodrome aerodrome=new Aerodrome(lastID,lastName,arp);
				for (Runway runway: runways)
					aerodrome.addRunway(runway);
				namedFixDB.addFix(aerodrome);
			}
		}
		
		runways.clear();
		lastElevation=Double.parseDouble(tokens[0])*Units.FT;
		lastID=tokens[3];
		lastName=tokens[4];
		runwayMoment=new Position(0,0,0);
		runwayArea=0.0;
	}
	
	protected void processPavement(String tokens[]) {
		if (tokens[2].equals("xxx"))
			return; // skip taxiways
		
		double lat=Double.parseDouble(tokens[0]);
		double lon=Double.parseDouble(tokens[1]);
		double length=Double.parseDouble(tokens[4])*Units.FT;
		double width=Double.parseDouble(tokens[7])*Units.FT;
		double area=length*width;
		
		String designation=tokens[2];
		if (designation.charAt(designation.length()-1)=='x') {
			designation=designation.substring(0, designation.lastIndexOf('x'));
		}
		Position center=new Position(lon,lat,0.0);
		double trueHeading=Double.parseDouble(tokens[3])*Units.DEG;
		
		runwayMoment=runwayMoment.add(new Vector3D(lon*area, lat*area, 0));
		runwayArea+=area;
		
		Runway runway=new Runway(center,designation,trueHeading,length);
		runways.add(runway);
	}
	
	@Override
	protected void endStream() throws DBParserException {
		if (runwayMoment!=null) {
			runwayMoment=runwayMoment.scale(1.0/runwayArea);
			
			if (isInRange(runwayMoment.getX(), runwayMoment.getY())) {
				runwayMoment=runwayMoment.add(new Vector3D(0,0,lastElevation));
				Position arp=new Position(runwayMoment);
				namedFixDB.addFix(new Aerodrome(lastID,lastName,arp));
			}
		}
		super.endStream();
	}

}
