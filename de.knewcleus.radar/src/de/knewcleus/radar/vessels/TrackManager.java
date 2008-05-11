package de.knewcleus.radar.vessels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.radar.aircraft.ICorrelationDatabase;

/**
 * The track manager updates the tracks using data from one or more position data providers.
 * 
 * The respective data providers need to be registered using the methods {@link #addPositionDataProvider(IPositionDataProvider)} and
 * {@link #removePositionDataProvider(IPositionDataProvider)}.
 * 
 * @author Ralf Gerlich
 * @see IPositionDataProvider
 *
 */
public class TrackManager implements IPositionUpdateListener {
	private final static Logger logger=Logger.getLogger(TrackManager.class.getName());
	protected final ICorrelationDatabase correlationDatabase;
	protected final Set<IPositionDataProvider> positionDataProviders=new HashSet<IPositionDataProvider>();
	protected final Map<Object, Track> targetMap=new HashMap<Object, Track>();
	protected final Set<ITrackUpdateListener> trackListeners=new HashSet<ITrackUpdateListener>();
	
	public TrackManager(ICorrelationDatabase correlationDatabase) {
		this.correlationDatabase=correlationDatabase;
	}
	
	public ICorrelationDatabase getCorrelationDatabase() {
		return correlationDatabase;
	}
	
	public synchronized void addPositionDataProvider(IPositionDataProvider provider) {
		if (positionDataProviders.add(provider)) {
			provider.registerPositionUpdateListener(this);
		}
	}
	
	public synchronized void removePositionDataProvider(IPositionDataProvider provider) {
		if (positionDataProviders.remove(provider)) {
			provider.unregisterPositionUpdateListener(this);
		}
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
	public synchronized void targetDataUpdated(Set<PositionUpdate> targets) {
		Set<Track> updatedStates=new HashSet<Track>();
		for (PositionUpdate target: targets) {
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
}
