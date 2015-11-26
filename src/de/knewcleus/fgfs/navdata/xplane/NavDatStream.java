/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navdata.xplane;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.impl.DME;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.fgfs.navdata.impl.Localizer;
import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.fgfs.navdata.impl.NDBFrequency;
import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.fgfs.navdata.impl.VORFrequency;
import de.knewcleus.fgfs.navdata.model.IDME;
import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.IGlideslope;
import de.knewcleus.fgfs.navdata.model.ILocalizer;
import de.knewcleus.fgfs.navdata.model.IMarkerBeacon;
import de.knewcleus.fgfs.navdata.model.INDB;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.model.IVOR;

/**
 * A <code>NavDatStream</code> is an {@link INavDataStream} reading the
 * <a href="http://www.x-plane.org/home/robinp/Nav810.htm">nav.dat format</a>
 * as defined for X-Plane.
 * 
 * @author Ralf Gerlich
 *
 */
public class NavDatStream implements INavDataStream<INavPoint> {
	protected final BufferedReader bufferedReader;
	
	public NavDatStream(Reader reader) throws IOException {
		this(new BufferedReader(reader));
	}
	
	public NavDatStream(BufferedReader bufferedReader) throws IOException {
		this.bufferedReader = bufferedReader;
		// Skip the line-ending-marker (I/A)
		bufferedReader.readLine();
		// Skip the copyright-line
		bufferedReader.readLine();
	}
	
	@Override
	public INavPoint readDatum() throws NavDataStreamException {
		INavPoint record = null;
		do {
			String line;
			try {
				line = bufferedReader.readLine();
			} catch (IOException e1) {
				throw new NavDataStreamException(e1);
			}
			if (line == null) {
				/* End of file reached */
				return null;
			}
			final FieldIterator fieldIterator = new FieldIterator(line);
			if (!fieldIterator.hasNext()) {
				/* Skip empty records */
				continue;
			}
			final String recordTypeString = fieldIterator.next();
			final int recordType;
			try {
				recordType = Integer.parseInt(recordTypeString);
			} catch (NumberFormatException e) {
				throw new NavDataStreamException("Record type must be a number:'"+recordTypeString+"'", e);
			}
			if (recordType == 99) {
				/* End-of-file marker */
				return null;
			}
			record = parseRecord(recordType, fieldIterator);
		} while (record==null);
		return record;
	}
	
	protected INavPoint parseRecord(int recordType, FieldIterator fieldIterator)
		throws NavDataStreamException
	{
		switch (recordType) {
		case 2:
			return parseNDB(fieldIterator);
		case 3:
			return parseVOR(fieldIterator);
		case 4:
		case 5:
			return parseLocalizer(fieldIterator);
		case 6:
			return parseGlideslope(fieldIterator);
		case 7:
			return parseMarkerBeacon(IMarkerBeacon.Type.Outer, fieldIterator);
		case 8:
			return parseMarkerBeacon(IMarkerBeacon.Type.Middle, fieldIterator);
		case 9:
			return parseMarkerBeacon(IMarkerBeacon.Type.Inner, fieldIterator);
		case 12:
		case 13:
			return parseDME(fieldIterator);
		default:
			return null;
		}
	}
	
	protected INDB parseNDB(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, elevString, freqString, rangeString;
		final String identification, name;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			elevString = fieldIterator.next();
			freqString = fieldIterator.next();
			rangeString = fieldIterator.next();
			// skip multi-purpose field
			fieldIterator.next();
			identification = fieldIterator.next();
			name = fieldIterator.restOfLine();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double lat, lon;
		final float elev, range;
		final float freqKHz;
		try {
			lat = Double.parseDouble(latString) * Units.DEG;
			lon = Double.parseDouble(lonString) * Units.DEG;
			elev = Float.parseFloat(elevString)*Units.FT;
			range = Float.parseFloat(rangeString)*Units.NM;
			freqKHz = Float.parseFloat(freqString);
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		final IFrequency frequency = new NDBFrequency(freqKHz);
		return new NDB(geographicPosition, elev, identification, name, frequency, range);
	}
	
	protected IVOR parseVOR(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, elevString, freqString, rangeString, varString;
		final String identification, name;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			elevString = fieldIterator.next();
			freqString = fieldIterator.next();
			rangeString = fieldIterator.next();
			varString = fieldIterator.next();
			identification = fieldIterator.next();
			name = fieldIterator.restOfLine();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double lat, lon;
		final float elev, range, variation;
		final float freqMHz;
		try {
			lat = Double.parseDouble(latString) * Units.DEG;
			lon = Double.parseDouble(lonString) * Units.DEG;
			elev = Float.parseFloat(elevString)*Units.FT;
			range = Float.parseFloat(rangeString)*Units.NM;
			variation = Float.parseFloat(varString)*Units.DEG;
			freqMHz = Float.parseFloat(freqString) / 100.0f;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		final IFrequency frequency = new VORFrequency(freqMHz);
		return new VOR(geographicPosition, elev, identification, name, frequency, range, variation);
	}
	
	protected ILocalizer parseLocalizer(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, elevString, freqString, rangeString, trueHeadingString;
		final String identification, airportID, runwayID;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			elevString = fieldIterator.next();
			freqString = fieldIterator.next();
			rangeString = fieldIterator.next();
			trueHeadingString = fieldIterator.next();
			identification = fieldIterator.next();
			airportID = fieldIterator.next();
			runwayID = fieldIterator.next();
			// skip component type
			fieldIterator.next();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double lat, lon;
		final float elev, range, trueHeading;
		final float freqMHz;
		try {
			lat = Double.parseDouble(latString) * Units.DEG;
			lon = Double.parseDouble(lonString) * Units.DEG;
			elev = Float.parseFloat(elevString)*Units.FT;
			range = Float.parseFloat(rangeString)*Units.NM;
			trueHeading = Float.parseFloat(trueHeadingString)*Units.DEG;
			freqMHz = Float.parseFloat(freqString) / 100.0f;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		final IFrequency frequency = new VORFrequency(freqMHz);
		return new Localizer(geographicPosition, elev, identification, frequency, range, airportID, runwayID, trueHeading);
	}
	
	protected IGlideslope parseGlideslope(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, elevString, freqString, rangeString, glideslopeAngleString;
		final String identification, airportID, runwayID;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			elevString = fieldIterator.next();
			freqString = fieldIterator.next();
			rangeString = fieldIterator.next();
			glideslopeAngleString = fieldIterator.next();
			identification = fieldIterator.next();
			airportID = fieldIterator.next();
			runwayID = fieldIterator.next();
			// skip component type
			fieldIterator.next();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double lat, lon;
		final float elev, range, glideslopeAngle;
		final float freqMHz;
		try {
			lat = Double.parseDouble(latString) * Units.DEG;
			lon = Double.parseDouble(lonString) * Units.DEG;
			elev = Float.parseFloat(elevString)*Units.FT;
			range = Float.parseFloat(rangeString)*Units.NM;
			glideslopeAngle = Integer.parseInt(glideslopeAngleString.substring(0, 3))*0.01f*Units.DEG;
			freqMHz = Float.parseFloat(freqString) / 100.0f;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		final IFrequency frequency = new VORFrequency(freqMHz);
		return new Glideslope(geographicPosition, elev, identification, frequency, range, airportID, runwayID, glideslopeAngle);
	}
	
	protected IMarkerBeacon parseMarkerBeacon(IMarkerBeacon.Type type, FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, elevString;
		final String airportID, runwayID;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			elevString = fieldIterator.next();
			// skip frequency
			fieldIterator.next();
			// skip range
			fieldIterator.next();
			// skip multipurpose field
			fieldIterator.next();
			// skip identification
			fieldIterator.next();
			
			airportID = fieldIterator.next();
			runwayID = fieldIterator.next();
			// skip component type
			fieldIterator.next();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(fieldIterator.getLine(),e);
		}
		final double lat, lon;
		final float elev;
		try {
			lat = Double.parseDouble(latString) * Units.DEG;
			lon = Double.parseDouble(lonString) * Units.DEG;
			elev = Float.parseFloat(elevString)*Units.FT;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		return new MarkerBeacon(geographicPosition, elev, type, airportID, runwayID);
	}
	
	protected IDME parseDME(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, elevString, freqString, rangeString, biasString;
		final String identification, name;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			elevString = fieldIterator.next();
			freqString = fieldIterator.next();
			rangeString = fieldIterator.next();
			biasString = fieldIterator.next();
			identification = fieldIterator.next();
			name = fieldIterator.restOfLine();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double lat, lon;
		final float elev, range, bias;
		final float freqMHz;
		try {
			lat = Double.parseDouble(latString) * Units.DEG;
			lon = Double.parseDouble(lonString) * Units.DEG;
			elev = Float.parseFloat(elevString)*Units.FT;
			range = Float.parseFloat(rangeString)*Units.NM;
			bias = Float.parseFloat(biasString)*Units.NM;
			freqMHz = Float.parseFloat(freqString) / 100.0f;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		final IFrequency frequency = new VORFrequency(freqMHz);
		return new DME(geographicPosition, elev, identification, name, frequency, range, bias);
	}
}
