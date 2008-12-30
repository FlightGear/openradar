package de.knewcleus.openradar.map.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.map.CoordinateSystemNotification;
import de.knewcleus.openradar.map.IMapViewAdapter;
import de.knewcleus.openradar.map.IView;
import de.knewcleus.openradar.map.IViewVisitor;
import de.knewcleus.openradar.map.ViewNotification;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.notify.Notifier;

public class GridView extends Notifier implements IView, INotificationListener {
	protected final IMapViewAdapter mapViewAdapter;
	protected final double gridX, gridY;
	
	public GridView(IMapViewAdapter mapViewAdapter, double gridX, double gridY) {
		super();
		this.mapViewAdapter = mapViewAdapter;
		this.gridX = gridX;
		this.gridY = gridY;
		mapViewAdapter.registerListener(this);
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		final AffineTransform oldTransform = g2d.getTransform();
		g2d.transform(mapViewAdapter.getLogicalToDeviceTransform());
		
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
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			/* When the logical coordinate system has change, update the view */
			final CoordinateSystemNotification coordinateSystemNotification;
			coordinateSystemNotification=(CoordinateSystemNotification)notification;
			if (coordinateSystemNotification.isTransformationChanged()) {
				fireViewChange(new ViewNotification(this));
			}
		}
	}
	
	protected void fireViewChange(ViewNotification notification) {
		notify(notification);
		mapViewAdapter.acceptNotification(notification);
	}
}
