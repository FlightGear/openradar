package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class VORFrequency extends AViewObject {

    private AirportData data;
    private String activeText;

    public VORFrequency(AirportData data, VOR vor, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, vor.getFrequency().toString(), minScaleText, maxScaleText);
        this.data = data;
        this.activeText = vor.getFrequency().toString();
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale; 
        scale = 15 * 10/scale;
        if(scale<10) scale=10;
        if(scale>15) scale=15;
        
        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()+scale,newDisplayPosition.getY()+scale+font.getSize()));
        if(data.getRadarObjectFilterState("VOR")) {
            text = activeText;
        } else {
            text = null;
        }
    }
}
