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
