package de.knewcleus.openradar.map.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.map.ILayer;
import de.knewcleus.openradar.map.IMap;
import de.knewcleus.openradar.map.IViewVisitor;
import de.knewcleus.openradar.notify.Notifier;

public class GridView extends Notifier implements ILayer {
	protected final IMap map;
	protected final double gridX, gridY;
	
	public GridView(IMap map, double gridX, double gridY) {
		super();
		this.map = map;
		this.gridX = gridX;
		this.gridY = gridY;
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public IMap getMap() {
		return map;
	}
	
	@Override
	public String getName() {
		return "Grid";
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		final AffineTransform oldTransform = g2d.getTransform();
		g2d.transform(map.getLogicalToDeviceTransform());
		
		final Rectangle2D clipBounds = g2d.getClipBounds();
		final double minX, maxX, minY, maxY;
		
		minX=Math.floor(clipBounds.getMinX()/gridX)*gridX;
		minY=Math.floor(clipBounds.getMinY()/gridY)*gridY;
		maxX=Math.ceil(clipBounds.getMaxX()/gridX)*gridX;
		maxY=Math.ceil(clipBounds.getMaxY()/gridY)*gridY;
	
		g2d.setColor(Color.BLACK);
		for (double x=minX; x<=maxX; x+=gridX) {
			Line2D line=new Line2D.Double(x,minY,x,maxY);
			g2d.draw(line);
		}
		
		for (double y=minY; y<=maxY; y+=gridY) {
			Line2D line=new Line2D.Double(minX,y,maxX,y);
			g2d.draw(line);
		}
		
		g2d.setTransform(oldTransform);
	}
	
	@Override
	public void traverse(IViewVisitor visitor) {
		// TODO Auto-generated method stub
	}
}
