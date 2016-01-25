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
package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XDRInputStream extends DataInputStream implements DataInput {

	public XDRInputStream(InputStream in) {
		super(in);
	}
	
	public final <T extends Enum<T>> T readEnum(Class<T> clazz) throws IOException {
		int i=readInt();
		return clazz.getEnumConstants()[i];
	}
	
	public final void readOpaque(byte b[]) throws IOException {
		readOpaque(b,b.length);
	}
	
	public final void readOpaque(byte b[], int l) throws IOException {
		readFully(b,0,l);
		int pad=(-l)&3;
		skip(pad);
	}
	
	public final void readVarOpaque(byte b[]) throws IOException {
		int l=readInt();
		if (l>b.length)
			throw new IOException("Variable opaque data is too long for buffer");
		readOpaque(b,l);
	}
	
	public final byte[] readVarOpaque() throws IOException {
		int l=readInt();
		byte []b=new byte[l];
		readOpaque(b,l);
		return b;
	}
	
	public String readString() throws IOException {
		byte []b=readVarOpaque();
		return new String(b,"US-ASCII");
	}
	
	public String readString(int m) throws IOException {
		int l=readInt();
		if (l>m)
			throw new IOException("Length of string is longer than limit");
		byte []b=new byte[l];
		readOpaque(b,l);
		return new String(b,"US-ASCII");
	}
}
