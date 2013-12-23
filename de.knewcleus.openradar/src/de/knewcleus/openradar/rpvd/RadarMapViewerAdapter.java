/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012  Wolfram Wagner
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
package de.knewcleus.openradar.rpvd;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.ICanvas;
import de.knewcleus.openradar.view.IUpdateManager;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.MapViewerAdapter;

public class RadarMapViewerAdapter extends MapViewerAdapter implements IRadarMapViewerAdapter {
	protected int trackHistoryLength = 5;
	protected double headingVectorTime = 1.0 * Units.MIN;
	
	public RadarMapViewerAdapter(ICanvas canvas, IUpdateManager updateManager, IProjection projection, Point2D center) {
		super(canvas, updateManager, projection, center);
	}

	@Override
	public int getTrackHistoryLength() {
		return trackHistoryLength;
	}
	
	@Override
	public void setTrackHistoryLength(int trackHistoryLength) {
		this.trackHistoryLength = trackHistoryLength;
		notify(new RadarMapViewerNotification(this));
	}
	
	@Override
	public double getHeadingVectorTime() {
		return headingVectorTime;
	}
	
	@Override
	public void setHeadingVectorTime(double headingVectorTime) {
		this.headingVectorTime = headingVectorTime;
	}

}
