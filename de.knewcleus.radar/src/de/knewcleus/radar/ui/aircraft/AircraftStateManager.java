package de.knewcleus.radar.ui.aircraft;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.knewcleus.radar.aircraft.IRadarDataConsumer;
import de.knewcleus.radar.aircraft.IRadarDataProvider;
import de.knewcleus.radar.aircraft.RadarTargetInformation;

public class AircraftStateManager implements IRadarDataConsumer {
	protected final IRadarDataProvider radarDataProvider;
	protected final Map<Object, AircraftState> aircraftStateMap=new HashMap<Object, AircraftState>();
	protected final Set<IAircraftStateConsumer> aircraftStateConsumers=new HashSet<IAircraftStateConsumer>();
	protected AircraftState selectedAircraft=null;
	protected final int maximumPositionBufferLength;
	
	public AircraftStateManager(IRadarDataProvider radarDataProvider) {
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
	
	public synchronized void select(AircraftState aircraftState) {
		if (selectedAircraft!=null)
			selectedAircraft.setSelected(false);
		selectedAircraft=aircraftState;
		if (selectedAircraft!=null)
			selectedAircraft.setSelected(true);
	}
	
	public void deselect() {
		select(null);
	}
	
	public AircraftState getSelectedAircraft() {
		return selectedAircraft;
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
	
	protected synchronized void fireAircraftStateAcquired(AircraftState aircraftState) {
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateAcquired(aircraftState);
		}
	}
	
	protected synchronized void fireAircraftStateUpdated() {
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateUpdate();
		}
	}
	
	protected synchronized void fireAircraftStateLost(AircraftState aircraftState) {
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateLost(aircraftState);
		}
	}
	
	@Override
	public synchronized void radarDataUpdated(Set<RadarTargetInformation> targets) {
		for (RadarTargetInformation target: targets) {
			Object trackIdentifier=target.getTrackIdentifier();
			AircraftState aircraftState;
			
			if (!aircraftStateMap.containsKey(trackIdentifier)) {
				aircraftState=new AircraftState(this);
				aircraftStateMap.put(trackIdentifier,aircraftState);
				aircraftState.update(target);
				fireAircraftStateAcquired(aircraftState);
			} else {
				aircraftState=aircraftStateMap.get(trackIdentifier);
				aircraftState.update(target);
			}
		}
		fireAircraftStateUpdated();
	}
	
	@Override
	public synchronized void radarTargetLost(Object trackIdentifier) {
		AircraftState aircraftState=aircraftStateMap.get(trackIdentifier);
		assert(aircraftState!=null);
		aircraftStateMap.remove(trackIdentifier);
		fireAircraftStateLost(aircraftState);
	}
	
	public Collection<AircraftState> getAircraftStates() {
		return Collections.unmodifiableCollection(aircraftStateMap.values());
	}
}
