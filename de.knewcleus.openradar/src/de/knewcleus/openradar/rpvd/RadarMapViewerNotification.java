package de.knewcleus.openradar.rpvd;

import de.knewcleus.openradar.notify.INotification;

public class RadarMapViewerNotification implements INotification {
	protected final IRadarMapViewerAdapter radarMapViewerAdapter;
	
	public RadarMapViewerNotification(IRadarMapViewerAdapter radarMapViewerAdapter) {
		this.radarMapViewerAdapter = radarMapViewerAdapter;
	}

	@Override
	public IRadarMapViewerAdapter getSource() {
		return radarMapViewerAdapter;
	}

}
