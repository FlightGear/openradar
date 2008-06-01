package de.knewcleus.radar.ui.core;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DisplayEdge extends DisplayElement {
	protected final DisplayElement node1;
	protected final DisplayElement node2;
	protected Line2D line=null;
	
	public DisplayEdge(DisplayElement node1, DisplayElement node2) {
		this.node1=node1;
		this.node2=node2;
		node1.addDependent(this);
		node2.addDependent(this);
	}
	
	@Override
	public Rectangle2D getBounds() {
		if (line==null)
			return null;
		return line.getBounds2D();
	}
	
	@Override
	public void validate() {
		invalidate();
		final Rectangle2D bounds1=node1.getBounds();
		final Rectangle2D bounds2=node2.getBounds();
		if (bounds1==null || bounds2==null) {
			line=null;
			return;
		}
		final Point2D hook1=getHookPoint(node1, node2);
		final Point2D hook2=getHookPoint(node2, node1);
		line=new Line2D.Double(hook1, hook2);
		invalidate();
	}

	@Override
	public boolean isHit(Point2D position) {
		/* This edge cannot be hit at all */
		return false;
	}

	@Override
	public void paintElement(Graphics2D g) {
		if (line==null)
			return;
		g.draw(line);
	}
	
	protected Point2D getHookPoint(DisplayElement src, DisplayElement dst) {
		final double vx, vy;
		
		final Rectangle2D srcbounds=src.getBounds();
		final Rectangle2D dstbounds=dst.getBounds();
		
		vx=dstbounds.getCenterX()-srcbounds.getCenterX();
		vy=dstbounds.getCenterY()-srcbounds.getCenterY();
		
		final Point2D dhook=src.getRelativeHookPoint(vx, vy);
		
		return new Point2D.Double(srcbounds.getCenterX()+dhook.getX(), srcbounds.getCenterY()+dhook.getY());
	}
}
