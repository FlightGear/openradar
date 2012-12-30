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
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeoUtil;
import de.knewcleus.fgfs.location.GeoUtil.GeoUtilInfo;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.fgfs.navdata.model.IAerodrome;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.model.SurfaceType;


/**
 * This class is an attempt to read the new XPlane format 1000 to get 
 * most recent information. It is unfinished, because important data 
 * are missing there... 
 * Bases on AptDatStream...
 * 
 * @author Wolfram Wagner
 *
 */
public class AptDatStream1000 implements INavDataStream<INavPoint> {
	protected final BufferedReader bufferedReader;
	protected final Queue<INavPoint> datumQueue = new LinkedList<INavPoint>();
	protected String nextLine = null;
	protected final static GeodesicUtils geodesicUtils = new GeodesicUtils(Ellipsoid.WGS84);
	
	public AptDatStream1000(Reader reader) throws IOException {
		this(new BufferedReader(reader));
	}
	
	public AptDatStream1000(BufferedReader bufferedReader) throws IOException {
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
	public INavPoint readDatum() throws NavDataStreamException {
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
	
	protected INavPoint parseAirport() throws NavDataStreamException {
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

		Point2D towerPosition = null;
		
		final List<LandingSurface> landingSurfaces = new ArrayList<LandingSurface>();
        final List<RawFrequency> frequencies = new ArrayList<RawFrequency>();
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
			} else if (recordCode.equals("100") ) {
				// runway
				final LandingSurface surface = parseRunway(recordIterator);
				if (surface!=null) {
					landingSurfaces.add(surface);
				}
			} else if (recordCode.equals("101")) {
                // water runway
                final LandingSurface surface = parseWaterRunway(recordIterator);
                if (surface!=null) {
                    landingSurfaces.add(surface);
                }
            } else if (recordCode.equals("102")) {
                // helipad
                final LandingSurface surface = parseHelipad(recordIterator);
                if (surface!=null) {
                    landingSurfaces.add(surface);
                }
            } else if (recordCode.equals("14")) {
                // tower position
                double towerLat = Double.parseDouble(recordIterator.next()) * Units.DEG;
                double towerLon = Double.parseDouble(recordIterator.next()) * Units.DEG;

                towerPosition = new Point2D.Double(towerLon,towerLat);
			} else if (/*recordCode.equals("50") || recordCode.equals("51") ||*/ recordCode.equals("53") || recordCode.equals("54") || recordCode.equals("55")) {
                // Radio frequencies
                final RawFrequency f = parseFrequency(recordIterator);
                if (f!=null) {
                    frequencies.add(f);
                }
            }
			consumeLine();
		}
		
		
			
		double totalWeight = 0.0;
		double cogLonWeight = 0.0;
		double cogLatWeight = 0.0;
		/* Calculate the center of gravity of the runway system as ARP */
		for (LandingSurface rwy: landingSurfaces) {
			final Point2D center = rwy.getGeographicCenter();
			final float rwyWeight = rwy.getWidth() * rwy.getLength();
			cogLonWeight += center.getX() * rwyWeight;
			cogLatWeight += center.getY() * rwyWeight;
			totalWeight += rwyWeight;
		}
		
		final Point2D geographicPosition;
		geographicPosition = new Point2D.Double(
				cogLonWeight / totalWeight,
				cogLatWeight / totalWeight);
		
		final IAerodrome aerodrome = new Aerodrome(
				geographicPosition, 
				towerPosition, 
				elevation,
				identification, 
				name, 
				aerodromeType);
		for (LandingSurface surface: landingSurfaces) {
			surface.setAerodrome(aerodrome);
			
			if (surface instanceof Runway) {
				datumQueue.add(((Runway) surface).getEndA());
				datumQueue.add(((Runway) surface).getEndB());
			} else if (surface instanceof Helipad) {
				datumQueue.add(surface);
			}
		}
		
		aerodrome.setFrequencies(frequencies);
		
		return aerodrome;
	}
	
	private RawFrequency parseFrequency(FieldIterator recordIterator) throws NavDataStreamException {
	    
	    final RawFrequency freq;
        try {
            String f = recordIterator.next();
            StringBuilder code = new StringBuilder();
            while(recordIterator.hasNext()) {
                  code.append(recordIterator.next());
                  code.append(" ");
            }
            
            // todo add type 53 GND 54 TWR 55 APP
            
            freq = new RawFrequency(code.toString().trim(),f);
        } catch (NoSuchElementException e) {
            throw new NavDataStreamException("Missing field in runway definition",e);
        }
        return freq;
    }

    protected LandingSurface parseRunway(FieldIterator fieldIterator) throws NavDataStreamException {
		final String widthString, surfaceCodeString;
		final String latStringA, lonStringA, runwayEndNumberA, thresholdStringA;
		final String latStringB, lonStringB, runwayEndNumberB, thresholdStringB;

		try {
            widthString = fieldIterator.next();
            surfaceCodeString = fieldIterator.next();
            fieldIterator.next(); // shoulder type
            fieldIterator.next(); // runway smoothness
            fieldIterator.next(); // runway edge lightning
            fieldIterator.next(); // runway center lightning
            fieldIterator.next(); // runway auto signs
            // runway end A
            runwayEndNumberA = fieldIterator.next();
			latStringA = fieldIterator.next();
			lonStringA = fieldIterator.next();
            thresholdStringA = fieldIterator.next();
            fieldIterator.next(); // runway over run
            fieldIterator.next(); // runway markings
            fieldIterator.next(); // runway lighting
            fieldIterator.next(); // runway touchdown zone
            fieldIterator.next(); // runway end identifier
            // runway end B
            runwayEndNumberB = fieldIterator.next();
            latStringB = fieldIterator.next();
            lonStringB = fieldIterator.next();
            thresholdStringB = fieldIterator.next();
            fieldIterator.next(); // runway over run
            fieldIterator.next(); // runway markings
            fieldIterator.next(); // runway lighting
            fieldIterator.next(); // runway touchdown zone
            fieldIterator.next(); // runway end identifier
            
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException("Missing field in runway definition",e);
		}
		
		final double latitudeA, longitudeA, latitudeB, longitudeB;
		final float heading;
		final float length, width;
		final SurfaceType surfaceType;
		try {
			latitudeA = Double.parseDouble(latStringA) * Units.DEG;
			longitudeA = Double.parseDouble(lonStringA) * Units.DEG;
            latitudeB = Double.parseDouble(latStringB) * Units.DEG;
            longitudeB = Double.parseDouble(lonStringB) * Units.DEG;
			surfaceType = getSurfaceType(Integer.parseInt(surfaceCodeString));
		} catch (NumberFormatException e) {
			throw new NavDataStreamException("Non-numeric value in numeric field",e);
		}

		GeoUtilInfo result = GeoUtil.getDistance(longitudeA, latitudeA, longitudeB, latitudeB); 
        heading = (float)result.angle;
        length = (float)result.length;
        width = Float.parseFloat(widthString) * Units.M;
        
        final Point2D rwACenter = new Point2D.Double( longitudeA,latitudeA);
        final Point2D rwBCenter = new Point2D.Double( longitudeB,latitudeB);
		final Point2D center = new Point2D.Double( longitudeA+(longitudeB-longitudeA)/2,latitudeA+(latitudeB-latitudeA)/2);
		
		final float endAthreshold, endBthreshold;
		try {
			endAthreshold = Float.parseFloat(thresholdStringA) * Units.M;
			endBthreshold = Float.parseFloat(thresholdStringB) * Units.M;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException("Non-numeric value in numeric field",e);
		}

		return new Runway(
				surfaceType,
				length, width,
				center, rwACenter, rwBCenter, heading,
				runwayEndNumberA,runwayEndNumberB,
				endAthreshold, endBthreshold);
	}
	
    protected LandingSurface parseWaterRunway(FieldIterator fieldIterator) throws NavDataStreamException {
        final String widthString;
        final String latStringA, lonStringA, runwayEndNumberA;
        final String latStringB, lonStringB, runwayEndNumberB;

        try {
            widthString = fieldIterator.next();
            fieldIterator.next(); // flag for buoys
            // runway end A
            runwayEndNumberA = fieldIterator.next();
            latStringA = fieldIterator.next();
            lonStringA = fieldIterator.next();

            // runway end B
            runwayEndNumberB = fieldIterator.next();
            latStringB = fieldIterator.next();
            lonStringB = fieldIterator.next();
        } catch (NoSuchElementException e) {
            throw new NavDataStreamException("Missing field in runway definition",e);
        }
        
        final double latitudeA, longitudeA, latitudeB, longitudeB;
        final float heading;
        final float length, width;
        final SurfaceType surfaceType = SurfaceType.WATER;
        try {
            latitudeA = Double.parseDouble(latStringA) * Units.DEG;
            longitudeA = Double.parseDouble(lonStringA) * Units.DEG;
            latitudeB = Double.parseDouble(latStringB) * Units.DEG;
            longitudeB = Double.parseDouble(lonStringB) * Units.DEG;
        } catch (NumberFormatException e) {
            throw new NavDataStreamException("Non-numeric value in numeric field",e);
        }
        GeoUtilInfo result = GeoUtil.getDistance(longitudeA, latitudeA, longitudeB, latitudeB); 
        heading = (float)result.angle;
        length = (float)result.length;
        width = Float.parseFloat(widthString) * Units.M;

        final Point2D rwACenter = new Point2D.Double( longitudeA,latitudeA);
        final Point2D rwBCenter = new Point2D.Double( longitudeB,latitudeB);
        final Point2D center = new Point2D.Double( longitudeA+(longitudeB-longitudeA)/2,latitudeA+(latitudeB-latitudeA)/2);
        
        return new Runway(
                surfaceType,
                length, width,
                center, rwACenter, rwBCenter, heading,
                runwayEndNumberA,runwayEndNumberB,
                0, 0);
    }

    protected LandingSurface parseHelipad(FieldIterator fieldIterator) throws NavDataStreamException {
    
        final String designation, latString, lonString, headingString, lengthString, widthString, surfaceCodeString;
    
        try {
            designation = fieldIterator.next();
            latString = fieldIterator.next();
            lonString = fieldIterator.next();
            headingString = fieldIterator.next();
            lengthString = fieldIterator.next();
            widthString = fieldIterator.next();
            surfaceCodeString = fieldIterator.next();
            fieldIterator.next(); // markings
            fieldIterator.next(); // shoulder type
            fieldIterator.next(); // helipad smoothness
            fieldIterator.next(); // runway edge lightning
            
        } catch (NoSuchElementException e) {
            throw new NavDataStreamException("Missing field in runway definition",e);
        }
        
        final double latitude, longitude;
        final float heading;
        final float length, width;
        final SurfaceType surfaceType;
        try {
            latitude = Double.parseDouble(latString) * Units.DEG;
            longitude = Double.parseDouble(lonString) * Units.DEG;
            surfaceType = getSurfaceType(Integer.parseInt(surfaceCodeString));
            heading = Float.parseFloat(headingString);
            length = Float.parseFloat(lengthString) * Units.M;;
            width = Float.parseFloat(widthString) * Units.M;
        } catch (NumberFormatException e) {
            throw new NavDataStreamException("Non-numeric value in numeric field",e);
        }
        final Point2D center = new Point2D.Double( longitude,latitude);
        
        return new Helipad(
                surfaceType,
                length, width,
                center, heading,
                designation);

    }
    
    protected static String normalizeDesignation(String designation) {
        String normalizedDesignation = designation;
        int length = designation.length();
        if (normalizedDesignation.charAt(length-1)=='x') {
            normalizedDesignation=normalizedDesignation.substring(0,length-1);
            length--;
        }
        if (normalizedDesignation.charAt(0)!='H' && normalizedDesignation.charAt(0)!='h') {
            if (length<2 || !Character.isDigit(normalizedDesignation.charAt(1))) {
                normalizedDesignation="0" + normalizedDesignation;
            }
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
        case 14:
            return SurfaceType.SNOW_ICE;
        case 15:
            return SurfaceType.TRANSPARENT;
		}		
		throw new NavDataStreamException("Unknown surface code "+surfaceCode);
	}

}
