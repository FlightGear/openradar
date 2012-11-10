package de.knewcleus.openradar.view.painter;

import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.IlsMarkerBeacon;

public class MarkerBeaconPainter extends AViewObjectPainter<MarkerBeacon> {

    public MarkerBeaconPainter(IMapViewerAdapter mapViewAdapter, MarkerBeacon markerBeacon) {
        super(mapViewAdapter, markerBeacon);
        
        IlsMarkerBeacon mb = new IlsMarkerBeacon(markerBeacon);
        viewObjectList.add(mb);
//
//      Font font = Palette.BEACON_FONT;
//        MarkerBeaconName mbn = new MarkerBeaconName(markerBeacon, font, Color.lightGray, 0 , 32);
//        viewObjectList.add(mbn);
    }
}
