package de.knewcleus.openradar.radardata.fgatc;

import de.knewcleus.openradar.radardata.ISSRData;

public class SSRData implements ISSRData {
	protected final PositionPacket packet;
	
	public SSRData(PositionPacket packet) {
		this.packet = packet;
	}

	@Override
	public String getMarkXModeACode() {
		return packet.getSSRCode();
	}

	@Override
	public float getMarkXModeCElevation() {
		return packet.getEncoderAltitude();
	}

	@Override
	public boolean hasMarkXModeACode() {
		return packet.isSSRActive();
	}

	@Override
	public boolean hasMarkXModeCElevation() {
		return packet.isEncoderActive();
	}

	@Override
	public boolean hasMarkXSPI() {
		return packet.hasSpecialPurposeIndicator();
	}

}
