package de.knewcleus.radar.targets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class TargetManager implements ITrackDataConsumer {
	private final static Logger logger=Logger.getLogger(TargetManager.class.getName());
	protected final ITargetProvider radarDataProvider;
	protected final Map<Object, Target> targetMap=new HashMap<Object, Target>();
	protected final Set<ITargetUpdateListener> targetListeners=new HashSet<ITargetUpdateListener>();
	protected final int maximumPositionBufferLength;
	
	public TargetManager(ITargetProvider radarDataProvider) {
		this.radarDataProvider=radarDataProvider;

		/*
		 * Note that we only register here. A radar data provider does not provide information
		 * about the current set of acquired targets. Instead a set of radar target information updates
		 * is sent every update cycle. With that information we also get the list of known targets.
		 */
		radarDataProvider.registerTrackDataConsumer(this);
		
		/* We record position data up to 15 minutes backwards */
		maximumPositionBufferLength=15*60/radarDataProvider.getSecondsBetweenUpdates();
	}
	
	public int getMaximumPositionBufferLength() {
		return maximumPositionBufferLength;
	}
	
	public int getSecondsBetweenUpdates() {
		return radarDataProvider.getSecondsBetweenUpdates();
	}
	
	public synchronized void registerTargetListener(ITargetUpdateListener consumer) {
		targetListeners.add(consumer);
	}
	
	public synchronized void unregisterTargetListener(ITargetUpdateListener consumer) {
		targetListeners.remove(consumer);
	}
	
	protected synchronized void fireTargetsUpdated(Set<Target> updatedTargets) {
		logger.fine("Targets updated:"+updatedTargets);
		for (ITargetUpdateListener consumer: targetListeners) {
			consumer.targetsUpdated(updatedTargets);
		}
	}
	
	protected synchronized void fireTargetLost(Target target) {
		for (ITargetUpdateListener consumer: targetListeners) {
			consumer.targetLost(target);
		}
	}
	
	@Override
	public synchronized void radarDataUpdated(Set<TargetInformation> targets) {
		Set<Target> updatedStates=new HashSet<Target>();
		for (TargetInformation target: targets) {
			Object trackIdentifier=target.getTrackIdentifier();
			Target aircraftState;
			
			if (!targetMap.containsKey(trackIdentifier)) {
				aircraftState=new Target(this);
				targetMap.put(trackIdentifier,aircraftState);
			} else {
				aircraftState=targetMap.get(trackIdentifier);
			}
			aircraftState.update(target);
			updatedStates.add(aircraftState);
		}
		fireTargetsUpdated(updatedStates);
	}
	
	@Override
	public synchronized void radarTargetLost(Object trackIdentifier) {
		Target aircraftState=targetMap.get(trackIdentifier);
		assert(aircraftState!=null);
		targetMap.remove(trackIdentifier);
		fireTargetLost(aircraftState);
	}
	
	public Collection<Target> getAircraftStates() {
		return Collections.unmodifiableCollection(targetMap.values());
	}
}
