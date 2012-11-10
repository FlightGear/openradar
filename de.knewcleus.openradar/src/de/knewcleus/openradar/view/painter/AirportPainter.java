package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.AirportCode;

public class AirportPainter extends AViewObjectPainter<Aerodrome> {

    private AirportCode airportCode ;
    
    public AirportPainter(IMapViewerAdapter mapViewAdapter, Aerodrome aerodrome) {
        super(mapViewAdapter, aerodrome);
        
        Font font = new Font("Arial", Font.PLAIN, 9);
        
        String code = aerodrome.getIdentification();
        airportCode = new AirportCode(font, Color.lightGray, code, 32 , 1000);
        
        viewObjectList.add(airportCode);
    }
    
}
