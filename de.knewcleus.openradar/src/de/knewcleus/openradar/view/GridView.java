package de.knewcleus.openradar.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;

public class GridView implements IView, INotificationListener {
	protected final IViewerAdapter viewAdapter;
	protected double gridLogicalSize;
	
	public GridView(IViewerAdapter viewAdapter, double gridLogicalSize) {
		super();
		this.viewAdapter = viewAdapter;
		this.gridLogicalSize = gridLogicalSize;
		viewAdapter.registerListener(this);
	}
	
	public double getGridLogicalSize() {
		return gridLogicalSize;
	}
	
	public void setGridLogicalSize(double gridLogicalSize) {
		this.gridLogicalSize = gridLogicalSize;
		viewAdapter.getUpdateManager().addDirtyView(this);
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		final AffineTransform oldTransform = g2d.getTransform();
		g2d.transform(viewAdapter.getLogicalToDeviceTransform());
		
		final Rectangle2D clipBounds = g2d.getClipBounds();
		final double minX, maxX, minY, maxY;
		
		minX=Math.floor(clipBounds.getMinX()/gridLogicalSize)*gridLogicalSize;
		minY=Math.floor(clipBounds.getMinY()/gridLogicalSize)*gridLogicalSize;
		maxX=Math.ceil(clipBounds.getMaxX()/gridLogicalSize)*gridLogicalSize;
		maxY=Math.ceil(clipBounds.getMaxY()/gridLogicalSize)*gridLogicalSize;
	
		g2d.setColor(Color.BLACK);
		for (double x=minX; x<=maxX; x+=gridLogicalSize) {
			Line2D line=new Line2D.Double(x,minY,x,maxY);
			g2d.draw(line);
		}
		
		for (double y=minY; y<=maxY; y+=gridLogicalSize) {
			Line2D line=new Line2D.Double(minX,y,maxX,y);
			g2d.draw(line);
		}
		
		g2d.setTransform(oldTransform);
	}
	
	@Override
	public void revalidate() {}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			/* When the logical coordinate system has changed, update the view */
			viewAdapter.getUpdateManager().addDirtyView(this);
		}
	}
}
