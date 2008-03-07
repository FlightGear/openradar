package de.knewcleus.radar.aircraft.fgmp;

import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.Player;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.radar.aircraft.AircraftType;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.radio.Channel;
import de.knewcleus.radar.radio.IMessage;

public class FGMPAircraft extends Player implements IAircraft {
	public FGMPAircraft(PlayerAddress address, String callsign) {
		super(address, callsign);
	}

	@Override
	public FlightType getFlightType() {
		// TODO: properly determine flight type
		return FlightType.GA;
	}

	@Override
	public String getOperator() {
		// TODO: properly determine operator
		return "";
	}

	@Override
	public AircraftType getType() {
		// TODO: properly determine aircraft type
		return null;
	}
	
	@Override
	public Vector3D getVelocityVector() {
		return linearVelocity;
	}
	
	@Override
	public void update(double dt) {
		position=position.add(linearVelocity.scale(dt));
	}

	@Override
	public void receive(Channel channel, IMessage message) {
	}
}
