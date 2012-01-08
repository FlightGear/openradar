package cz.muni.fi.openradar.vessels.hla;

import de.knewcleus.openradar.vessels.IPositionDataProvider;
import de.knewcleus.openradar.vessels.IPositionUpdateListener;
import de.knewcleus.openradar.vessels.PositionUpdate;
import java.util.HashSet;
import java.util.Set;

public class HlaRegistry implements IPositionDataProvider {

    protected final Set<IPositionUpdateListener> listeners = new HashSet<IPositionUpdateListener>();
    AsnDataFetcher updater = new AsnDataFetcher(this);

    public HlaRegistry() {
        updater.start();
    }

    protected void fireRadarDataUpdated(Set<PositionUpdate> targets) {
        for (IPositionUpdateListener consumer : listeners) {
            consumer.targetDataUpdated(targets);
        }
    }

    @Override
    public synchronized void registerPositionUpdateListener(IPositionUpdateListener consumer) {
        listeners.add(consumer);
    }

    @Override
    public synchronized void unregisterPositionUpdateListener(IPositionUpdateListener consumer) {
        listeners.remove(consumer);
    }
}
