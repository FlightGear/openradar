package de.knewcleus.fgfs.geodata;

import java.io.DataInputStream;
import java.io.IOException;

public class LittleEndianHelper {

	public static short readShort(DataInputStream dataStream) throws IOException {
		return (short)(dataStream.read() | dataStream.read()<<8);
	}

	public static int readInt(DataInputStream dataStream) throws IOException {
		return (dataStream.read() |
				dataStream.read()<<8 |
				dataStream.read()<<16 |
				dataStream.read()<<24);
	}

	public static long readLong(DataInputStream dataStream) throws IOException {
		return ((long)dataStream.read() |
				(long)dataStream.read()<<8 |
				(long)dataStream.read()<<16 |
				(long)dataStream.read()<<24 |
				(long)dataStream.read()<<32 |
				(long)dataStream.read()<<40 |
				(long)dataStream.read()<<48 |
				(long)dataStream.read()<<56);
	}

	public static double readDouble(DataInputStream dataStream) throws IOException {
		return Double.longBitsToDouble(readLong(dataStream));
	}

}
