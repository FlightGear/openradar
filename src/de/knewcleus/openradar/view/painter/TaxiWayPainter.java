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
package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.HoldPoint;
import de.knewcleus.openradar.view.objects.ParkingPosition;
import de.knewcleus.openradar.view.objects.TaxiwayLine;

public class TaxiWayPainter extends AViewObjectPainter<TaxiWaySegment> {

    public TaxiWayPainter(AirportData data, IMapViewerAdapter mapViewAdapter, TaxiWaySegment seg) {
        super(mapViewAdapter, seg);
        
        Font font = new Font("Arial", Font.PLAIN, 9);
        
        TaxiwayLine line = new TaxiwayLine(seg,0,12);
        viewObjectList.add(line);
        
        if(seg.getBegin() instanceof ParkPos &&
                !"Startup Location".equals(((ParkPos)seg.getBegin()).getName()) ) {
            
            ParkingPosition pos = new ParkingPosition(data, seg,(ParkPos)seg.getBegin(),font,Color.lightGray,0,10);
            viewObjectList.add(pos);
        }

        if(!"none,PushBack".contains(seg.getBegin().getHoldPointType())
                && !(seg.getBegin() instanceof ParkPos)) {
            HoldPoint hp = new HoldPoint(seg, seg.getBegin(), 0,10);
            viewObjectList.add(hp);
        }
        
        if(seg.getEnd() instanceof ParkPos &&
                !"Startup Location".equals(((ParkPos)seg.getEnd()).getName()) ) {
            ParkingPosition pos = new ParkingPosition(data,seg,(ParkPos)seg.getEnd(),font,Color.lightGray,0,10);
            viewObjectList.add(pos);
        }

        if(!"none,PushBack".contains(seg.getEnd().getHoldPointType()) 
                && !(seg.getEnd() instanceof ParkPos)) {
            HoldPoint hp = new HoldPoint(seg, seg.getEnd(),0,10);
            viewObjectList.add(hp);
        }
    }    
    
}
