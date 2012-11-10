package de.knewcleus.openradar.view.painter;

import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class DummyPainter extends AViewObjectPainter<INavPoint> {

    
    public DummyPainter(IMapViewerAdapter mapViewAdapter, INavPoint iNavPoint) {
        super(mapViewAdapter, iNavPoint);
    }
}
