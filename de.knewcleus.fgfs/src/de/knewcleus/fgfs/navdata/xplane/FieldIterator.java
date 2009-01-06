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
