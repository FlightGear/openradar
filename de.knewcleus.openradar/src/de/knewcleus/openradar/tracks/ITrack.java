/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2015 Wolfram Wagner
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
package de.knewcleus.openradar.tracks;

import java.util.List;

import de.knewcleus.openradar.radardata.IRadarDataPacket;

/**
 * A track represents the state history of a radar target.
 *  
 * @author Ralf Gerlich
 * 
 * @see ITrackManager
 *
 */
public interface ITrack {
	/**
	 * @return the number of states available.
	 */
	public int size();
	
	/**
	 * Return whether the track is considered lost.
	 */
	public boolean isLost();
	
	/**
	 * Return the state with the given index.
	 * 
	 * The index must be in the range <code>[0;size())</code>
	 * 
	 * The current state has index 0, with indices increasing towards older states.
	 * 
	 * @param index		The index of the desired stat.
	 * @return the state with the given index.
	 * @throws IndexOutOfBoundsException	The given index is out of bounds.
	 */
	public IRadarDataPacket getState(int index);
	
	/**
	 * Return the current state of the track.
	 * This is equivalent to getState(0)
	 * 
	 * @return the current state of the track.
	 * @see #getState(int)
	 */
	public IRadarDataPacket getCurrentState();
	
	public int getTailOffset();

    public void resetTailOffset();

    public List<IRadarDataPacket> getCopyOfHistory();
}
