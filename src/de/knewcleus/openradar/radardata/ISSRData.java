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

/**
 * Secondary Surveilance Radar (SSR) data provided by a radar station.
 * 
 * @author Ralf Gerlich
 *
 */
public interface ISSRData {
	/**
	 * @return true, iff Standard Mark X Mode A code is available.
	 */
	public boolean hasMarkXModeACode();
	
	/**
	 * @return Standard Mark X Mode A code, if any, or null, if none available
	 */
	public String getMarkXModeACode();
	
	/**
	 * @return true, iff Standard Mark X Mode C elevation is available.
	 */
	public boolean hasMarkXModeCElevation();
	
	/**
	 * @return Standard Mark X Mode C elevation in feet, if any
	 */
	public float getMarkXModeCElevation();
	
	/**
	 * @return true, iff Standard Mark X Special Purpose Indicator was received.
	 */
	public boolean hasMarkXSPI();
}
