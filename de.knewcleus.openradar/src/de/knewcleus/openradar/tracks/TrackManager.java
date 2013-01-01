/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.knewcleus.openradar.notify.Notifier;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.IRadarDataProvider;
import de.knewcleus.openradar.radardata.IRadarDataRecipient;
import de.knewcleus.openradar.tracks.TrackLifetimeNotification.LifetimeState;

public class TrackManager extends Notifier implements ITrackManager, IRadarDataRecipient {
	protected final Map<Object, Track> trackMap=new HashMap<Object, Track>();
	/**
	 * The length of the time in milliseconds to wait between the last signal and
	 * the assumption of loss of target.
	 */
	protected long lossOfTrackTimeoutMsecs = 10 * 1000;
	
	/**
	 * The length of the time in milliseconds to wait between the last signal and
	 * the retirement of the target.
	 */
	protected long trackRetirementTimeoutMsecs = 15*1000; // 30 * 60 * 1000;

	@Override
	public void acceptRadarData(IRadarDataProvider provider, IRadarDataPacket radarData) {
		final Object trackIdentifier = radarData.getTrackingIdentifier();
		final Track track;
		final boolean isNewTrack = !trackMap.containsKey(trackIdentifier);
		if (isNewTrack) {
			track = new Track();
			trackMap.put(trackIdentifier, track);
		} else {
			track = trackMap.get(trackIdentifier);
		}
		
		track.addState(radarData);
		if (radarData.wasSeenOnLastScan()) {
			track.setLost(false);
		}
		track.setLastUpdateTimestamp(System.currentTimeMillis());
		
		if (isNewTrack) {
			notify(new TrackLifetimeNotification(this, track, LifetimeState.CREATED));
		}
	}
	
	/**
	 * Check for target loss or track retirement.
	 * 
	 * This method should be called on regular basis.
	 */
	public void checkForLossOrRetirement() {
		final Set<Track> retiredTracks = new HashSet<Track>();
		final Iterator<Track> trackIterator = trackMap.values().iterator();
		final long currentTime = System.currentTimeMillis();
		
		while (trackIterator.hasNext()) {
			final Track track = trackIterator.next();
			final long trackAge = currentTime - track.getLastUpdateTimestamp();
			if (trackAge > trackRetirementTimeoutMsecs) {
				/* retire track */
				retiredTracks.add(track);
				trackIterator.remove();
			} else if (trackAge > lossOfTrackTimeoutMsecs) {
				/* consider track lost */
				track.setLost(true);
			}
		}
		
		for (Track track: retiredTracks) {
			notify(new TrackLifetimeNotification(this, track, LifetimeState.RETIRED));
		}
	}
	
	@Override
	public Iterator<ITrack> iterator() {
		return new TrackIterator();
	}
	
	protected class TrackIterator implements Iterator<ITrack> {
		protected final Iterator<Track> parentIterator = trackMap.values().iterator();

		public boolean hasNext() {
			return parentIterator.hasNext();
		}

		public Track next() {
			return parentIterator.next();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
