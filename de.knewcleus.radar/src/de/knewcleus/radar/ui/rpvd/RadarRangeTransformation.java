package de.knewcleus.radar.ui.rpvd;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;


public class RadarRangeTransformation implements IDeviceTransformation {
	protected double width, height, range;
	private double centerx, centery, scale;
	
	public RadarRangeTransformation(double width, double height, double range) {
		this.width=width;
		this.height=height;
		this.range=range;
		recalculateTransformation();
	}
	
	public void setSize(double width, double height) {
		this.width=width;
		this.height=height;
		recalculateTransformation();
	}
	
	public void setHeight(double height) {
		this.height = height;
		recalculateTransformation();
	}
	
	public void setWidth(double width) {
		this.width = width;
		recalculateTransformation();
	}
	
	public void setRange(double range) {
		this.range=range;
		recalculateTransformation();
	}
	
	public double getTotalRange() {
		return Math.max(width,height)/2.0/scale;
	}
	
	private void recalculateTransformation() {
		centerx=width/2.0;
		centery=height/2.0;
		scale=2.0*range/Math.min(width,height);
	}

	public Position fromDevice(Point2D point) {
		return new Position((point.getX()-centerx)*scale,(centery-point.getY())*scale,0.0);
	}

	public Position fromDeviceRelative(Point2D dimension) {
		return new Position(dimension.getX()*scale,-dimension.getY()*scale,0.0);
	}

	public Point2D toDevice(Position pos) {
		return new Point2D.Double(centerx+pos.getX()/scale,centery-pos.getY()/scale);
	}

	public Point2D toDeviceRelative(Vector3D dimension) {
		return new Point2D.Double(dimension.getX()/scale,-dimension.getY()/scale);
	}
}
