package de.knewcleus.radar.aircraft;

import java.util.Set;


public interface ITargetUpdateListener {
	public abstract void targetsUpdated(Set<Target> targets);
	public abstract void targetLost(Target target);
}
