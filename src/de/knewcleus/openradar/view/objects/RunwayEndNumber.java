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
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.RunwayData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayEndNumber extends AViewObject {

    private final RunwayEnd runwayEnd;
    private final AirportData data;
    private int defaultMinScaleText;
    private int defaultMaxScaleText;
    
    public RunwayEndNumber(RunwayEnd runwayEnd, AirportData data, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, runwayEnd.getRunwayID(), minScaleText, maxScaleText);
        this.runwayEnd = runwayEnd;
        this.data = data;
        this.defaultMinScaleText = minScaleText;
        this.defaultMaxScaleText = maxScaleText;
    }

    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        RunwayData rwd = data.getRunwayData(runwayEnd.getRunwayID());
        boolean enabled = rwd==null || (rwd!=null && rwd.isEnabledAtAll()); 
        
        if(enabled) {
            if((runwayEnd.isStartingActive() || runwayEnd.isLandingActive()) ) {
                setMinScaleText(0);
                setMaxScaleText(Integer.MAX_VALUE);
            } else {
                setMinScaleText(defaultMinScaleText);
                setMaxScaleText(defaultMaxScaleText);
            }
            super.paint(g2d, mapViewAdapter);
        }
    }
    
    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()-12,newDisplayPosition.getY()+12));
    }
}
