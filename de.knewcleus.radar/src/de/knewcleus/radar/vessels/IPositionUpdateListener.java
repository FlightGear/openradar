package de.knewcleus.radar.vessels;

import java.util.EventListener;
import java.util.Set;

public interface IPositionUpdateListener extends EventListener {
	public void targetDataUpdated(Set<PositionUpdate> targets);
	public void targetLost(Object trackIdentifier);
}
