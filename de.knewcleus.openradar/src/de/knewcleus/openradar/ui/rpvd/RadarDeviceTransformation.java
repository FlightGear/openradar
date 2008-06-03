package de.knewcleus.openradar.ui.rpvd;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;


public class RadarDeviceTransformation implements IDeviceTransformation {
	protected final RadarPlanViewSettings radarPlanViewSettings;
	private double centerx, centery, scale=1;
	
	public RadarDeviceTransformation(RadarPlanViewSettings radarPlanViewSettings) {
		this.radarPlanViewSettings=radarPlanViewSettings;
	}
	
	public void update(int width, int height) {
		centerx=width/2.0;
		centery=height/2.0;
		scale=2.0/Math.min(width,height);
	}

	public Position fromDevice(Point2D point) {
		double range=radarPlanViewSettings.getRange()*Units.NM;
		return new Position((point.getX()-centerx)*range*scale,(centery-point.getY())*range*scale,0.0);
	}

	public Position fromDeviceRelative(Point2D dimension) {
		double range=radarPlanViewSettings.getRange()*Units.NM;
		return new Position(dimension.getX()*range*scale,-dimension.getY()*range*scale,0.0);
	}

	public Point2D toDevice(Position pos) {
		double range=radarPlanViewSettings.getRange()*Units.NM;
		return new Point2D.Double(centerx+pos.getX()/range/scale,centery-pos.getY()/range/scale);
	}

	public Point2D toDeviceRelative(Vector3D dimension) {
		double range=radarPlanViewSettings.getRange()*Units.NM;
		return new Point2D.Double(dimension.getX()/range/scale,-dimension.getY()/range/scale);
	}
}
