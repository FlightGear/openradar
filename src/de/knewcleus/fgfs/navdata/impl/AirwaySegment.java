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
package de.knewcleus.fgfs.navdata.impl;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment;
import de.knewcleus.fgfs.navdata.model.IIntersection;

public class AirwaySegment implements IAirwaySegment {
	protected final String identification;
	protected final Category category;
	protected final IIntersection startPoint;
	protected final IIntersection endPoint;
	protected final boolean oneWay;
	protected final float topAltitude;
	protected final float baseAltitude;
	
	public AirwaySegment(String identification, Category category,
			IIntersection startPoint, IIntersection endPoint, boolean oneWay,
			float topAltitude, float baseAltitude) {
		this.identification = identification;
		this.category = category;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.oneWay = oneWay;
		this.topAltitude = topAltitude;
		this.baseAltitude = baseAltitude;
	}

	@Override
	public String getIdentification() {
		return identification;
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public IIntersection getStartPoint() {
		return startPoint;
	}

	@Override
	public IIntersection getEndPoint() {
		return endPoint;
	}

	@Override
	public boolean isOneWay() {
		return oneWay;
	}

	@Override
	public float getTopAltitude() {
		return topAltitude;
	}

	@Override
	public float getBaseAltitude() {
		return baseAltitude;
	}
	
	@Override
	public String toString() {
		return String.format("AWY %s (%s)-(%s) %s FL%03d-FL%03d %s",
				identification,
				startPoint, endPoint,
				category.toString(),
				(int)Math.round(baseAltitude / 100.0f / Units.FT), 
				(int)Math.round(topAltitude / 100.0f / Units.FT),
				(oneWay?"oneway":"twoway")); 
	}
}
