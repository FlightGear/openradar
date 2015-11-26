/**
 * Copyright (C) 2012 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class NDBFrequency extends AViewObject {

    private GuiMasterController master;
    private String activeText;
    private Intersection ndb;
    private int defaultMaxScale;
    private Color defaultColor;

    public NDBFrequency(GuiMasterController master, NDB ndb, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, ndb.getFrequency().toString(), minScaleText, maxScaleText);
        this.master = master;
        this.ndb = ndb;
        this.defaultMaxScale = maxScaleText;
        this.defaultColor = color;
        this.activeText = ndb.getFrequency().toString();
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {

        Color highLightColor = master.getAirportData().getNavaidDB().getNavaidHighlightColor(master,ndb);

        if(highLightColor!=null) {
            this.maxScaleText=Integer.MAX_VALUE;
            this.color = highLightColor;
        } else {
            this.maxScaleText=defaultMaxScale;
            this.color = defaultColor;
        }

        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale;
        scale = 15 * 10/scale;
        if(scale<10) scale=10;
        if(scale>15) scale=15;

        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()+scale,newDisplayPosition.getY()+scale+font.getSize()));
        if(master.getAirportData().getRadarObjectFilterState("NDB") || ndb.isHighlighted() || master.getAirportData().getNavaidDB().isPartOfRoute(master, ndb)) {
            text = activeText;
        } else {
            text = null;
        }
    }
}
