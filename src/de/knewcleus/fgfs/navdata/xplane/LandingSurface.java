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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navdata.xplane;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IAerodrome;
import de.knewcleus.fgfs.navdata.model.ILandingSurface;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.model.SurfaceType;

public class LandingSurface implements INavPoint, ILandingSurface {
	protected IAerodrome aerodrome;
	protected final SurfaceType surfaceType;
	protected final float length;
	protected final float width;
	protected final Point2D geographicCenter;
	protected final float trueHeading;
	protected final String designation;
	
	public LandingSurface(SurfaceType surfaceType, float length, float width,
			Point2D geographicCenter, float trueHeading, String designation) {
		this.surfaceType = surfaceType;
		this.length = length;
		this.width = width;
		this.geographicCenter = geographicCenter;
		this.trueHeading = trueHeading;
		this.designation = designation;
	}
	
	@Override
	public Point2D getGeographicPosition() {
		return geographicCenter;
	}

	protected void setAerodrome(IAerodrome aerodrome) {
		this.aerodrome = aerodrome;
	}

	public IAerodrome getAerodrome() {
		return aerodrome;
	}

	public String getAirportID() {
		return aerodrome.getIdentification();
	}

	public SurfaceType getSurfaceType() {
		return surfaceType;
	}

	public float getLength() {
		return length;
	}

	public float getWidth() {
		return width;
	}

	public Point2D getGeographicCenter() {
		return geographicCenter;
	}

	public float getTrueHeading() {
		return trueHeading;
	}

	public String getDesignation() {
		return designation;
	}

}