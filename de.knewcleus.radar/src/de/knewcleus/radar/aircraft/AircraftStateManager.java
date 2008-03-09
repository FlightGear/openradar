package de.knewcleus.radar.aircraft;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AircraftStateManager implements IRadarDataConsumer<IAircraft> {
	protected final IRadarDataProvider<? extends IAircraft> radarDataProvider;
	protected final Map<IAircraft, AircraftState> aircraftStateMap=new HashMap<IAircraft, AircraftState>();
	protected final Set<IAircraftStateConsumer> aircraftStateConsumers=new HashSet<IAircraftStateConsumer>();
	protected AircraftState selectedAircraft=null;
	protected final int maximumPositionBufferLength;
	
	public AircraftStateManager(IRadarDataProvider<? extends IAircraft> radarDataProvider) {
		this.radarDataProvider=radarDataProvider;
		
		/* Copy all existing targets */
		// FIXME: possible race condition
		radarDataProvider.registerRadarDataConsumer(this);
		for (IAircraft aircraft: radarDataProvider) {
			radarTargetAcquired(aircraft);
		}
		/* We record position data up to 15 minutes backwards */
		maximumPositionBufferLength=15*60/radarDataProvider.getSecondsBetweenUpdates();
	}
	
	public void select(AircraftState aircraftState) {
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
	
	public void registerAircraftStateConsumer(IAircraftStateConsumer consumer) {
		aircraftStateConsumers.add(consumer);
	}
	
	public void unregisterAircraftStateConsumer(IAircraftStateConsumer consumer) {
		aircraftStateConsumers.remove(consumer);
	}
	
	protected void fireAircraftStateAcquired(AircraftState aircraftState) {
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateAcquired(aircraftState);
		}
	}
	
	protected void fireAircraftStateUpdated() {
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateUpdate();
		}
	}
	
	protected void fireAircraftStateLost(AircraftState aircraftState) {
		for (IAircraftStateConsumer consumer: aircraftStateConsumers) {
			consumer.aircraftStateLost(aircraftState);
		}
	}
	
	@Override
	public void radarTargetAcquired(IAircraft aircraft) {
		AircraftState aircraftState=new AircraftState(this, aircraft);
		aircraftStateMap.put(aircraft,aircraftState);
		aircraftState.update();
		fireAircraftStateAcquired(aircraftState);
	}
	
	@Override
	public void radarDataUpdated() {
		for (AircraftState aircraftState: aircraftStateMap.values()) {
			aircraftState.update();
		}
		fireAircraftStateUpdated();
	}
	
	@Override
	public void radarTargetLost(IAircraft aircraft) {
		AircraftState aircraftState=aircraftStateMap.get(aircraft);
		aircraftStateMap.remove(aircraft);
		fireAircraftStateLost(aircraftState);
	}
	
	public Collection<AircraftState> getAircraftStates() {
		return Collections.unmodifiableCollection(aircraftStateMap.values());
	}
}
