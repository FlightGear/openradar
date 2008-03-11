package de.knewcleus.radar.aircraft.fgmp;

import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.Player;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;
import de.knewcleus.radar.aircraft.AircraftType;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.radio.Channel;
import de.knewcleus.radar.radio.IMessage;

public class FGMPAircraft extends Player implements IAircraft {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
	
	protected Position geodeticPosition=new Position();
	
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
	public Position getPosition() {
		return geodeticPosition;
	}
	
	@Override
	public Vector3D getVelocityVector() {
		return orientation.transform(linearVelocity);
	}
	
	@Override
	public void update(double dt) {
	}
	
	@Override
	public void updatePosition(long t, PositionMessage packet) {
		super.updatePosition(t, packet);
		geodeticPosition=geodToCartTransformation.backward(super.getCartesianPosition());
	}

	@Override
	public void receive(Channel channel, IMessage message) {
	}
}
