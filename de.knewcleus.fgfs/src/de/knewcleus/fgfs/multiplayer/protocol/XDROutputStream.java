package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class XDROutputStream extends DataOutputStream {
	public XDROutputStream(OutputStream out) {
		super(out);
	}
}
