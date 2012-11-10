package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.FixName;
import de.knewcleus.openradar.view.objects.FixSymbol;

public class IntersectionPainter extends AViewObjectPainter<Intersection> {

    
    public IntersectionPainter(IMapViewerAdapter mapViewAdapter, Intersection fix) {
        super(mapViewAdapter, fix);
        
        if(!fix.getIdentification().matches("R[\\d]{4}")) { // hide runway fixes
            Font font = Palette.BEACON_FONT;
            
            
            FixSymbol fs = new FixSymbol(0,40);
            viewObjectList.add(fs);
    
            FixName fn = new FixName(fix, font, Color.lightGray, 0 , 40);
            viewObjectList.add(fn);
        }
    }

}
