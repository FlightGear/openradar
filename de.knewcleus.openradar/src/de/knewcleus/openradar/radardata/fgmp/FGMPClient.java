package de.knewcleus.openradar.radardata.fgmp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.IPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.MultiplayerClient;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.IRadarDataProvider;
import de.knewcleus.openradar.radardata.IRadarDataRecipient;

public class FGMPClient<T extends TargetStatus> extends MultiplayerClient<T> implements IRadarDataProvider {
	protected final static Position linearVelocity=new Position();
	protected final static Position orientation=new Position();
	protected final String callsign;
	protected final Position position;
	protected long lastAntennaRotationTime;
	
	/**
	 * Time for a single antenna rotation in milliseconds.
	 * 
	 * At most one radar data packet per aircraft is sent per antenna rotation.
	 */
	protected static final int antennaRotationTimeMsecs=1000;
	
	/**
	 * Timeout for removal of stale targets in milliseconds.
	 */
	protected static final int staleTargetTimeoutMsecs=4 * antennaRotationTimeMsecs;

	
	protected final Set<IRadarDataRecipient> recipients=new HashSet<IRadarDataRecipient>();

	public FGMPClient(IPlayerRegistry<T> playerRegistry, String callsign, Position position) throws IOException {
		super(playerRegistry);
		this.callsign=callsign;
		this.position=position;
		lastAntennaRotationTime=System.currentTimeMillis();
	}
	
	@Override
	protected int getUpdateMillis() {
		return antennaRotationTimeMsecs;
	}
	
	@Override
	protected int getPlayerTimeoutMillis() {
		return staleTargetTimeoutMsecs;
	}
	
	@Override
	protected void update() {
		super.update();
		sendRadarPackets();
	}

	@Override
	public String getCallsign() {
		return callsign;
	}

	@Override
	public Position getLinearVelocity() {
		return linearVelocity;
	}

	@Override
	public String getModel() {
		return "Aircraft/ATC/ATC-set.xml";
	}

	@Override
	public Position getOrientation() {
		return orientation;
	}

	@Override
	public Position getPosition() {
		return position;
	}
	
	protected synchronized void sendRadarPackets() {
		if (lastAntennaRotationTime+antennaRotationTimeMsecs>System.currentTimeMillis()) {
			return;
		}
		synchronized (getPlayerRegistry()) {
			for (TargetStatus targetStatus: getPlayerRegistry().getPlayers()) {
				final IRadarDataPacket packet=targetStatus.getRadarDataPacket();
				for (IRadarDataRecipient recipient: recipients) {
					recipient.acceptRadarData(this, packet);
				}
			}
		}
		lastAntennaRotationTime=System.currentTimeMillis();
	}
	
	@Override
	public synchronized void registerRecipient(IRadarDataRecipient recipient) {
		recipients.add(recipient);
	}
	
	@Override
	public synchronized void unregisterRecipient(IRadarDataRecipient recipient) {
		recipients.remove(recipient);
	}
}
