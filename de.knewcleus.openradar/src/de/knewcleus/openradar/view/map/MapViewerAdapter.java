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

import java.awt.Component;
import java.awt.Point;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.ICanvas;
import de.knewcleus.openradar.view.IRadarViewChangeListener.Change;
import de.knewcleus.openradar.view.IUpdateManager;
import de.knewcleus.openradar.view.ViewerAdapter;

public class MapViewerAdapter extends ViewerAdapter implements IMapViewerAdapter {
	protected volatile IProjection projection;
	protected volatile Point2D originalOrigin = null;

	
	public MapViewerAdapter(ICanvas canvas, IUpdateManager updateManager, IProjection projection, Point2D center) {
		super(canvas, updateManager);
		this.projection = projection;
        this.originalOrigin = center;
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

    public void setZoom(double scale, Point2D newGeoOrigin) {
        
        setLogicalScale(scale, newGeoOrigin);
        originalOrigin = getCenter(); // centers zoom at mouse
    }       
    
    @Override
    public void setLogicalScale(double scale) {
        Point2D center = getGeoCenter();
        setLogicalScale(scale, center);
    }

    public void setLogicalScale(double scale, Point mouseLocation) {
        setLogicalScale(scale, originalOrigin);
    }
    
    public void setLogicalScale(double scale, Point2D center) {
        this.logicalScale = scale;
        updateTransforms();
        notifyListeners(Change.ZOOM);
        
        setGeoCenter(center);
    }
    
    public void shiftDeviceOrigin(Point2D deviceOrigin, Point2D deviceTarget) {
        // new
        double deltaX = deviceTarget.getX() - deviceOrigin.getX();
        double deltaY = deviceTarget.getY() - deviceOrigin.getY();
        setDeviceOrigin(new Point2D.Double(getDeviceOrigin().getX()+deltaX, getDeviceOrigin().getY()+deltaY));
        
        updateTransforms();
        originalOrigin = getGeoLocationOf(getDeviceCenter());
    }
    

    public Point2D getDeviceCenter() {
        Component c = canvas.getManagedComponent();
        double x = c.getWidth()/2;
        double y = c.getHeight()/2;
        return new Point2D.Double(x, y);
    }
    
    
    /**
     * Returns the GEO coordinates of the point in the middle
     * @return
     */
    public Point2D getGeoCenter() {
        return projection.toGeographical(getDeviceToLogicalTransform().transform(getDeviceOrigin(),null));
    }

    public void setCenterOnDevicePoint(Point devicePoint) {
        Point2D newCenter = getGeoLocationOf(devicePoint);
        setGeoCenter(newCenter);
        originalOrigin= newCenter;
    }

    
    
    /**
     * Sets the center of the displayed map to the given GEO coordinates 
     * @return
     */
    public void setGeoCenter(Point2D newGeoCenter) {
        Point2D newDeviceCenter = getLogicalToDeviceTransform().transform(projection.toLogical(newGeoCenter),null);
        shiftDeviceOrigin(newDeviceCenter,getDeviceCenter());
    }
    
//    public void setGeoOrigin(Point2D newGeoOrigin) {
//        Point2D newDeviceOrigin = getLogicalToDeviceTransform().transform(projection.toLogical(newGeoOrigin),null);
//        shiftDeviceOrigin(getDeviceCenter(), newDeviceOrigin);
//    }       
    
    public void centerMap() {
        setGeoCenter(originalOrigin);
    }

	public Point2D getCenter() {
	    return getGeoLocationOf(getDeviceCenter()); 
	}

   public Point2D getGeoLocationOf(Point2D devicePoint) {
        return getProjection().toGeographical(getDeviceToLogicalTransform().transform(devicePoint,null));
    }

   public Point2D getGeoLocationOf(Point awtPoint) {
       return getProjection().toGeographical(getDeviceToLogicalTransform().transform(new Point2D.Double(awtPoint.getX(),awtPoint.getY()),null));
    }

   public void forceRepaint() {
       updateTransforms();
   }
}
