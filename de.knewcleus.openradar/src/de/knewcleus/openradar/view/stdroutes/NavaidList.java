package de.knewcleus.openradar.view.stdroutes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.fgfs.navdata.model.IIntersection;

public class NavaidList {

    private final Color color;
    private final List<String> navaids = new ArrayList<String>();

    public NavaidList(Color color) {
        this.color=color;
    }

    public Color getColor() {
        return color;
    }

    public synchronized void addNavaid(String navaid) {
        navaids.add(navaid.toUpperCase());
    }
    public synchronized boolean containsNavaid(IIntersection navPoint) {
        String id = navPoint.getIdentification().toUpperCase();
        if(navaids.contains(id)) {
            return true;
        } else {
            if(navPoint instanceof Intersection && navaids.contains("(FIX)"+id)) {
                return true;
            }
            if(navPoint instanceof NDB && navaids.contains("(NDB)"+id)) {
                return true;
            }
            if(navPoint instanceof VOR && navaids.contains("(VOR)"+id)) {
                return true;
            }
        }
        return false;
    }

}
