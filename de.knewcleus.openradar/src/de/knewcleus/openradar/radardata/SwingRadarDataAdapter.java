package de.knewcleus.openradar.radardata;

import java.util.LinkedList;
import java.util.Queue;

import javax.swing.SwingUtilities;

public class SwingRadarDataAdapter extends RadarDataProvider implements IRadarDataRecipient, Runnable {
	protected final Queue<IRadarDataPacket> packetQueue = new LinkedList<IRadarDataPacket>();

	@Override
	public synchronized void acceptRadarData(IRadarDataProvider provider, IRadarDataPacket radarData) {
		if (packetQueue.isEmpty()) {
			SwingUtilities.invokeLater(this);
		}
		packetQueue.add(radarData);
	}
	
	@Override
	public synchronized void run() {
		while (!packetQueue.isEmpty()) {
			final IRadarDataPacket packet = packetQueue.poll();
			publishRadarDataPacket(packet);
		}
	}

}
