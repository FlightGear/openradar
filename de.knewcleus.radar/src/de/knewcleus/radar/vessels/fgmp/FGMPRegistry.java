package de.knewcleus.radar.vessels.fgmp;

import java.util.HashSet;
import java.util.Set;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.multiplayer.AbstractPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;
import de.knewcleus.radar.aircraft.ICorrelationDatabase;
import de.knewcleus.radar.aircraft.ISquawkAllocator;
import de.knewcleus.radar.aircraft.OutOfSquawksException;
import de.knewcleus.radar.vessels.IPositionDataProvider;
import de.knewcleus.radar.vessels.IPositionUpdateListener;
import de.knewcleus.radar.vessels.PositionUpdate;

public class FGMPRegistry extends AbstractPlayerRegistry<FGMPAircraft> implements IPositionDataProvider, IUpdateable {
	protected final Set<IPositionUpdateListener> listeners=new HashSet<IPositionUpdateListener>();
	protected final static int updateMillis=1000;
	protected final Updater radarUpdater=new Updater(this,updateMillis);
	protected final ISquawkAllocator squawkAllocator;
	protected final ICorrelationDatabase correlationDatabase;
	
	public FGMPRegistry(ISquawkAllocator squawkAllocator, ICorrelationDatabase correlationDatabase) {
		this.squawkAllocator=squawkAllocator;
		this.correlationDatabase=correlationDatabase;
		radarUpdater.start();
	}
	
	@Override
	public FGMPAircraft createNewPlayer(String callsign) throws MultiplayerException {
		String squawk;
		try {
			squawk = squawkAllocator.allocateSquawk();
		} catch (OutOfSquawksException e) {
			throw new MultiplayerException(e);
		}
		correlationDatabase.registerSquawk(squawk, callsign);
		FGMPAircraft aircraft=new FGMPAircraft(callsign,squawk);
		return aircraft;
	}
	
	@Override
	public synchronized void unregisterPlayer(FGMPAircraft expiredPlayer) {
		String squawk=expiredPlayer.getSSRCode();
		correlationDatabase.unregisterSquawk(squawk);
		squawkAllocator.returnSquawk(squawk);
		super.unregisterPlayer(expiredPlayer);
		fireRadarTargetLost(expiredPlayer);
	}
	
	protected void fireRadarDataUpdated(Set<PositionUpdate> targets) {
		for (IPositionUpdateListener consumer: listeners) {
			consumer.targetDataUpdated(targets);
		}
	}
	
	protected void fireRadarTargetLost(FGMPAircraft target) {
		for (IPositionUpdateListener consumer: listeners) {
			consumer.targetLost(target);
		}
	}
	
	@Override
	public synchronized void registerPositionUpdateListener(IPositionUpdateListener consumer) {
		listeners.add(consumer);
	}
	
	@Override
	public synchronized void unregisterPositionUpdateListener(IPositionUpdateListener consumer) {
		listeners.remove(consumer);
	}
	
	@Override
	public synchronized void update(double dt) {
		Set<PositionUpdate> targets=new HashSet<PositionUpdate>();
		for (FGMPAircraft aircraft: getPlayers()) {
			/*
			 * NOTE: We are reporting the time sent in the update packet as timestamp,
			 *       as this is independent of network lag.
			 */
			targets.add(new PositionUpdate(aircraft,
								aircraft.getPositionTime(),
								aircraft.getLongitude(),aircraft.getLatitude(),
								aircraft.getGroundSpeed(),aircraft.getTrueCourse(),
								aircraft.getSSRMode(),aircraft.getSSRCode(),aircraft.getPressureAltitude()));
		}
		fireRadarDataUpdated(targets);
	}
}
