/**
 * Copyright (C) 2013 Wolfram Wagner 
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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.setup;

import java.util.Map;
import java.util.TreeMap;

import de.knewcleus.fgfs.navdata.model.IIntersection;

public class NavaidDB {
    private volatile IIntersection selectedNavaid = null; 
    private final Map<String, IIntersection> navaidMap = new TreeMap<String, IIntersection>();

    public synchronized void registerNavaid(IIntersection navPoint) {
        navaidMap.put(navPoint.getIdentification().toUpperCase(), navPoint);
    }
    
    public synchronized IIntersection getNavaid(String id) {
        return navaidMap.get(id);
    }

    // highlighting
    public synchronized IIntersection highlight(String id) {
        IIntersection navPoint = navaidMap.get(id);
        if(navPoint!=null) {
            navPoint.setHighlighted(true);
        }
        return navPoint;
    }

    public synchronized void resetHighlighting() {
        for(IIntersection navPoint : navaidMap.values()) {
            navPoint.setHighlighted(false);
        }
    }
    
    
    // navaid selection
    
    public synchronized boolean isNavaidSelected(String id) {
        return selectedNavaid!=null && selectedNavaid.getIdentification().equalsIgnoreCase(id);
    }

    public synchronized void selectNavaid(String id) {
        selectedNavaid=navaidMap.get(id);
    }
}