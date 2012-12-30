package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.openradar.view.groundnet.TaxiSign;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.TaxiSignObject;

public class TaxiSignPainter extends AViewObjectPainter<TaxiSign> {

    public TaxiSignPainter(IMapViewerAdapter mapViewAdapter, TaxiSign sign) {
            super(mapViewAdapter, sign);
            
            Font font = new Font("Arial", Font.PLAIN, 9);

            TaxiSignObject s = new TaxiSignObject(sign, font, Color.white, 0, 15);
            viewObjectList.add(s);
    }

}
