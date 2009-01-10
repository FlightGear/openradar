package de.knewcleus.openradar.rpvd;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.ICanvas;
import de.knewcleus.openradar.view.IUpdateManager;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.MapViewerAdapter;

public class RadarMapViewerAdapter extends MapViewerAdapter implements IRadarMapViewerAdapter {
	protected final TrackSelectionManager trackSelectionManager = new TrackSelectionManager();
	protected int trackHistoryLength = 5;
	protected double headingVectorTime = 1.0 * Units.MIN;
	
	public RadarMapViewerAdapter(ICanvas canvas, IUpdateManager updateManager,
			IProjection projection) {
		super(canvas, updateManager, projection);
	}
	
	@Override
	public ITrackSelectionManager getTrackSelectionManager() {
		return trackSelectionManager;
	}

	@Override
	public int getTrackHistoryLength() {
		return trackHistoryLength;
	}
	
	@Override
	public void setTrackHistoryLength(int trackHistoryLength) {
		this.trackHistoryLength = trackHistoryLength;
		notify(new RadarMapViewerNotification(this));
	}
	
	@Override
	public double getHeadingVectorTime() {
		return headingVectorTime;
	}
	
	@Override
	public void setHeadingVectorTime(double headingVectorTime) {
		this.headingVectorTime = headingVectorTime;
	}

}
