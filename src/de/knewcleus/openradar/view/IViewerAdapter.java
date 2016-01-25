/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
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

import de.knewcleus.openradar.notify.INotifier;

/**
 * A view adapter centrally manages the aspects of a viewer,
 * such as the logical and device coordinate systems.
 * 
 * It is also the central interface between the viewer and the views.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IViewerAdapter extends INotifier {
	public abstract ICanvas getCanvas();
	
	/**
	 * @return the update manager for the viewer.
	 */
	public abstract IUpdateManager getUpdateManager();

	/**
	 * @return the current device extents of the viewer.
	 */
	public abstract Rectangle2D getViewerExtents();

	/**
	 * Set the device extents of the viewer and issue a notification about the change.
	 */
	public abstract void setViewerExtents(Rectangle2D extents);

	/**
	 * @return the current logical-to-device transformation for this map.
	 */
	public abstract AffineTransform getLogicalToDeviceTransform();

	/**
	 * @return the current device-to-logical transformation for this map.
	 */
	public abstract AffineTransform getDeviceToLogicalTransform();
	
	/**
	 * @return the current device origin.
	 */
	public abstract Point2D getDeviceOrigin();
	
	/**
	 * Set the device origin.
	 * 
	 * @param origin  The new device origin.
	 */
	public abstract void setDeviceOrigin(Point2D origin);
	
	/**
	 * Set the device origin and issue a notification about the change.
	 * 
	 * @param originX	The new x-origin
	 * @param originY	The new y-origin
	 */
	public abstract void setDeviceOrigin(double originX, double originY);

	/**
	 * @return the current logical scale.
	 */
	public abstract double getLogicalScale();

	/**
	 * Set the logical scale and issue a notification about the change.
	 * @param scale		The new scale.
	 */
	public abstract void setLogicalScale(double scale);
	
    /**
     * Set the logical scale and issue a notification about the change. This zooms at this spot
     * @param scale     The new scale.
     */
    public abstract void setLogicalScale(double scale, Point mouseLocation);

    /**
	 * @return the current logical origin.
	 */
	public abstract Point2D getLogicalOrigin();

	/**
	 * Set the logical origin.
	 * @param originX	The new logical x-origin
	 * @param originY	The new logical y-origin
	 */
	public abstract void setLogicalOrigin(double originX, double originY);
	
	/**
	 * Set the logical origin.
	 * @param origin 	The new logical origin.
	 */
	public abstract void setLogicalOrigin(Point2D origin);

}