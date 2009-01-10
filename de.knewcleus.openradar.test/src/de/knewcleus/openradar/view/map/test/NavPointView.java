package de.knewcleus.openradar.view.map.test;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.ui.Palette;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;

public class NavPointView implements IBoundedView, INotificationListener {
	protected final static double triangleWidth = 10.0;
	protected final static double triangleHeight = cos(PI/3.0)*triangleWidth;
	
	protected final IMapViewerAdapter mapViewAdapter;
	protected final INavPoint navPoint;
	
	protected Point2D logicalPosition = new Point2D.Double();
	protected Point2D displayPosition = new Point2D.Double();
	protected Path2D displayShape = null;
	protected Rectangle2D displayExtents = new Rectangle2D.Double();
	
	public NavPointView(IMapViewerAdapter mapViewAdapter, INavPoint navPoint) {
		this.mapViewAdapter = mapViewAdapter;
		this.navPoint = navPoint;
		mapViewAdapter.registerListener(this);
		updateLogicalPosition();
	}

	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}

	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(Palette.BEACON);
		g2d.draw(displayShape);
	}

	@Override
	public void validate() {}

	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof ProjectionNotification) {
			updateLogicalPosition();
		} if (notification instanceof CoordinateSystemNotification) {
			updateDisplayPosition();
		}
	}
	
	protected void updateLogicalPosition() {
		final IProjection projection = mapViewAdapter.getProjection();
		logicalPosition = projection.toLogical(navPoint.getGeographicPosition());
		updateDisplayPosition();
	}
	
	protected void updateDisplayPosition() {
		/* make sure that our previously occupied region is redrawn */
		mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
		final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
		displayPosition = logical2display.transform(logicalPosition, null);
		displayShape = new Path2D.Double();
		final double x, y;
		x = displayPosition.getX();
		y = displayPosition.getY();
		displayShape.moveTo(x,y-triangleHeight/2.0);
		displayShape.lineTo(x+triangleWidth/2.0, y+triangleHeight/2.0);
		displayShape.lineTo(x-triangleWidth/2.0, y+triangleHeight/2.0);
		displayShape.closePath();
		displayExtents = displayShape.getBounds2D();
		mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}

}
