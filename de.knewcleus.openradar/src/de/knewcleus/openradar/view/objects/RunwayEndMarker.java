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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.RunwayData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayEndMarker extends AViewObject {

    private final RunwayEnd runwayEnd;
    private final AirportData data;

    public RunwayEndMarker(RunwayEnd runwayEnd, AirportData data) {
        super(Color.white);
        this.runwayEnd = runwayEnd;
        this.data = data;
        fillPath=true;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        RunwayData rwd = data.getRunwayData(runwayEnd.getRunwayID());
        boolean enabled = rwd==null || (rwd!=null && rwd.isEnabledAtAll()); 

        if ( !enabled || !runwayEnd.isActive() && !runwayEnd.getOppositeEnd().isActive()) {
               path=null;
               return;
        }
        
        color = runwayEnd == runwayEnd.getRunway().getLandSide() 
                || runwayEnd.getOppositeEnd() == runwayEnd.getRunway().getStartSide()
                ? Palette.RUNWAYEND_OPEN : Palette.RUNWAYEND_FORBIDDEN;
        
        path = new Path2D.Double();
        path.append(new Ellipse2D.Double(newDisplayPosition.getX()-2.5d, newDisplayPosition.getY()-2.5d, 5d, 5d), false);

    }
}