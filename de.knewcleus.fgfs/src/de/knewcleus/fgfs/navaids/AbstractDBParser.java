package de.knewcleus.fgfs.navaids;

import java.awt.Shape;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public abstract class AbstractDBParser {
	protected final Shape geographicBounds;

	public AbstractDBParser(Shape geographicBounds)
	{
		this.geographicBounds=geographicBounds;
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
		return geographicBounds.contains(lon, lat);
	}

}