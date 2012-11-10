package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayEndMarker extends AViewObject {

    private RunwayEnd runwayEnd;

    public RunwayEndMarker(RunwayEnd runwayEnd) {
        super(Color.white);
        this.runwayEnd = runwayEnd;
        fillPath=true;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        if ( runwayEnd != runwayEnd.getRunway().getLandSide() &&
             runwayEnd != runwayEnd.getRunway().getStartSide() && 
             runwayEnd.getOppositeEnd() != runwayEnd.getRunway().getLandSide() ){ // to show red side opposite of land side
            path=null;
            return;
        }
        color = runwayEnd == runwayEnd.getRunway().getLandSide() ? Palette.RUNWAYEND_OPEN : Palette.RUNWAYEND_FORBIDDEN;
        
        path = new Path2D.Double();
        path.append(new Ellipse2D.Double(newDisplayPosition.getX()-2.5d, newDisplayPosition.getY()-2.5d, 5d, 5d), false);

    }
}