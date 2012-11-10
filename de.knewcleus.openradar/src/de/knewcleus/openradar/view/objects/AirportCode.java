package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class AirportCode extends AViewObject {
    public AirportCode(Font font, Color color, String text, int minScaleText, int maxScaleText) {
        super(font, color, text, minScaleText, maxScaleText);
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        setTextCoordinates(newDisplayPosition);
    }
}
