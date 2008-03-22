package de.knewcleus.radar.aircraft;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.radar.ui.aircraft.IAircraftStateConsumer;

public class TargetManager implements IRadarDataConsumer {
	protected final static Logger logger=Logger.getLogger(TargetManager.class.getName());
	protected final IRadarDataProvider radarDataProvider;
	protected final Map<Object, Target> aircraftStateMap=new HashMap<Object, Target>();
	protected final Set<IAircraftStateConsumer> aircraftStateConsumers=new HashSet<IAircraftStateConsumer>();
	protected Target selectedTarget=null;
	protected final int maximumPositionBufferLength;
	
	public TargetManager(IRadarDataProvider radarDataProvider) {
		this.radarDataProvider=radarDataProvider;

		/*
		 * Note that we only register here. A radar data provider does not provide information
		 * about the current set of acquired targets. Instead a set of radar target information updates
		 * is sent every update cycle. With that information we also get the list of known targets.
		 */
		radarDataProvider.registerRadarDataConsumer(this);
		
		/* We record position data up to 15 minutes backwards */
		maximumPositionBufferLength=15*60/radarDataProvider.getSecondsBetweenUpdates();
	}
	
	public synchronized void select(Target aircraftState) {
		if (selectedTarget!=null) {
			logger.fine("Deselecting aircraft "+selectedTarget);
			selectedTarget.setSelected(false);
		}
		selectedTarget=aircraftState;
		if (selectedTarget!=null) {
			logger.fine("Selecting aircraft "+selectedTarget);
			selectedTarget.setSelected(true);
		}
	}
	
	public void deselect() {
		select(null);
	}
	
	public Target getSelectedTarget() {
		return selectedTarget;
	}
	
	public int getMaximumPositionBufferLength() {
		return maximumPositionBufferLength;
	}
	
	public int getSecondsBetweenUpdates() {
		return radarDataProvider.getSecondsBetweenUpdates();
	}
	
	public synchronized void registerAircraftStateConsumer(IAircraftStateConsumer consumer) {
		aircraftStateConsumers.add(consumer);
	}
	
	public synchronized void unregisterAircraftStateConsumer(IAircraftStateConsumer consumer) {
		aircraftStateConsumers.remove(consumer);
	}
	
	protected synchronized void fireAircraftStateUpdated(Set<Target> updatedStates) {
		logger.fine("Aircraft states updated:"+updatedStates);
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateUpdate(updatedStates);
		}
	}
	
	protected synchronized void fireAircraftStateLost(Target aircraftState) {
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateLost(aircraftState);
		}
	}
	
	@Override
	public synchronized void radarDataUpdated(Set<RadarTargetInformation> targets) {
		Set<Target> updatedStates=new HashSet<Target>();
		for (RadarTargetInformation target: targets) {
			Object trackIdentifier=target.getTrackIdentifier();
			Target aircraftState;
			
			if (!aircraftStateMap.containsKey(trackIdentifier)) {
				aircraftState=new Target(this);
				aircraftStateMap.put(trackIdentifier,aircraftState);
			} else {
				aircraftState=aircraftStateMap.get(trackIdentifier);
			}
			aircraftState.update(target);
			updatedStates.add(aircraftState);
		}
		fireAircraftStateUpdated(updatedStates);
	}
	
	@Override
	public synchronized void radarTargetLost(Object trackIdentifier) {
		Target aircraftState=aircraftStateMap.get(trackIdentifier);
		assert(aircraftState!=null);
		aircraftStateMap.remove(trackIdentifier);
		fireAircraftStateLost(aircraftState);
	}
	
	public Collection<Target> getAircraftStates() {
		return Collections.unmodifiableCollection(aircraftStateMap.values());
	}
}
