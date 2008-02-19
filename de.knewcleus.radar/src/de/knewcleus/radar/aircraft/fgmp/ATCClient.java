package de.knewcleus.radar.aircraft.fgmp;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.IPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.MultiplayerClient;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;

public class ATCClient extends MultiplayerClient {
	protected final String callsign;
	protected final Position position;

	public ATCClient(IPlayerRegistry playerRegistry, String callsign, Position position)
			throws MultiplayerException {
		super(playerRegistry);
		this.callsign=callsign;
		this.position=position;
	}

	@Override
	public String getCallsign() {
		return callsign;
	}

	@Override
	public Position getLinearVelocity() {
		return new Position();
	}

	@Override
	public String getModel() {
		return "Aircraft/ATC/ATC-set.xml";
	}

	@Override
	public Position getOrientation() {
		return new Position();
	}

	@Override
	public Position getPosition() {
		return position;
	}
}
