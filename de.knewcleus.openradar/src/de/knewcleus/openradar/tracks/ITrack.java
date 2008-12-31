package de.knewcleus.openradar.tracks;

import java.util.Iterator;

import de.knewcleus.openradar.notify.INotifier;
import de.knewcleus.openradar.radardata.IRadarDataPacket;

/**
 * A track represents the state history of a radar target.
 *  
 * @author Ralf Gerlich
 * 
 * @see ITrackManager
 *
 */
public interface ITrack extends INotifier, Iterable<IRadarDataPacket> {
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
	
	/**
	 * Return an iterator over the states of the track.
	 * 
	 * The iterator starts with the current state and proceeds backwards in history.
	 */
	@Override
	public Iterator<IRadarDataPacket> iterator();
}
