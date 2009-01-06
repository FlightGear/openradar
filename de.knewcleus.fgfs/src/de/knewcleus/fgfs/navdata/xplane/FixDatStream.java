package de.knewcleus.fgfs.navdata.xplane;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.model.INavDataStream;


/**
 * A <code>FixDatStream</code> is an {@link INavDataStream} reading the
 * <a href="http://www.x-plane.org/home/robinp/Fix600.htm">fix.dat format</a>
 * as defined for X-Plane.
 * 
 * @author Ralf Gerlich
 *
 */
public class FixDatStream implements INavDataStream<IIntersection> {
	protected final BufferedReader bufferedReader;
	
	public FixDatStream(Reader reader) throws IOException {
		this(new BufferedReader(reader));
	}
	
	public FixDatStream(BufferedReader bufferedReader) throws IOException {
		this.bufferedReader = bufferedReader;
		// Skip the line-ending-marker (I/A)
		bufferedReader.readLine();
		// Skip the copyright-line
		bufferedReader.readLine();
	}
	
	@Override
	public IIntersection readDatum() throws NavDataStreamException {
		IIntersection record = null;
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
	
	protected IIntersection parseRecord(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString;
		final String identification;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			identification = fieldIterator.next();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double lat, lon;
		try {
			lat = Double.parseDouble(latString) * Units.DEG;
			lon = Double.parseDouble(lonString) * Units.DEG;
		} catch (NumberFormatException e) {
			throw new NavDataStreamException(e);
		}
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		return new Intersection(geographicPosition, identification);
	}

}
