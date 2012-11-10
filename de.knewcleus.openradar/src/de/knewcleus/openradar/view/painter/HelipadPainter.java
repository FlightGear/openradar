package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.fgfs.navdata.xplane.Helipad;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.HelipadNumber;
import de.knewcleus.openradar.view.objects.HelipadSymbol;

public class HelipadPainter extends AViewObjectPainter<Helipad>{
    
    public HelipadPainter(IMapViewerAdapter mapViewAdapter, Helipad helipad) {
        super(mapViewAdapter, helipad);

        Font font = new Font("Arial", Font.PLAIN, 9);
        
        
        HelipadSymbol hps = new HelipadSymbol(helipad, 0, 15);
        viewObjectList.add(hps);

//        NDBSymbol ndbSymbol = new NDBSymbol();
//        viewObjectList.add(ndbSymbol);
        
        HelipadNumber hpn = new HelipadNumber(helipad, font, Color.lightGray, 0 , 10);
        viewObjectList.add(hpn);
    }
}
