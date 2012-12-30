package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class NDBName extends AViewObject {
    
    private AirportData data;
    private String activeText;
    
    public NDBName(AirportData data, NDB ndb, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, ndb.getIdentification(), minScaleText, maxScaleText);
        this.data = data;
        this.activeText = ndb.getIdentification();
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {

        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale; 
        scale = 15 * 10/scale;
        if(scale<10) scale=10;
        if(scale>15) scale=15;
        
        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()+scale,newDisplayPosition.getY()+scale));
        if(data.getRadarObjectFilterState("NDB")) {
            text = activeText;
        } else {
            text = null;
        }
    }
}
