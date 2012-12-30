package de.knewcleus.openradar.view.painter;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.DistanceCircle;

public class AtcObjectsPainter extends AViewObjectPainter<AirportData> {

    public AtcObjectsPainter(IMapViewerAdapter mapViewAdapter, AirportData data) {
        super(mapViewAdapter, data);
        
        DistanceCircle line = new DistanceCircle(data,DistanceCircle.Style.MINOR , 5, 0, 150);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN , 10, 0, 150);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.MINOR , 15, 0, 150);
        viewObjectList.add(line);
        
        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN , 20, 0, 500);
        viewObjectList.add(line);
        
        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN, 40, 0, 500);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.IMPORTANT, 60, 0, Integer.MAX_VALUE);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN, 80, 0, 500);
        viewObjectList.add(line);

        line = new DistanceCircle(data,DistanceCircle.Style.PLAIN, 100, 0, 500);
        viewObjectList.add(line);
}    
    
}
