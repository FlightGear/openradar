package de.knewcleus.openradar.radardata;

import java.util.HashSet;
import java.util.Set;

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
	
	protected void publish(IRadarData data) {
		for (IRadarDataRecipient recipient: recipients) {
			recipient.acceptRadarData(this, data);
		}
	}

}
