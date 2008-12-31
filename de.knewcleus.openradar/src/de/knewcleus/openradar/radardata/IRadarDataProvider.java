package de.knewcleus.openradar.radardata;

/**
 * A radar data provider delivers data from surveillance radar stations.
 * 
 * Some radar data providers may deliver estimated data of targets, even though
 * the target has not been seen in recent radar antenna scans. However, this is
 * not guaranteed and the recipient must employ own measures for loss-of-target
 * detection.
 * 
 * @author Ralf Gerlich
 * 
 * @see IRadarDataRecipient
 *
 */
public interface IRadarDataProvider {
	public void registerRecipient(IRadarDataRecipient recipient);
	public void unregisterRecipient(IRadarDataRecipient recipient);
}
