package de.knewcleus.fgfs.navaids;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public abstract class AbstractDBParser {
	protected final double north;
	protected final double west;
	protected final double south;
	protected final double east;

	public AbstractDBParser(double north, double west, double south, double east)
	{
		this.north=north;
		this.west=west;
		this.south=south;
		this.east=east;
	}

	public void readCompressed(InputStream inputStream) throws DBParserException {
		GZIPInputStream gzipInputStream;
		try {
			gzipInputStream = new GZIPInputStream(inputStream);
			read(gzipInputStream);
			gzipInputStream.close();
		} catch (IOException e) {
			throw new DBParserException(e);
		}
	}
	
	public abstract void read(InputStream inputStream) throws DBParserException;

	protected boolean isInRange(double lon, double lat) {
		return (west<=lon && lon<=east) && (south<=lat && lat<=north);
	}

}