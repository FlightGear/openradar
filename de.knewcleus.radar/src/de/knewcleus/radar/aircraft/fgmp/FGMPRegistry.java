package de.knewcleus.radar.aircraft.fgmp;

import java.util.HashSet;
import java.util.Set;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.multiplayer.AbstractPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.radar.aircraft.IRadarDataConsumer;
import de.knewcleus.radar.aircraft.IRadarDataProvider;
import de.knewcleus.radar.aircraft.RadarTargetInformation;
import de.knewcleus.radar.aircraft.SSRMode;

public class FGMPRegistry extends AbstractPlayerRegistry<FGMPAircraft> implements IRadarDataProvider, IUpdateable {
	protected final Set<IRadarDataConsumer> consumers=new HashSet<IRadarDataConsumer>();
	protected final Updater radarUpdater=new Updater(this,1000*getSecondsBetweenUpdates());
	
	public FGMPRegistry() {
		radarUpdater.start();
	}
	
	@Override
	public FGMPAircraft createNewPlayer(PlayerAddress address, String callsign) {
		FGMPAircraft aircraft=new FGMPAircraft(address,callsign);
		return aircraft;
	}
	
	@Override
	public int getSecondsBetweenUpdates() {
		return 1;
	}
	
	@Override
	public synchronized void unregisterPlayer(FGMPAircraft expiredPlayer) {
		super.unregisterPlayer(expiredPlayer);
		fireRadarTargetLost(expiredPlayer);
	}
	
	protected void fireRadarDataUpdated(Set<RadarTargetInformation> targets) {
		for (IRadarDataConsumer consumer: consumers) {
			consumer.radarDataUpdated(targets);
		}
	}
	
	protected void fireRadarTargetLost(FGMPAircraft target) {
		for (IRadarDataConsumer consumer: consumers) {
			consumer.radarTargetLost(target);
		}
	}
	
	@Override
	public synchronized void registerRadarDataConsumer(IRadarDataConsumer consumer) {
		consumers.add(consumer);
	}
	
	@Override
	public synchronized void unregisterRadarDataConsumer(IRadarDataConsumer consumer) {
		consumers.remove(consumer);
	}
	
	@Override
	public synchronized void update(double dt) {
		Set<RadarTargetInformation> targets=new HashSet<RadarTargetInformation>();
		for (FGMPAircraft aircraft: getPlayers()) {
			targets.add(new RadarTargetInformation(aircraft,
								aircraft.getLongitude(),aircraft.getLatitude(),
								aircraft.getGroundSpeed(),aircraft.getTrueCourse(),
								SSRMode.MODEC,aircraft.getSSRCode(),aircraft.getPressureAltitude()));
		}
		fireRadarDataUpdated(targets);
	}
}
