package de.knewcleus.radar.targets;

import java.util.Set;

public interface ITrackDataConsumer {
	public void radarDataUpdated(Set<TargetInformation> radarTargets);
	public void radarTargetLost(Object trackIdentifier);
}
