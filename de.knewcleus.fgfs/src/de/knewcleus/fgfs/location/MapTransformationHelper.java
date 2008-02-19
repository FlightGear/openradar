package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class MapTransformationHelper {
	protected IDeviceTransformation mapTransformation;
	
	public MapTransformationHelper(IDeviceTransformation mapTransformation) {
		this.mapTransformation=mapTransformation;
	}
	
	public Rectangle2D toLocal(Position p0, Position p1) {
		Point2D localP0=mapTransformation.toDevice(p0);
		Point2D localP1=mapTransformation.toDevice(p1);
		
		double xmin,xmax,ymin,ymax;
		
		xmin=Math.min(localP0.getX(),localP1.getX());
		xmax=Math.max(localP0.getX(),localP1.getX());
		ymin=Math.min(localP0.getY(),localP1.getY());
		ymax=Math.max(localP0.getY(),localP1.getY());
		
		return new Rectangle2D.Double(xmin,ymin,xmax-xmin,ymax-ymin);
	}
}
