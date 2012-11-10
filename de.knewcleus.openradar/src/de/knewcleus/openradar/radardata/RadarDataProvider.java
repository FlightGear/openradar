package de.knewcleus.openradar.radardata;

import java.util.HashSet;
import java.util.Set;
/**
 * This class is a the parent of classes that receive the radar data from somewhere.
 * 
 * The implementation here forwards the data to its registered listeners, the recipients.
 * 
 * @author Ralf Gehrlich
 *
 */
public abstract class RadarDataProvider implements IRadarDataProvider {
	protected final Set<IRadarDataRecipient> recipients = new HashSet<IRadarDataRecipient>();

	@Override
	public void registerRecipient(IRadarDataRecipient recipient) {
		assert(!recipients.contains(recipient));
		recipients.add(recipient);
	}

	@Override
	public void unregisterRecipient(IRadarDataRecipient recipient) {
		assert(recipients.contains(recipient));
		recipients.remove(recipient);
	}
	
	protected void publishRadarDataPacket(IRadarDataPacket data) {
		for (IRadarDataRecipient recipient: recipients) {
			recipient.acceptRadarData(this, data);
		}
	}

}
