package de.knewcleus.radar.ui.rpvd;

import de.knewcleus.fgfs.location.IDeviceTransformation;

public class RadarPlanViewContext {
	protected final RadarPlanViewSettings radarPlanViewSettings;
	protected final IDeviceTransformation deviceTransformation;
	
	public RadarPlanViewContext(RadarPlanViewSettings radarPlanViewSettings, IDeviceTransformation deviceTransformation) {
		this.radarPlanViewSettings=radarPlanViewSettings;
		this.deviceTransformation=deviceTransformation;
	}
	
	public RadarPlanViewSettings getRadarPlanViewSettings() {
		return radarPlanViewSettings;
	}
	
	public IDeviceTransformation getDeviceTransformation() {
		return deviceTransformation;
	}
}
