package de.knewcleus.radar.autolabel.test;

import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.autolabel.Label;

public class SimpleLabel implements Label {
	protected final PointObject associatedObject;
	protected double hookX, hookY;
	protected final double width, height;
	
	protected double centerX,centerY;
	
	public SimpleLabel(PointObject associatedObject, double width, double height) {
		this.associatedObject=associatedObject;
		this.width=width;
		this.height=height;
		hookX=0.0;
		hookY=0.1;
	}

	@Override
	public PointObject getAssociatedObject() {
		return associatedObject;
	}

	@Override
	public double getHookX() {
		return hookX;
	}

	@Override
	public double getHookY() {
		return hookY;
	}

	@Override
	public void move(double dx, double dy) {
		hookX+=dx;
		hookY+=dy;
		
		final double hookLen=Math.sqrt(hookX*hookX+hookY*hookY);
		final double dirX,dirY;
		
		dirX=hookX/hookLen;
		dirY=hookY/hookLen;
		
		if (hookLen>PointObject.maxLabelDist) {
			hookX=dirX*PointObject.maxLabelDist;
			hookY=dirY*PointObject.maxLabelDist;
		} else if (hookLen<PointObject.minLabelDist) {
			hookX=dirX*PointObject.minLabelDist;
			hookY=dirY*PointObject.minLabelDist;
		}
		
		centerX=hookX+dirX*width/2.0;
		centerY=hookY+dirY*height/2.0;
	}

	@Override
	public double getChargeDensity() {
		return 1E2;
	}
	
	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(associatedObject.getX()-centerX-width/2,
									  associatedObject.getY()-centerY-height/2,
									  width, height);
	}
}
