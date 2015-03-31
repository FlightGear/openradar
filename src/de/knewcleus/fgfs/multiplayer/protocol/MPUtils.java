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
package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.IOException;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;

public class MPUtils {
	public static String readCString(XDRInputStream inputStream, int bufferlen) throws IOException {
		byte[] b=new byte[bufferlen];
		inputStream.readFully(b);
		int len;
		for (len=0;len<bufferlen;len++) {
			if (b[len]==0)
				break;
		}
		return new String(b,0,len,"US-ASCII");
	}
	
	public static void writeCString(XDROutputStream outputStream, String str, int bufferlen) throws IOException {
		byte[] b=str.getBytes("US-ASCII");
		int len=Math.min(b.length,bufferlen-1);
		outputStream.write(b, 0, len);
		while (len<bufferlen) {
			outputStream.writeByte(0);
			len++;
		}
	}
	
	public static Position readDoublePosition(XDRInputStream inputStream) throws IOException {
		double x,y,z;
		
		x=inputStream.readDouble()*Units.M;
		y=inputStream.readDouble()*Units.M;
		z=inputStream.readDouble()*Units.M;
		
		return new Position(x,y,z);
	}
	
	public static Position readFloatPosition(XDRInputStream inputStream) throws IOException {
		double x,y,z;
		
		x=inputStream.readFloat()*Units.M;
		y=inputStream.readFloat()*Units.M;
		z=inputStream.readFloat()*Units.M;
		
		return new Position(x,y,z);
	}
	
	public static void writeDoublePosition(XDROutputStream outputStream, Position pos) throws IOException {
		outputStream.writeDouble(pos.getX()/Units.M);
		outputStream.writeDouble(pos.getY()/Units.M);
		outputStream.writeDouble(pos.getZ()/Units.M);
	}
	
	public static void writeFloatPosition(XDROutputStream outputStream, Position pos) throws IOException {
		outputStream.writeFloat((float)(pos.getX()/Units.M));
		outputStream.writeFloat((float)(pos.getY()/Units.M));
		outputStream.writeFloat((float)(pos.getZ()/Units.M));
	}
}
