package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.HoldPoint;
import de.knewcleus.openradar.view.objects.ParkingPosition;
import de.knewcleus.openradar.view.objects.TaxiwayLine;

public class TaxiWayPainter extends AViewObjectPainter<TaxiWaySegment> {

    public TaxiWayPainter(AirportData data, IMapViewerAdapter mapViewAdapter, TaxiWaySegment seg) {
        super(mapViewAdapter, seg);
        
        Font font = new Font("Arial", Font.PLAIN, 9);
        
        TaxiwayLine line = new TaxiwayLine(seg,0,12);
        viewObjectList.add(line);
        
        if(seg.getBegin() instanceof ParkPos &&
                !"Startup Location".equals(((ParkPos)seg.getBegin()).getName()) ) {
            
            ParkingPosition pos = new ParkingPosition(data, seg,(ParkPos)seg.getBegin(),font,Color.lightGray,0,10);
            viewObjectList.add(pos);
        }

        if(!"none,PushBack".contains(seg.getBegin().getHoldPointType())
                && !(seg.getBegin() instanceof ParkPos)) {
            HoldPoint hp = new HoldPoint(seg, seg.getBegin(), 0,10);
            viewObjectList.add(hp);
        }
        
        if(seg.getEnd() instanceof ParkPos &&
                !"Startup Location".equals(((ParkPos)seg.getEnd()).getName()) ) {
            ParkingPosition pos = new ParkingPosition(data,seg,(ParkPos)seg.getEnd(),font,Color.lightGray,0,10);
            viewObjectList.add(pos);
        }

        if(!"none,PushBack".contains(seg.getEnd().getHoldPointType()) 
                && !(seg.getEnd() instanceof ParkPos)) {
            HoldPoint hp = new HoldPoint(seg, seg.getEnd(),0,10);
            viewObjectList.add(hp);
        }
    }    
    
}
