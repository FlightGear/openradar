package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;


public interface NamedFix {
	public String getID();
	public Position getPosition();
}
