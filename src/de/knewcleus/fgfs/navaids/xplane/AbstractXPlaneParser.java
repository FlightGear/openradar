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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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