package de.knewcleus.openradar.view.map.test;

import java.util.HashMap;
import java.util.Map;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.rpvd.IRadarMapViewerAdapter;
import de.knewcleus.openradar.rpvd.RadarTargetView;
import de.knewcleus.openradar.tracks.ITrack;
import de.knewcleus.openradar.tracks.ITrackManager;
import de.knewcleus.openradar.tracks.TrackLifetimeNotification;
import de.knewcleus.openradar.view.LayeredView;

public class RadarTargetProvider implements INotificationListener {
	protected final IRadarMapViewerAdapter radarMapViewAdapter;
	protected final LayeredView radarTargetLayer;
	protected final ITrackManager trackManager;
	
	protected final Map<ITrack, RadarTargetView> viewMap = new HashMap<ITrack, RadarTargetView>();
	protected final Map<ITrack, LabelView> labelViewMap = new HashMap<ITrack, LabelView>();
	
	public RadarTargetProvider(IRadarMapViewerAdapter radarMapViewAdapter, LayeredView radarTargetLayer, ITrackManager trackManager) {
		this.radarMapViewAdapter = radarMapViewAdapter;
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
			case CREATED: {
				final RadarTargetView targetView = new RadarTargetView(radarMapViewAdapter, track);
				final LabelView labelView = new LabelView(radarMapViewAdapter, track);
				viewMap.put(track, targetView);
				labelViewMap.put(track, labelView);
				radarTargetLayer.pushView(targetView);
				radarTargetLayer.pushView(labelView);
				break;
			}
			case RETIRED: {
				final RadarTargetView targetView = viewMap.get(track);
				final LabelView labelView = labelViewMap.get(track);
				radarTargetLayer.removeView(targetView);
				radarTargetLayer.removeView(labelView);
				viewMap.remove(track);
				labelViewMap.remove(track);
				break;
			}
			}
		}
	}

}
