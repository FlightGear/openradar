package de.knewcleus.openradar.radardata;

public interface IRadarDataProvider {
	public void registerRecipient(IRadarDataRecipient recipient);
	public void unregisterRecipient(IRadarDataRecipient recipient);
}
