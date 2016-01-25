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
package de.knewcleus.fgfs.navdata.xplane;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FieldIterator implements Iterator<String> {
	protected final String line;
	protected final int length;
	protected int startOfField;
	
	public FieldIterator(String line) {
		this.line = line;
		this.length = line.length();
		startOfField = skipWhitespace(0);
	}
	
	public String getLine() {
		return line;
	}
	
	public void reset() {
		startOfField = skipWhitespace(0);
	}
	
	@Override
	public boolean hasNext() {
		return startOfField < length;
	}
	
	@Override
	public String next() throws NoSuchElementException {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final int startOfDelimiter = skipToDelimiter(startOfField);
		final String field = line.substring(startOfField, startOfDelimiter);
		startOfField = skipWhitespace(startOfDelimiter);
		return field;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public String restOfLine() {
		return line.substring(startOfField);
	}
	
	/**
	 * Return the index of the next non-whitespace character.
	 * @param start		The index of the character to start searching at.
	 */
	protected int skipWhitespace(int start) {
		int i;
		for (i=start;i<length;++i) {
			if (!Character.isWhitespace(line.charAt(i))) {
				break;
			}
		}
		return i;
	}

	/**
	 * Return the index of the next whitespace character.
	 * 
	 * @param start		The index to start searching at.
	 */
	protected int skipToDelimiter(int start) {
		int i;
		for (i=start;i<length;++i) {
			if (Character.isWhitespace(line.charAt(i))) {
				break;
			}
		}
		return i;
	}
}
