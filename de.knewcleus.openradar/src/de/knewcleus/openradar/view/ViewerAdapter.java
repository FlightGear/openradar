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
package de.knewcleus.openradar.view;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.notify.Notifier;
import de.knewcleus.openradar.view.IRadarViewChangeListener.Change;

public class ViewerAdapter extends Notifier implements IViewerAdapter {
	protected final ICanvas canvas;
	protected final IUpdateManager updateManager; 
	protected Rectangle2D viewerExtents = new Rectangle2D.Double();
	protected double logicalScale = 1.0;
	private Point2D logicalOrigin = new Point2D.Double();
	private Point2D deviceOrigin = new Point2D.Double();
	protected AffineTransform deviceToLogicalTransform = null;
	protected AffineTransform logicalToDeviceTransform = null;

	protected List<IRadarViewChangeListener> listeners = new ArrayList<IRadarViewChangeListener>();
	
	public ViewerAdapter(ICanvas canvas, IUpdateManager updateManager) {
		this.canvas = canvas;
		this.updateManager = updateManager;
		updateTransforms();
	}
	
	public void addRadarViewChangeListener(IRadarViewChangeListener l) {
	    listeners.add(l);
	}
    public void removeRadarViewChangeListener(IRadarViewChangeListener l) {
        listeners.remove(l);
    }
	
	@Override
	public ICanvas getCanvas() {
		return canvas;
	}
	
	@Override
	public IUpdateManager getUpdateManager() {
		return updateManager;
	}

	@Override
	public Rectangle2D getViewerExtents() {
		return viewerExtents;
	}

	@Override
	public void setViewerExtents(Rectangle2D extents) {
		viewerExtents = extents;
		notify(new CoordinateSystemNotification(this));
	}
	
	@Override
	public Point2D getDeviceOrigin() {
		return deviceOrigin;
	}
	
	@Override
	public void setDeviceOrigin(double originX, double originY) {
		setDeviceOrigin(new Point2D.Double(originX, originY));
	}
	
	@Override
	public void setDeviceOrigin(Point2D origin) {
		deviceOrigin = origin;
		updateTransforms();
        notifyListeners(Change.CENTER);
	}

	@Override
	public double getLogicalScale() {
		return logicalScale;
	}

	@Override
	public void setLogicalScale(double scale) {
		this.logicalScale = scale;
		updateTransforms();
        notifyListeners(Change.ZOOM);
	}

    @Override
    public void setLogicalScale(double scale, Point mouseLocation) {
        this.logicalScale = scale;
        updateTransforms();
        notifyListeners(Change.ZOOM);
    }
	
	public void shiftLogicalOrigin(double offsetX, double offsetY) {
		setLogicalOrigin(logicalOrigin.getX()+offsetX, logicalOrigin.getY()+offsetY);
	}
	
    public void setLogicalOrigin(double newX, double newY) {
        logicalOrigin = new Point2D.Double(newX, newY);
        updateTransforms();
        notifyListeners(Change.CENTER);
    }

    @Override
	public void setLogicalOrigin(Point2D origin) {
		logicalOrigin = origin;
		updateTransforms();
        notifyListeners(Change.CENTER);
	}
	
	@Override
	public Point2D getLogicalOrigin() {
		return logicalOrigin;
	}

	@Override
	public AffineTransform getDeviceToLogicalTransform() {
		return deviceToLogicalTransform;
	}

	@Override
	public AffineTransform getLogicalToDeviceTransform() {
		return logicalToDeviceTransform;
	}
	
	protected void updateTransforms() {
		deviceToLogicalTransform = new AffineTransform(
				logicalScale, 0,
				0, -logicalScale,
				logicalOrigin.getX() - logicalScale * deviceOrigin.getX(),
				logicalOrigin.getY() + logicalScale * deviceOrigin.getY());
		logicalToDeviceTransform = new AffineTransform(
				1.0/logicalScale, 0,
				0, -1.0/logicalScale,
				deviceOrigin.getX() - logicalOrigin.getX()/logicalScale,
				deviceOrigin.getY() + logicalOrigin.getY()/logicalScale);
		notify(new CoordinateSystemNotification(this));
	}
	
	public void notifyListeners(Change c) {
	    for(IRadarViewChangeListener l : listeners) {
	        l.radarViewChanged(this, c);
	    }
	}
}