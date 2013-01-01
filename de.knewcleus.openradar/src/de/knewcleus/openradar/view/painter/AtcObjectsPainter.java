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

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.DistanceCircle;

public class AtcObjectsPainter extends AViewObjectPainter<AirportData> {

    public AtcObjectsPainter(IMapViewerAdapter mapViewAdapter, AirportData data) {
        super(mapViewAdapter, data);
        
        DistanceCircle line = new DistanceCircle(data,DistanceCircle.Style.MINOR , 5, 0, 150);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN , 10, 0, 150);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.MINOR , 15, 0, 150);
        viewObjectList.add(line);
        
        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN , 20, 0, 500);
        viewObjectList.add(line);
        
        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN, 40, 0, 500);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.IMPORTANT, 60, 0, Integer.MAX_VALUE);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN, 80, 0, 500);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN, 100, 0, 500);
        viewObjectList.add(line);
}    
    
}
