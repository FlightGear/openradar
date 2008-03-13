package de.knewcleus.radar.aircraft;

import java.util.Set;

public interface IRadarDataConsumer {
	public void radarDataUpdated(Set<RadarTargetInformation> radarTargets);
	public void radarTargetLost(Object trackIdentifier);
}
