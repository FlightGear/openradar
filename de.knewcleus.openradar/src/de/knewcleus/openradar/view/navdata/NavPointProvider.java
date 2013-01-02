/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
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
package de.knewcleus.openradar.view.navdata;

import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.IView;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class NavPointProvider {
	protected final IMapViewerAdapter mapViewerAdapter;
	protected final LayeredView navPointLayer;
	protected final AirportData data;
	
	private List<INavPointListener> navPointListeners = new ArrayList<INavPointListener>();
	
	public NavPointProvider(IMapViewerAdapter mapViewerAdapter, LayeredView navPointLayer, AirportData data) {
		this.mapViewerAdapter = mapViewerAdapter;
		this.navPointLayer = navPointLayer;
		this.data = data;
	}
	
	public IView provideNavPoint(INavPoint point) {
		return new NavPointView(mapViewerAdapter, data, point);
	}
	
	public void addViews(INavDataStream<? extends INavPoint> stream) throws NavDataStreamException {
		INavPoint point;
		while ((point=stream.readDatum())!=null) {
			navPointLayer.pushView(provideNavPoint(point));

			for(INavPointListener l : navPointListeners) {
	            l.navPointAdded(point);
	        }
		}
	}
	
	public void addNavPointListener(INavPointListener l) {
	    navPointListeners.add(l);
	}
	
    public void removeNavPointListener(INavPointListener l) {
        navPointListeners.remove(l);
    }
}
