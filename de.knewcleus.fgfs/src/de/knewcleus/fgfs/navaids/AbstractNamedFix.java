package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;

public class AbstractNamedFix implements NamedFix {
	protected final String id;
	protected final Position position;
	
	public AbstractNamedFix(String id, Position position) {
		this.id=id;
		this.position=position;
	}
	
	public String getID() {
		return id;
	}

	public Position getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return id+position.toString();
	}
}
