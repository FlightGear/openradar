package de.knewcleus.openradar.ui.rpvd;

import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.openradar.ui.map.RadarMapPanel;

public class RadarPlanViewContext {
	protected final RadarMapPanel radarPlanViewPanel;
	protected final RadarPlanViewSettings radarPlanViewSettings;
	protected final IMapProjection deviceTransformation;
	
	public RadarPlanViewContext(RadarMapPanel radarPlanViewPanel, RadarPlanViewSettings radarPlanViewSettings, IMapProjection deviceTransformation) {
		this.radarPlanViewPanel=radarPlanViewPanel;
		this.radarPlanViewSettings=radarPlanViewSettings;
		this.deviceTransformation=deviceTransformation;
	}
	
	public RadarMapPanel getRadarPlanViewPanel() {
		return radarPlanViewPanel;
	}
	
	public RadarPlanViewSettings getRadarPlanViewSettings() {
		return radarPlanViewSettings;
	}
	
	public IMapProjection getDeviceTransformation() {
		return deviceTransformation;
	}
}
