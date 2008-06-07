package de.knewcleus.fgfs.navaids.xplane;

import java.awt.Shape;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.knewcleus.fgfs.navaids.AbstractDBParser;
import de.knewcleus.fgfs.navaids.DBParserException;

public abstract class AbstractXPlaneParser extends AbstractDBParser {

	protected AbstractXPlaneParser(Shape geographicBounds) {
		super(geographicBounds);
	}
	
	protected abstract void processRecord(String line) throws DBParserException;
	
	protected void endStream() throws DBParserException {
	}

	@Override
	public void read(InputStream inputStream) throws DBParserException {
		InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
		BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
		
		String line;
		
		try {
			// Skip the line-ending-marker (I/A)
			bufferedReader.readLine();
			// Skip the copyright-line
			bufferedReader.readLine();
			
			while ((line=bufferedReader.readLine())!=null) {
				line=line.trim();
				if (line.length()==0)
					continue; // skip empty lines
				if (line.equals("99"))
					break;
				processRecord(line);
			}
		} catch (IOException e) {
			throw new DBParserException(e);
		}
		endStream();
	}

}