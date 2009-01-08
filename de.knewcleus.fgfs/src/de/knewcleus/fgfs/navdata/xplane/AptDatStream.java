package de.knewcleus.fgfs.navdata.xplane;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.fgfs.navdata.model.IAerodrome;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavDatum;
import de.knewcleus.fgfs.navdata.model.SurfaceType;

public class AptDatStream implements INavDataStream<INavDatum> {
	protected final BufferedReader bufferedReader;
	protected final Queue<INavDatum> datumQueue = new LinkedList<INavDatum>();
	protected String nextLine = null;
	
	public AptDatStream(Reader reader) throws IOException {
		this(new BufferedReader(reader));
	}
	
	public AptDatStream(BufferedReader bufferedReader) throws IOException {
		this.bufferedReader = bufferedReader;
		// Skip the line-ending-marker (I/A)
		bufferedReader.readLine();
		// Skip the copyright-line
		bufferedReader.readLine();
	}
	
	protected String peekLine() throws NavDataStreamException {
		while (nextLine == null || isEmpty(nextLine)) {
			try {
				nextLine = bufferedReader.readLine();
			} catch (IOException e) {
				throw new NavDataStreamException(e);
			}
			if (nextLine == null) {
				/* End of file */
				return null;
			}
		}
		return nextLine;
	}
	
	protected void consumeLine() {
		nextLine = null;
	}
	
	protected static boolean isEmpty(String line) {
		final int length = line.length();
		for (int i = 0; i < length; ++i) {
			if (!Character.isWhitespace(line.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public INavDatum readDatum() throws NavDataStreamException {
		if (!datumQueue.isEmpty()) {
			return datumQueue.poll();
		}
		final String line = peekLine();
		if (line == null) {
			/* End of file */
			return null;
		}
		final FieldIterator fieldIterator = new FieldIterator(line);
		final String recordTypeString = fieldIterator.next();
		if (recordTypeString.equals("99")) {
			/* End of file */
			return null;
		}
		return parseAirport();
	}
	
	protected INavDatum parseAirport() throws NavDataStreamException {
		final String airportLine = peekLine();
		final FieldIterator fieldIterator = new FieldIterator(airportLine);
		assert(fieldIterator.hasNext());
		final String airportCode = fieldIterator.next();
		final IAerodrome.Type aerodromeType;
		if (airportCode.equals("1")) {
			aerodromeType = IAerodrome.Type.Land;
		} else if (airportCode.equals("16")) {
			aerodromeType = IAerodrome.Type.Sea;
		} else if (airportCode.equals("17")) {
			aerodromeType = IAerodrome.Type.Heliport;
		} else {
			throw new NavDataStreamException("Invalid airport code '"+airportCode+"'");
		}
		final String elevString, identification, name;
		try {
			elevString = fieldIterator.next();
			// skip tower field
			fieldIterator.next();
			// skip default buildings field
			fieldIterator.next();
			identification = fieldIterator.next();
			name = fieldIterator.restOfLine();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException("Missing field in airport line",e);
		}
		
		final float elevation;
		try {
			elevation = Float.parseFloat(elevString) * Units.FT;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException("Elevation is not a number",e);
		}
		consumeLine();
		
		final List<Runway> runways = new ArrayList<Runway>();
		while (peekLine()!=null) {
			final String recordLine = peekLine();
			final FieldIterator recordIterator = new FieldIterator(recordLine);
			assert(recordIterator.hasNext());
			final String recordCode = recordIterator.next();
			if (recordCode.equals("1") || recordCode.equals("16") ||
					recordCode.equals("17") || recordCode.equals("99"))
			{
				/* end of file marker or next airport header */
				break;
			} else if (recordCode.equals("10")) {
				// runway or taxiway
				final Runway runway = parseRunway(recordIterator);
				if (runway!=null) {
					runways.add(runway);
				}
			}
			consumeLine();
		}
		
		double totalWeight = 0.0;
		double cogLonWeight = 0.0;
		double cogLatWeight = 0.0;
		final String[] runwayIDs = new String[runways.size()*2];
		int i=0;
		for (Runway rwy: runways) {
			runwayIDs[i++]=rwy.getDesignation();
			runwayIDs[i++]=rwy.getOppositeEndDesignation();
			
			final Point2D center = rwy.getGeographicCenter();
			final float rwyWeight = rwy.getWidth() * rwy.getLength();
			cogLonWeight += center.getX() * rwyWeight;
			cogLatWeight += center.getY() * rwyWeight;
			totalWeight += rwyWeight;
			
			datumQueue.add(rwy.getEndA());
			datumQueue.add(rwy.getEndB());
		}
		
		final Point2D geographicPosition;
		geographicPosition = new Point2D.Double(
				cogLonWeight / totalWeight,
				cogLatWeight / totalWeight);
		
		final IAerodrome aerodrome = new Aerodrome(
				geographicPosition, elevation,
				identification, name,
				aerodromeType,
				runwayIDs);
		for (Runway rwy: runways) {
			rwy.setAerodrome(aerodrome);
		}
		
		return aerodrome;
	}
	
	protected Runway parseRunway(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, designation, headingString, lengthString;
		final String thresholdString, stopwayString, widthString;
		final String surfaceCodeString;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			designation = fieldIterator.next();
			headingString = fieldIterator.next();
			lengthString = fieldIterator.next();
			thresholdString = fieldIterator.next();
			stopwayString = fieldIterator.next();
			widthString = fieldIterator.next();
			// skip lighting codes
			fieldIterator.next();
			surfaceCodeString = fieldIterator.next();
			// ignore the rest of the line
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException("Missing field in runway definition",e);
		}
		if (designation.charAt(0)=='H' || designation.charAt(0)=='h') {
			// Helipad, ignore for now
			return null;
		} else if (designation.equals("xxx")) {
			// Taxiway => ignore
			return null;
		}
		
		final int thresholdDotIndex = thresholdString.indexOf('.');
		if (thresholdDotIndex==-1) {
			throw new NavDataStreamException("Invalid format for threshold length field '"+thresholdString+"'");
		}
		final int stopwayDotIndex = stopwayString.indexOf('.');
		if (stopwayDotIndex==-1) {
			throw new NavDataStreamException("Invalid format for stopway length field '"+stopwayString+"'");
		}
		
		final double latitude, longitude;
		final float heading;
		final float length, width;
		final float endAthreshold, endBthreshold;
		final float endAstopway, endBstopway;
		final SurfaceType surfaceType;
		try {
			latitude = Double.parseDouble(latString) * Units.DEG;
			longitude = Double.parseDouble(lonString) * Units.DEG;
			heading = Float.parseFloat(headingString) * Units.DEG;
			length = Float.parseFloat(lengthString) * Units.FT;
			width = Float.parseFloat(widthString) * Units.FT;
			endAthreshold = Integer.parseInt(thresholdString.substring(0,thresholdDotIndex)) * Units.FT;
			endBthreshold = Integer.parseInt(thresholdString.substring(thresholdDotIndex+1)) * Units.FT;
			endAstopway = Integer.parseInt(stopwayString.substring(0,stopwayDotIndex)) * Units.FT;
			endBstopway = Integer.parseInt(stopwayString.substring(stopwayDotIndex+1)) * Units.FT;
			surfaceType = getSurfaceType(Integer.parseInt(surfaceCodeString));
		} catch (NumberFormatException e) {
			throw new NavDataStreamException("Non-numeric value in numeric field",e);
		}
		
		final String normalizedDesignation = normalizeDesignation(designation);
		
		final Point2D center = new Point2D.Double( longitude, latitude );
		return new Runway(
				surfaceType,
				length, width,
				center,
				heading,
				normalizedDesignation,
				endAthreshold, endBthreshold,
				endAstopway, endBstopway);
	}
	
	protected static String normalizeDesignation(String designation) {
		String normalizedDesignation = designation;
		int length = designation.length();
		if (normalizedDesignation.charAt(length-1)=='x') {
			normalizedDesignation=normalizedDesignation.substring(0,length-1);
			length--;
		}
		if (length<2 || !Character.isDigit(normalizedDesignation.charAt(1))) {
			normalizedDesignation="0" + normalizedDesignation;
		}
		return normalizedDesignation.toUpperCase();
	}
	
	protected static SurfaceType getSurfaceType(int surfaceCode) throws NavDataStreamException {
		switch (surfaceCode) {
		case 1: case 6: case 10:
			return SurfaceType.ASPHALT;
		case 2: case 7: case 11:
			return SurfaceType.CONCRETE;
		case 3: case 8:
			return SurfaceType.TURF;
		case 4: case 9:
			return SurfaceType.DIRT;
		case 5:
			return SurfaceType.GRAVEL;
		case 12:
			return SurfaceType.LAKEBED;
		case 13:
			return SurfaceType.WATER;
		}
		throw new NavDataStreamException("Unknown surface code "+surfaceCode);
	}

}
