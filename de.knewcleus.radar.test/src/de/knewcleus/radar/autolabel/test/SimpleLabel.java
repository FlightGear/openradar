package de.knewcleus.radar.autolabel.test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.autolabel.Label;

public class SimpleLabel implements Label {
	protected final PointObject associatedObject;
	protected Point2D position;
	protected final double width, height;
	
	protected double centerX,centerY;
	
	public SimpleLabel(PointObject associatedObject, double width, double height) {
		this.associatedObject=associatedObject;
		this.width=width;
		this.height=height;
		final Rectangle2D symbolBounds=associatedObject.getBounds2D();
		this.position=new Point2D.Double(symbolBounds.getCenterX()+0.1, symbolBounds.getCenterY());
	}

	@Override
	public PointObject getAssociatedObject() {
		return associatedObject;
	}
	
	@Override
	public void setCentroidPosition(double x, double y) {
		final Rectangle2D symbolBounds=associatedObject.getBounds2D();
		double dx,dy;
		
		dx=x-symbolBounds.getCenterX();
		dy=y-symbolBounds.getCenterY();
		
		
		final double hookLen=Math.sqrt(dx*dx+dy*dy);
		final double dirX,dirY;
		
		dirX=dx/hookLen;
		dirY=dy/hookLen;
		
		if (hookLen>PointObject.maxLabelDist) {
			dx=dirX*PointObject.maxLabelDist;
			dy=dirY*PointObject.maxLabelDist;
		} else if (hookLen<PointObject.minLabelDist) {
			dx=dirX*PointObject.minLabelDist;
			dy=dirY*PointObject.minLabelDist;
		}
		
		position.setLocation(symbolBounds.getCenterX()+dx-width/2.0, symbolBounds.getCenterY()+dy-height/2.0);
	}

	@Override
	public double getPriority() {
		return 1E2;
	}
	
	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(position.getX(), position.getY(), width, height);
	}
}
