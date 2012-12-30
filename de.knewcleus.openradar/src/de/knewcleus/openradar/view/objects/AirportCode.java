package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class AirportCode extends AViewObject {
    
    private AirportData data;
    private String activeText;

    public AirportCode(AirportData data, Font font, Color color, String text, int minScaleText, int maxScaleText) {
        super(font, color, text, minScaleText, maxScaleText);
        this.data = data;
        this.activeText = text;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        setTextCoordinates(newDisplayPosition);
        if(data.getRadarObjectFilterState("APT")) {
            text = activeText;
        } else {
            text = null;
        }
    }
}
