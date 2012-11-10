package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.VORFrequency;
import de.knewcleus.openradar.view.objects.VORName;
import de.knewcleus.openradar.view.objects.VORSymbol;
import de.knewcleus.openradar.view.objects.VORSymbol.VORType;

public class VORPainter extends AViewObjectPainter<VOR> {

    
    public VORPainter(IMapViewerAdapter mapViewAdapter, VOR vor) {
        super(mapViewAdapter, vor);
        
        Font font = Palette.BEACON_FONT;
        
        VORSymbol.VORType vorType = VORType.VOR;
        if(vor.getName().contains("DME")) vorType = VORType.VOR_DME;
        else if(vor.getName().contains("TAC")) vorType = VORType.VORTAC;
        
        VORSymbol s = new VORSymbol(vorType);
        viewObjectList.add(s);

        VORName n = new VORName(vor, font, Color.lightGray, 0 , Integer.MAX_VALUE);
        viewObjectList.add(n);

        VORFrequency f = new VORFrequency(vor, font, Color.lightGray, 0 , Integer.MAX_VALUE);
        viewObjectList.add(f);
    }

}
