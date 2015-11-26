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
package de.knewcleus.openradar.radardata;

import java.awt.geom.Point2D;


/**
 * Radar packets contain data about a target detected by surveillance radar.
 * 
 * It may include secondary radar data.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IRadarDataPacket {
	/**
	 * Determine whether the target was actually seen in the last
	 * radar antenna scan.
	 * 
	 * If the target was not seen, the data from the last available
	 * scan or estimated data is provided.
	 * 
	 * @return <code>true</code>, if and only if the target was seen
	 *         in the last scan.
	 */
	public abstract boolean wasSeenOnLastScan();

	/**
	 * Every radar datum is associated with a timestamp expressed in seconds.
	 */
	public abstract float getTimestamp();

	/**
	 * @return the geographic position of the radar target.
	 */
	public abstract Point2D getPosition();
	
	/**
	 * @return the tracking identifier.
	 */
	public abstract Object getTrackingIdentifier();

	/**
	 * @return SSR data, if any, or null if no SSR data is available for this target.
	 */
	public abstract ISSRData getSSRData();

	/**
	 * @return the calculated velocity.
	 */
	public float getCalculatedVelocity();
	
	/**
	 * @return the calculated true course.
	 */
	public float getCalculatedTrueCourse();
}
