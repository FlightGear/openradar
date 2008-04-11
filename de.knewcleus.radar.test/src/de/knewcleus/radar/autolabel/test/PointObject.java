package de.knewcleus.radar.autolabel.test;

import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.autolabel.ILabel;
import de.knewcleus.radar.autolabel.ILabeledObject;

public class PointObject implements ILabeledObject {
	protected double x,y;
	protected final double r;
	protected final double vx,vy;
	protected final ILabel label;
	
	protected static final double labelWidth=0.05;
	protected static final double labelHeight=0.025;
	protected static final double maxLabelDist=0.1;
	protected static final double minLabelDist=0.04;
	protected static final double meanLabelDist=(minLabelDist+maxLabelDist)/2.0;
	protected static final double labelDistRange=maxLabelDist-minLabelDist;

	public PointObject(double x, double y, double vx, double vy, double r) {
		this.x=x;
		this.y=y;
		this.r=r;
		this.vx=vx;
		this.vy=vy;
		this.label=new SimpleLabel(this,labelWidth,labelHeight);
	}

	@Override
	public ILabel getLabel() {
		return label;
	}

	@Override
	public double getPriority() {
		return 1E5;
	}
	
	public void update() {
		x+=vx;
		y+=vy;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getR() {
		return r;
	}
	
	public double getVx() {
		return vx;
	}
	
	public double getVy() {
		return vy;
	}
	
	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(x-r,y-r,2*r,2*r);
	}
}
