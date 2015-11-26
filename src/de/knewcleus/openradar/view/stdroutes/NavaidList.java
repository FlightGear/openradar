/**
 * Copyright (C) 2013,2015 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.openradar.gui.setup.AdditionalFix;

public class NavaidList {

   protected final StdRouteAttributes attributes;
   private final List<String> navaids = new ArrayList<String>();

    public NavaidList(StdRouteAttributes attributes) {
        this.attributes=attributes;
    }

    public StdRouteAttributes getAttributes() {
        return this.attributes;
    }

    public synchronized void addNavaid(String navaid) {
        navaids.add(navaid.toUpperCase());
    }
    public synchronized boolean containsNavaid(IIntersection navPoint) {
        String id = navPoint.getIdentification().toUpperCase();
        if(navaids.contains(id)) {
            return true;
        } else {
            if(navPoint instanceof AdditionalFix && navaids.contains("(FIX)"+id)) {
                return true;
            }
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
