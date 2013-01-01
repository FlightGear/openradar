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
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.impl.AirwaySegment;
import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment.Category;

public class AwyDatStream implements INavDataStream<IAirwaySegment> {
	protected final BufferedReader bufferedReader;
	protected final Queue<IAirwaySegment> segmentQueue = new LinkedList<IAirwaySegment>();
	
	public AwyDatStream(Reader reader) throws IOException {
		this(new BufferedReader(reader));
	}
	
	public AwyDatStream(BufferedReader bufferedReader) throws IOException {
		this.bufferedReader = bufferedReader;
		// Skip the line-ending-marker (I/A)
		bufferedReader.readLine();
		// Skip the copyright-line
		bufferedReader.readLine();
	}
	
	@Override
	public IAirwaySegment readDatum() throws NavDataStreamException {
		if (!segmentQueue.isEmpty()) {
			return segmentQueue.poll();
		}
		IAirwaySegment record = null;
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
			final String firstField = fieldIterator.next();
			if (firstField.equals("99")) {
				/* End-of-file marker */
				return null;
			}
			record = parseRecord(new FieldIterator(line));
		} while (record==null);
		return record;
	}
	
	protected IAirwaySegment parseRecord(FieldIterator fieldIterator) throws NavDataStreamException {
		final String startID, startLatString, startLonString;
		final String endID, endLatString, endLonString;
		final String categoryString;
		final String baseAltString, topAltString;
		final String identification;
		try {
			startID = fieldIterator.next();
			startLatString = fieldIterator.next();
			startLonString = fieldIterator.next();
			endID = fieldIterator.next();
			endLatString = fieldIterator.next();
			endLonString = fieldIterator.next();
			categoryString = fieldIterator.next();
			baseAltString = fieldIterator.next();
			topAltString = fieldIterator.next();
			identification = fieldIterator.next();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double startLat, startLon, endLat, endLon;
		final float baseAlt, topAlt;
		try {
			startLat = Double.parseDouble(startLatString) * Units.DEG;
			startLon = Double.parseDouble(startLonString) * Units.DEG;
			endLat = Double.parseDouble(endLatString) * Units.DEG;
			endLon = Double.parseDouble(endLonString) * Units.DEG;
			baseAlt = Float.parseFloat(baseAltString) * 100.0f * Units.FT;
			topAlt = Float.parseFloat(topAltString) * 100.0f * Units.FT;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final IIntersection start = new Intersection(new Point2D.Double(startLon, startLat), startID);
		final IIntersection end = new Intersection(new Point2D.Double(endLon, endLat), endID);
		final Category category;
		if (categoryString.equals("1")) {
			category = Category.Low;
		} else if (categoryString.equals("2")) {
			category = Category.High;
		} else {
			throw new NavDataStreamException("Unknown airway category '"+categoryString+"'");
		}
		final String[] identifications = identification.split("-");
		for (String id: identifications) {
			segmentQueue.add(new AirwaySegment(id, category, start, end, false, topAlt, baseAlt));
		}
		return segmentQueue.poll();
	}

}
