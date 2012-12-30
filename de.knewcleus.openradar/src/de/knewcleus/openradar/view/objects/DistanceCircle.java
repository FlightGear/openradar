package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class DistanceCircle extends AViewObject {

    public enum Style { MINOR, PLAIN, IMPORTANT }
    private double radius;
    private AirportData data;
    
    private double x1=0;
    private double x2=0;
    private double y1=0;
    private double y2=0;
    private Point2D point;
    
    public DistanceCircle(AirportData data, Style style, double radius, int minScale, int maxScale) {
        super(Color.gray);
        this.data =data;
        this.radius = radius;
        setMinScalePath(minScale);
        setMaxScalePath(maxScale);
        
        this.font = new Font("Arial", Font.PLAIN, 9);
        
        switch(style) {
        case MINOR:
            color=Color.gray;
            this.stroke = new BasicStroke(0.3f);
            break;
        case PLAIN:
            color=Color.lightGray;
            this.stroke = new BasicStroke(0.4f);
            break;
        case IMPORTANT:
            color=Color.white;
            this.stroke = new BasicStroke(0.5f);
            break;
        }
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        
        double dotRadius = Converter2D.getFeetToDots(radius*Units.NM/Units.FT, mapViewAdapter);
        Point2D logPoint = mapViewAdapter.getProjection().toLogical(data.getAirportPosition());
        point = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint,null);

        x1 = Converter2D.getMapDisplayPoint(point, 270d, dotRadius).getX();
        y1 = Converter2D.getMapDisplayPoint(point, 0d, dotRadius).getY();
        x2 = Converter2D.getMapDisplayPoint(point, 90d, dotRadius).getX();
        y2 = Converter2D.getMapDisplayPoint(point, 180d, dotRadius).getY();

        path = new Path2D.Double();
        if(data.getRadarObjectFilterState("CIRCLES")) {
            path.append(new Ellipse2D.Double(x1, y1, x2-x1, y2-y1), false);
        }
    }
    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        super.paint(g2d, mapViewAdapter);
        if(data.getRadarObjectFilterState("CIRCLES")) {
            g2d.setColor(color);
            double currentScale = mapViewAdapter.getLogicalScale();
            if (minScalePath < currentScale && maxScalePath > currentScale) {
                if (font != null)
                    g2d.setFont(font);
                
                String sRadius = String.format("%1.0f", radius);
                g2d.drawString(sRadius, (float) point.getX(), (float) y1+10);
                g2d.drawString(sRadius, (float) point.getX(), (float) y2-4);
                g2d.drawString(sRadius, (float) x1+4, (float) point.getY());
                g2d.drawString(sRadius, (float) x2- (radius>99?20:14), (float) point.getY());
            }
        }
    }
}
