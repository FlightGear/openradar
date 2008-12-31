package de.knewcleus.openradar.tracks;

import de.knewcleus.openradar.notify.INotifier;

/**
 * A track manager is responsible for updating the tracks from data
 * provided by radar data providers.
 * 
 * @see de.knewcleus.openradar.radardata.IRadarDataProvider
 * @see de.knewcleus.openradar.radardata.IRadarData
 * 
 * @author Ralf Gerlich
 */
public interface ITrackManager extends Iterable<ITrack>, INotifier {
}
