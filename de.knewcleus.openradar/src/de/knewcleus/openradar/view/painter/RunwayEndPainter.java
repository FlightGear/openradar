package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.RunwayEndIlsCone;
import de.knewcleus.openradar.view.objects.RunwayEndMarker;
import de.knewcleus.openradar.view.objects.RunwayEndNumber;

public class RunwayEndPainter extends AViewObjectPainter<RunwayEnd> {

    
    public RunwayEndPainter(IMapViewerAdapter mapViewAdapter, AirportData data, RunwayEnd runwayEnd) {
        super(mapViewAdapter, runwayEnd);
        
        Font font = new Font("Arial", Font.PLAIN, 9);
        
        RunwayEndIlsCone ils = new RunwayEndIlsCone(data,runwayEnd);
        viewObjectList.add(ils);
        
        RunwayEndMarker runwayEndMarker = new RunwayEndMarker(runwayEnd);
        viewObjectList.add(runwayEndMarker);

//        RunwayEndCenterLine runwayEndCenterLine = new RunwayEndCenterLine(runwayEnd, 100d);
//        viewObjectList.add(runwayEndCenterLine);

        RunwayEndNumber runwayEndNumber = new RunwayEndNumber(runwayEnd, font, Color.lightGray, 0 , 50);
        viewObjectList.add(runwayEndNumber);
        
    }

}
