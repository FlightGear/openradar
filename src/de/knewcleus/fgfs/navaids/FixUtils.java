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
package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;

public class FixUtils {
	/**
	 * Determine the true bearing from station to current position (QTE).
	 */
	public static float getQTE(Position current, Position station) {
		double dx=station.getX()-current.getX();
		double dy=station.getY()-current.getY();
		
		float qte=(float)Math.toDegrees(Math.atan2(dy, dx));
		
		if (qte<=0.0)
			qte+=360.0;
		if (qte>360.0)
			qte-=360.0;
		
		return qte;
	}
	
	/**
	 * Determine the true bearing from current position to station (QUJ).
	 */
	public static float getQUJ(Position current, Position station) {
		return getQTE(station,current);
	}
	
	/**
	 * Determine the magnetic bearing from current position to station (QDM).
	 */
	public static float getQDM(Position current, Position station) {
		return getQDR(station,current);
	}
	
	/**
	 * Determine the magnetic bearing from station to current position (QDR/radial).
	 */
	public static float getQDR(Position current, Position station) {
		float qte=getQTE(current,station);
		
		float qdr=qte;
		// TODO: correct for variation
		
		return qdr;
	}
	
	/**
	 * Determine the lateral position relative to the given line of position (LOP)
	 * of the given station.
	 * 
	 * The line of position is oriented in the given direction. The lateral position is positive when
	 * current position is left of the LOP as seen when looking in the direction given.
	 */
	public static double getRelativePosition(Position current, Position station, float radial) {
		double vrx,vry;
		
		vrx=Math.sin(Math.toRadians(radial));
		vry=Math.cos(Math.toRadians(radial));
		
		double dx,dy;
		dx=current.getX()-station.getX();
		dy=current.getY()-station.getY();
		
		return -vry*dx+vrx*dy;
	}
	
	/**
	 * Determine the distance from the given station.
	 */
	public static double getDistance(Position current, Position station) {
		double dx,dy;
		dx=current.getX()-station.getX();
		dy=current.getY()-station.getY();
		
		return Math.sqrt(dx*dx+dy*dy);
	}
}
