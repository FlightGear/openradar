package de.knewcleus.openradar.radardata;

/**
 * A radar data recipient receives radar data from a radar data provider.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IRadarDataRecipient {
	public void acceptRadarData(IRadarDataProvider provider, IRadarDataPacket radarData);
}
