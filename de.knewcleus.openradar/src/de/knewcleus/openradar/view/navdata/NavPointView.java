package de.knewcleus.openradar.view.navdata;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.navdata.model.INavPoint;
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

public class NavPointView implements IBoundedView, INotificationListener {
    protected final static double triangleWidth = 10.0;
    protected final static double triangleHeight = cos(PI / 3.0) * triangleWidth;

    protected final IMapViewerAdapter mapViewAdapter;
    protected final INavPoint navPoint;

    protected boolean visible = true;

    protected Point2D logicalPosition = new Point2D.Double();
    protected Point2D displayPosition = new Point2D.Double();
    protected Path2D displayShape = null;
    protected Rectangle2D displayExtents = new Rectangle2D.Double();

//    protected Path2D rwLandSide = null;
//    protected Path2D rwStartSide = null;
    
    protected AViewObjectPainter<?> viewObjectPainter;
//    protected volatile String text = null;

    public NavPointView(IMapViewerAdapter mapViewAdapter, AirportData data, INavPoint navPoint) {
        this.mapViewAdapter = mapViewAdapter;
        this.navPoint = navPoint;
        // factory method
        viewObjectPainter = AViewObjectPainter.getPainterForNavpoint(mapViewAdapter, data, navPoint);

        mapViewAdapter.registerListener(this);
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
        logicalPosition = projection.toLogical(navPoint.getGeographicPosition());
        updateDisplayPosition();
    }

    protected void updateDisplayPosition() {

        final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
        displayPosition = logical2display.transform(logicalPosition, null);
        viewObjectPainter.updateDisplayPosition(displayPosition);
        
//        if (navPoint instanceof Helipad) {
//            Helipad hp = (Helipad) navPoint;
//            setText(hp.getDesignation());
//
//            /* make sure that our previously occupied region is redrawn */
//            mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
//            final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
//            displayPosition = logical2display.transform(logicalPosition, null);
//            displayShape = new Path2D.Double();
//            final double x, y;
//            x = displayPosition.getX();
//            y = displayPosition.getY();
//            displayShape.moveTo(x, y);
//            displayShape.append(new Ellipse2D.Double(x - 8.0d, y - 8.0d, 16.0d, 16.0d), false);
//            displayShape.moveTo(x - 3, y - 4);
//            displayShape.lineTo(x - 3, y + 4);
//            displayShape.moveTo(x - 3, y);
//            displayShape.lineTo(x + 3, y);
//            displayShape.moveTo(x + 3, y - 4);
//            displayShape.lineTo(x + 3, y + 4);
//            displayExtents = displayShape.getBounds2D();
//            mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
//
//        } else {
//
//            if (navPoint instanceof IAerodrome) {
//                setText(((IAerodrome) navPoint).getIdentification());
//            } else if (navPoint instanceof RunwayEnd) {
//                IRunwayEnd runwayEnd = (IRunwayEnd) navPoint;
//                setText(runwayEnd.getRunwayID());
//                
//                rwStartSide = getRWStartSideShapes(displayShape,runwayEnd);
//                rwLandSide = getRWLandSideShapes(displayShape,runwayEnd);
//                
//            } else {
//                System.out.println(navPoint.getClass());
//            }
//            /* make sure that our previously occupied region is redrawn */
//            mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
//            final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
//            displayPosition = logical2display.transform(logicalPosition, null);
//            // displayShape = new Path2D.Double();
//            final double x, y;
//            x = displayPosition.getX();
//            y = displayPosition.getY();
//            // displayShape.moveTo(x, y - triangleHeight / 2.0);
//            // displayShape.lineTo(x + triangleWidth / 2.0, y + triangleHeight /
//            // 2.0);
//            // displayShape.lineTo(x - triangleWidth / 2.0, y + triangleHeight /
//            // 2.0);
//            // displayShape.closePath();
//            displayExtents = new Rectangle2D.Double(x - 50, y - 50, 100, 100);
//            mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
//        }
    }

//      public synchronized void setText(String text) {
//        this.text = text;
//    }
//
//    public synchronized String getText() {
//        return this.text;
//    }
}
