package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.NDBFrequency;
import de.knewcleus.openradar.view.objects.NDBName;
import de.knewcleus.openradar.view.objects.NDBSymbol;

public class NDBPainter extends AViewObjectPainter<NDB> {

    
    public NDBPainter(AirportData data, IMapViewerAdapter mapViewAdapter, NDB ndb) {
        super(mapViewAdapter, ndb);
        
        Font font = Palette.BEACON_FONT;
        
        NDBSymbol s = new NDBSymbol(data, 0 , 200);
        viewObjectList.add(s);

        NDBName n = new NDBName(data, ndb, font, Color.lightGray, 0 , 200);
        viewObjectList.add(n);

        NDBFrequency f = new NDBFrequency(data, ndb, font, Color.lightGray, 0 , 200);
        viewObjectList.add(f);
    }

}
