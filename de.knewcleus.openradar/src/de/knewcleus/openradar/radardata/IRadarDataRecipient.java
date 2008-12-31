package de.knewcleus.openradar.radardata;

public interface IRadarDataRecipient {
	public void acceptRadarData(IRadarDataProvider provider, IRadarData radarData);
}
