package de.knewcleus.openradar.view.groundnet;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;
import de.knewcleus.openradar.view.painter.AViewObjectPainter;

public class GroundnetView implements IBoundedView, INotificationListener {
    protected final IMapViewerAdapter mapViewAdapter;
    protected final AirportData data;
    protected GroundnetReader groundnetReader;
    protected final TaxiWayObjext obj;

    protected boolean visible = true;

    protected Point2D logicalPosition = new Point2D.Double();
    protected Point2D displayPosition = new Point2D.Double();
    protected Path2D displayShape = null;
    protected Rectangle2D displayExtents = new Rectangle2D.Double();

    
    protected AViewObjectPainter<?> viewObjectPainter;

	public GroundnetView(IMapViewerAdapter mapViewAdapter, TaxiWayObjext obj, AirportData data) {
		this.mapViewAdapter = mapViewAdapter;
		this.data=data;
//		this.groundnetReader = groundnetReader;
		this.obj=obj;
		mapViewAdapter.registerListener(this);
        // factory method
        viewObjectPainter = AViewObjectPainter.getPainterForNavpoint(mapViewAdapter, data, obj);
        updateLogicalPosition();
	}
    @Override
    public Rectangle2D getDisplayExtents() {
        return viewObjectPainter.getDisplayExtents();
    }

    @Override
    public void accept(IViewVisitor visitor) {
        visitor.visitView(this);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        mapViewAdapter.getUpdateManager().markRegionDirty(viewObjectPainter.getDisplayExtents()/*displayExtents*/);
    }

    @Override
    public void paint(Graphics2D g2d) {
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.PLAIN, 4));

        viewObjectPainter.paint(g2d);
        
    }

    @Override
    public void validate() {
    }

    @Override
    public void acceptNotification(INotification notification) {
        if (notification instanceof ProjectionNotification) {
            updateLogicalPosition();
        }
        if (notification instanceof CoordinateSystemNotification) {
            updateDisplayPosition();
        }
    }

    protected void updateLogicalPosition() {
        final IProjection projection = mapViewAdapter.getProjection();
        logicalPosition = projection.toLogical(obj.getGeoPoint());
        updateDisplayPosition();
    }

    protected void updateDisplayPosition() {
        final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
        displayPosition = logical2display.transform(logicalPosition, null);
        viewObjectPainter.updateDisplayPosition(displayPosition);
    }        
}
