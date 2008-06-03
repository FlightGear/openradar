package de.knewcleus.openradar.ui.rpvd;

import de.knewcleus.fgfs.location.IDeviceTransformation;

public class RadarPlanViewContext {
	protected final RadarPlanViewPanel radarPlanViewPanel;
	protected final RadarPlanViewSettings radarPlanViewSettings;
	protected final IDeviceTransformation deviceTransformation;
	
	public RadarPlanViewContext(RadarPlanViewPanel radarPlanViewPanel, RadarPlanViewSettings radarPlanViewSettings, IDeviceTransformation deviceTransformation) {
		this.radarPlanViewPanel=radarPlanViewPanel;
		this.radarPlanViewSettings=radarPlanViewSettings;
		this.deviceTransformation=deviceTransformation;
	}
	
	public RadarPlanViewPanel getRadarPlanViewPanel() {
		return radarPlanViewPanel;
	}
	
	public RadarPlanViewSettings getRadarPlanViewSettings() {
		return radarPlanViewSettings;
	}
	
	public IDeviceTransformation getDeviceTransformation() {
		return deviceTransformation;
	}
}
