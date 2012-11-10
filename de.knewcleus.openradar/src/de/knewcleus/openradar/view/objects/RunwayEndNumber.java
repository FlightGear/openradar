package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayEndNumber extends AViewObject {

    private RunwayEnd runwayEnd;
    private int defaultMinScaleText;
    private int defaultMaxScaleText;
    
    public RunwayEndNumber(RunwayEnd runwayEnd, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, runwayEnd.getRunwayID(), minScaleText, maxScaleText);
        this.runwayEnd = runwayEnd;
        this.defaultMinScaleText = minScaleText;
        this.defaultMaxScaleText = maxScaleText;
    }

    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        if(runwayEnd.isStartingActive() || runwayEnd.isLandingActive() ) {
            setMinScaleText(0);
            setMaxScaleText(Integer.MAX_VALUE);
        } else {
            setMinScaleText(defaultMinScaleText);
            setMaxScaleText(defaultMaxScaleText);
        }
        super.paint(g2d, mapViewAdapter);
    }
    
    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()-12,newDisplayPosition.getY()+12));
    }
}
