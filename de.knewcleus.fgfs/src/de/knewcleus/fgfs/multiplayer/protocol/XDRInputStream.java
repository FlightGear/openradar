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
