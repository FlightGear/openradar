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

	public FlightType getFlightType() {
		// TODO: properly determine flight type
		return FlightType.GA;
	}

	public String getOperator() {
		// TODO: properly determine operator
		return "";
	}

	public AircraftType getType() {
		// TODO: properly determine aircraft type
		return null;
	}

	public double getVelocity() {
		return getLinearVelocity().getLength();
	}

	public void update(double dt) {
		Vector3D distanceMade=new Vector3D(linearVelocity);
		linearVelocity.scale(dt);
		position.translate(distanceMade);
	}

	public void receive(Channel channel, IMessage message) {
	}
}
