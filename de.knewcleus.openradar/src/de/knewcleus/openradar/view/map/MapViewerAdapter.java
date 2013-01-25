/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012,2013 Wolfram Wagner
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
package de.knewcleus.openradar.view.map;

import java.awt.Point;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.ICanvas;
import de.knewcleus.openradar.view.IRadarViewChangeListener.Change;
import de.knewcleus.openradar.view.IUpdateManager;
import de.knewcleus.openradar.view.ViewerAdapter;

public class MapViewerAdapter extends ViewerAdapter implements IMapViewerAdapter {
	protected volatile IProjection projection;
	protected volatile Point2D originalCenter = null;
	protected volatile Point2D currentCenter = null;

	
	public MapViewerAdapter(ICanvas canvas, IUpdateManager updateManager, IProjection projection, Point2D center) {
		super(canvas, updateManager);
		this.projection = projection;
        this.currentCenter = center; 
        this.originalCenter = center;
	}

	@Override
	public IProjection getProjection() {
		return projection;
	}
	
	public void setProjection(IProjection projection) {
		this.projection = projection;
		updateTransforms();
		notify(new ProjectionNotification(this));
	}

	public Point2D getGeoLocationOf(Point awtPoint) {
        Point2D result = new Point2D.Double();
        getDeviceToLogicalTransform().transform(new Point2D.Double(awtPoint.getX(),awtPoint.getY()), result);
        result = getProjection().toGeographical(result);
        return result;
    }

    public void setZoom(double scale, Point2D newCenter) {
        setLogicalOrigin(new Point2D.Double());
        currentCenter = newCenter;
        setProjection(new LocalSphericalProjection(newCenter));
        setLogicalScale(scale);
        notifyListeners(Change.CENTER);
    }       
    
    
    public void setCenter(Point2D newCenter) {
        setProjection(new LocalSphericalProjection(newCenter));
        currentCenter = newCenter;
        setLogicalOrigin(new Point2D.Double());
        notifyListeners(Change.CENTER);
    }       

    public void centerMap() {
        setProjection(new LocalSphericalProjection(originalCenter));
        currentCenter = originalCenter;
        setLogicalOrigin(new Point2D.Double());
        notifyListeners(Change.CENTER);
    }

	public Point2D getCenter() {
	    return projection.toGeographical(logicalOrigin); 
	}
}
