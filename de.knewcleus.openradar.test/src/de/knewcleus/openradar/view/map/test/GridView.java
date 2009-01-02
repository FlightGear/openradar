package de.knewcleus.openradar.view.map.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.IViewerAdapter;

public class GridView implements IView, INotificationListener {
	protected final IViewerAdapter viewAdapter;
	protected final double gridX, gridY;
	
	public GridView(IViewerAdapter viewAdapter, double gridX, double gridY) {
		super();
		this.viewAdapter = viewAdapter;
		this.gridX = gridX;
		this.gridY = gridY;
		viewAdapter.registerListener(this);
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
	public void revalidate() {}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			/* When the logical coordinate system has changed, update the view */
			viewAdapter.getUpdateManager().addDirtyView(this);
		}
	}
}
