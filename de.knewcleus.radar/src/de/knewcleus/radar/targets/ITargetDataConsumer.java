package de.knewcleus.radar.targets;

import java.util.Set;

public interface ITargetDataConsumer {
	public void targetDataUpdated(Set<TargetInformation> argets);
	public void targetLost(Object trackIdentifier);
}
