package de.knewcleus.radar.aircraft.fgmp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.multiplayer.AbstractPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.radar.aircraft.IRadarDataConsumer;
import de.knewcleus.radar.aircraft.IRadarDataProvider;

public class FGMPRegistry extends AbstractPlayerRegistry<FGMPAircraft> implements IRadarDataProvider<FGMPAircraft>, IUpdateable {
	protected final Set<IRadarDataConsumer<? super FGMPAircraft>> consumers=new HashSet<IRadarDataConsumer<? super FGMPAircraft>>();
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
	public void registerPlayer(FGMPAircraft player) {
		super.registerPlayer(player);
		fireRadarTargetAcquired(player);
	}
	
	@Override
	public void unregisterPlayer(FGMPAircraft expiredPlayer) {
		super.unregisterPlayer(expiredPlayer);
		fireRadarTargetLost(expiredPlayer);
	}
	
	protected void fireRadarDataUpdated() {
		for (IRadarDataConsumer<? super FGMPAircraft> consumer: consumers) {
			consumer.radarDataUpdated();
		}
	}
	
	protected void fireRadarTargetAcquired(FGMPAircraft target) {
		for (IRadarDataConsumer<? super FGMPAircraft> consumer: consumers) {
			consumer.radarTargetAcquired(target);
		}
	}
	
	protected void fireRadarTargetLost(FGMPAircraft target) {
		for (IRadarDataConsumer<? super FGMPAircraft> consumer: consumers) {
			consumer.radarTargetLost(target);
		}
	}
	
	@Override
	public void registerRadarDataConsumer(IRadarDataConsumer<? super FGMPAircraft> consumer) {
		consumers.add(consumer);
	}
	
	@Override
	public void unregisterRadarDataConsumer(IRadarDataConsumer<? super FGMPAircraft> consumer) {
		consumers.remove(consumer);
	}
	
	@Override
	public void update(double dt) {
		fireRadarDataUpdated();
	}
	
	@Override
	public Iterator<FGMPAircraft> iterator() {
		return getPlayers().iterator();
	}
}
