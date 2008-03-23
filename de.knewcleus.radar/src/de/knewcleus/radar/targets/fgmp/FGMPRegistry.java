package de.knewcleus.radar.targets.fgmp;

import java.util.HashSet;
import java.util.Set;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.multiplayer.AbstractPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.radar.aircraft.ICorrelationDatabase;
import de.knewcleus.radar.aircraft.ISquawkAllocator;
import de.knewcleus.radar.aircraft.OutOfSquawksException;
import de.knewcleus.radar.targets.ITrackDataConsumer;
import de.knewcleus.radar.targets.ITargetProvider;
import de.knewcleus.radar.targets.TargetInformation;

public class FGMPRegistry extends AbstractPlayerRegistry<FGMPAircraft> implements ITargetProvider, IUpdateable {
	protected final Set<ITrackDataConsumer> consumers=new HashSet<ITrackDataConsumer>();
	protected final Updater radarUpdater=new Updater(this,1000*getSecondsBetweenUpdates());
	protected final ISquawkAllocator squawkAllocator;
	protected final ICorrelationDatabase correlationDatabase;
	
	public FGMPRegistry(ISquawkAllocator squawkAllocator, ICorrelationDatabase correlationDatabase) {
		this.squawkAllocator=squawkAllocator;
		this.correlationDatabase=correlationDatabase;
		radarUpdater.start();
	}
	
	@Override
	public FGMPAircraft createNewPlayer(PlayerAddress address, String callsign) throws MultiplayerException {
		String squawk;
		try {
			squawk = squawkAllocator.allocateSquawk();
		} catch (OutOfSquawksException e) {
			throw new MultiplayerException(e);
		}
		correlationDatabase.registerSquawk(squawk, callsign);
		FGMPAircraft aircraft=new FGMPAircraft(address,callsign,squawk);
		return aircraft;
	}
	
	@Override
	public int getSecondsBetweenUpdates() {
		return 1;
	}
	
	@Override
	public synchronized void unregisterPlayer(FGMPAircraft expiredPlayer) {
		String squawk=expiredPlayer.getSSRCode();
		correlationDatabase.unregisterSquawk(squawk);
		squawkAllocator.returnSquawk(squawk);
		super.unregisterPlayer(expiredPlayer);
		fireRadarTargetLost(expiredPlayer);
	}
	
	protected void fireRadarDataUpdated(Set<TargetInformation> targets) {
		for (ITrackDataConsumer consumer: consumers) {
			consumer.radarDataUpdated(targets);
		}
	}
	
	protected void fireRadarTargetLost(FGMPAircraft target) {
		for (ITrackDataConsumer consumer: consumers) {
			consumer.radarTargetLost(target);
		}
	}
	
	@Override
	public synchronized void registerTrackDataConsumer(ITrackDataConsumer consumer) {
		consumers.add(consumer);
	}
	
	@Override
	public synchronized void unregisterTrackDataConsumer(ITrackDataConsumer consumer) {
		consumers.remove(consumer);
	}
	
	@Override
	public synchronized void update(double dt) {
		Set<TargetInformation> targets=new HashSet<TargetInformation>();
		for (FGMPAircraft aircraft: getPlayers()) {
			targets.add(new TargetInformation(aircraft,
								aircraft.getLongitude(),aircraft.getLatitude(),
								aircraft.getGroundSpeed(),aircraft.getTrueCourse(),
								aircraft.getSSRMode(),aircraft.getSSRCode(),aircraft.getPressureAltitude()));
		}
		fireRadarDataUpdated(targets);
	}
}
