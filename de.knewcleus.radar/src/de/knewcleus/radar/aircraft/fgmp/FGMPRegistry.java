package de.knewcleus.radar.aircraft.fgmp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.knewcleus.fgfs.multiplayer.AbstractPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.aircraft.IRadarDataConsumer;
import de.knewcleus.radar.aircraft.IRadarDataProvider;

public class FGMPRegistry extends AbstractPlayerRegistry<FGMPAircraft> implements IRadarDataProvider<FGMPAircraft> {
	protected final Set<IRadarDataConsumer> consumers=new HashSet<IRadarDataConsumer>();
	
	@Override
	public FGMPAircraft createNewPlayer(PlayerAddress address, String callsign) {
		FGMPAircraft aircraft=new FGMPAircraft(address,callsign);
		return aircraft;
	}
	
	@Override
	public void registerPlayer(FGMPAircraft player) {
		super.registerPlayer(player);
		fireRadarTargetAcquired((IAircraft)player);
	}
	
	@Override
	public void unregisterPlayer(FGMPAircraft expiredPlayer) {
		super.unregisterPlayer(expiredPlayer);
		fireRadarTargetLost((IAircraft)expiredPlayer);
	}
	
	protected void fireRadarTargetAcquired(IAircraft target) {
		for (IRadarDataConsumer consumer: consumers) {
			consumer.radarTargetAcquired(target);
		}
	}
	
	protected void fireRadarTargetLost(IAircraft target) {
		for (IRadarDataConsumer consumer: consumers) {
			consumer.radarTargetLost(target);
		}
	}
	
	@Override
	public void registerRadarDataConsumer(IRadarDataConsumer consumer) {
		consumers.add(consumer);
	}
	
	@Override
	public void unregisterRadarDataConsumer(IRadarDataConsumer consumer) {
		consumers.remove(consumer);
	}
	
	@Override
	public Iterator<FGMPAircraft> iterator() {
		return getPlayers().iterator();
	}
}
