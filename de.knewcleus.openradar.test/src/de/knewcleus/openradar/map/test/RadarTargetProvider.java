package de.knewcleus.openradar.map.test;

import java.util.HashMap;
import java.util.Map;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.tracks.ITrack;
import de.knewcleus.openradar.tracks.ITrackManager;
import de.knewcleus.openradar.tracks.TrackLifetimeNotification;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RadarTargetProvider implements INotificationListener {
	protected final IMapViewerAdapter mapViewAdapter;
	protected final LayeredView radarTargetLayer;
	protected final ITrackManager trackManager;
	
	protected final Map<ITrack, RadarTargetView> viewMap = new HashMap<ITrack, RadarTargetView>();
	
	public RadarTargetProvider(IMapViewerAdapter mapViewAdapter, LayeredView radarTargetLayer, ITrackManager trackManager) {
		this.mapViewAdapter = mapViewAdapter;
		this.radarTargetLayer = radarTargetLayer;
		this.trackManager = trackManager;
		trackManager.registerListener(this);
	}

	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof TrackLifetimeNotification) {
			final TrackLifetimeNotification lifetimeNotification;
			lifetimeNotification=(TrackLifetimeNotification)notification;
			
			final ITrack track=lifetimeNotification.getTrack();
			switch (lifetimeNotification.getLifetimeState()) {
			case CREATED:
				final RadarTargetView newView = new RadarTargetView(mapViewAdapter, track);
				viewMap.put(track, newView);
				radarTargetLayer.pushView(newView);
				break;
			case RETIRED:
				final RadarTargetView oldView = viewMap.get(track);
				radarTargetLayer.removeView(oldView);
				viewMap.remove(track);
				break;
			}
		}
	}

}
