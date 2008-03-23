package de.knewcleus.radar.targets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class TrackManager implements ITargetDataConsumer {
	private final static Logger logger=Logger.getLogger(TrackManager.class.getName());
	protected final ITargetProvider radarDataProvider;
	protected final Map<Object, Track> targetMap=new HashMap<Object, Track>();
	protected final Set<ITrackUpdateListener> trackListeners=new HashSet<ITrackUpdateListener>();
	protected final int maximumPositionBufferLength;
	
	public TrackManager(ITargetProvider radarDataProvider) {
		this.radarDataProvider=radarDataProvider;

		/*
		 * Note that we only register here. A radar data provider does not provide information
		 * about the current set of acquired targets. Instead a set of radar target information updates
		 * is sent every update cycle. With that information we also get the list of known targets.
		 */
		radarDataProvider.registerTargetDataConsumer(this);
		
		/* We record position data up to 15 minutes backwards */
		maximumPositionBufferLength=15*60/radarDataProvider.getSecondsBetweenUpdates();
	}
	
	public int getMaximumPositionBufferLength() {
		return maximumPositionBufferLength;
	}
	
	public int getSecondsBetweenUpdates() {
		return radarDataProvider.getSecondsBetweenUpdates();
	}
	
	public synchronized void registerTrackUpdateListener(ITrackUpdateListener consumer) {
		trackListeners.add(consumer);
	}
	
	public synchronized void unregisterTrackUpdateListener(ITrackUpdateListener consumer) {
		trackListeners.remove(consumer);
	}
	
	protected synchronized void fireTracksUpdated(Set<Track> updatedTracks) {
		logger.fine("Tracks updated:"+updatedTracks);
		for (ITrackUpdateListener listener: trackListeners) {
			listener.tracksUpdated(updatedTracks);
		}
	}
	
	protected synchronized void fireTrackLost(Track track) {
		for (ITrackUpdateListener listener: trackListeners) {
			listener.trackLost(track);
		}
	}
	
	@Override
	public synchronized void targetDataUpdated(Set<TargetInformation> targets) {
		Set<Track> updatedStates=new HashSet<Track>();
		for (TargetInformation target: targets) {
			Object trackIdentifier=target.getTrackIdentifier();
			Track aircraftState;
			
			if (!targetMap.containsKey(trackIdentifier)) {
				aircraftState=new Track(this);
				targetMap.put(trackIdentifier,aircraftState);
			} else {
				aircraftState=targetMap.get(trackIdentifier);
			}
			aircraftState.update(target);
			updatedStates.add(aircraftState);
		}
		fireTracksUpdated(updatedStates);
	}
	
	@Override
	public synchronized void targetLost(Object trackIdentifier) {
		Track aircraftState=targetMap.get(trackIdentifier);
		assert(aircraftState!=null);
		targetMap.remove(trackIdentifier);
		fireTrackLost(aircraftState);
	}
	
	public Collection<Track> getAircraftStates() {
		return Collections.unmodifiableCollection(targetMap.values());
	}
}
