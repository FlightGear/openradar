/**
 * Copyright (C) 2013-2015 Wolfram Wagner
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
package de.knewcleus.openradar.gui.flightplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.rpvd.contact.ContactShape.Symbol;

public class SquawkCodeManager {

    private Set<Integer> usedCodes = new HashSet<Integer>();
    
    private Map<String, SquawkRange> ranges = Collections.synchronizedMap(new TreeMap<String, SquawkRange>());
//    private AirportData data;
    private int ifrCode = 2000;  
    private int vfrCode = 1200; // Europe, US 7000

    public synchronized int getIfrCode() {
        return ifrCode;
    }

    public synchronized void setIfrCode(int ifrCode) {
        this.ifrCode = ifrCode;
    }

    public synchronized int getVfrCode() {
        return vfrCode;
    }

    public synchronized void setVfrCode(int vfrCode) {
        this.vfrCode = vfrCode;
    }

    public SquawkCodeManager(AirportData data) {
//        this.data = data;
    }

    public synchronized void putSquawkRange(SquawkRange sqRange) {
        ranges.put(sqRange.getName(),sqRange);
    }

    public synchronized void removeSquawkRange(String name) {
        ranges.remove(name);
    }

    public synchronized Integer getNextSquawkCode(String rangeName, Integer formerCode) {
        if(formerCode!=null) {
            usedCodes.remove(formerCode);
        }
        SquawkRange sqRange = ranges.get(rangeName);
        if(sqRange==null) {
            throw new IllegalArgumentException("Error: Squawk range "+rangeName+" not known.");
        }
        int nextSquawkCode = sqRange.getNexCode(sqRange.getLastIssued());
        while(usedCodes.contains(nextSquawkCode)) {
            if(nextSquawkCode==sqRange.getLastIssued()) {
                return null; // all in use
            }
            nextSquawkCode = sqRange.getNexCode(sqRange.getLastIssued());
        }
        usedCodes.add(nextSquawkCode);
        sqRange.setLastIssued(nextSquawkCode);
        return nextSquawkCode;
    }

    /**
     * Updates the list of assigned SquawkCodes to free unused ones to be re-used.
     *
     * @param contactList
     * @return
     */
    public synchronized void updateUsedSquawkCodes(List<GuiRadarContact> contactList) {
        usedCodes.clear();
        for(GuiRadarContact c : contactList) {
            if(c.getAssignedSquawk()!=null) {
                // assigned codes
                usedCodes.add(c.getAssignedSquawk());
            } else {
                if(c.getTranspSquawkCode()!=null && c.getTranspSquawkCode()>0) { // filter out transponder off -9999
                    // unassigned, but transmitted codes in range
                    usedCodes.add(c.getAssignedSquawk());
                }
            }
        }
    }

    public synchronized void addSquawkRangeTo(Properties p) {
        p.setProperty("squawk.ifrCode",""+ifrCode);
        p.setProperty("squawk.v.frCode",""+vfrCode);

        for(SquawkRange sqRange : ranges.values()) {
            sqRange.addValuesToProperties(p);
        }
    }

    public synchronized void restoreSquawkRangeFrom(Properties p) {
        ranges.clear();
        ifrCode = Integer.parseInt(p.getProperty("squawk.ifrCode","2000"));
        vfrCode = Integer.parseInt(p.getProperty("squawk.vfrCode","1200"));
        
        for(Object o : p.keySet() ) {
            String key = (String)o;
            if(key.startsWith("squawk.") && key.contains("first")) {
                // do it only once
                String name = key.substring(7,key.indexOf(".", 7));
                int first = Integer.parseInt(p.getProperty("squawk."+name+".first","2000"));
                int last = Integer.parseInt(p.getProperty("squawk."+name+".last","4000"));
                String symbolName = p.getProperty("squawk."+name+".first",Symbol.Asterix.toString());

                Symbol symbol = Symbol.valueOf(symbolName);
                if(symbol==null) {
                    symbol=Symbol.Asterix;
                }
                
                ranges.put(name, new SquawkRange(name, first, last, symbol));
                
            }
        }
    }

    public synchronized void revokeSquawkCode(Integer assignedSquawk) {
        // called when an ATC revokes a squawk code. The code stays in the list of used codes, until it is actually gone...
    }

    public synchronized List<String> getSqRangeNames() {
        return new ArrayList<String>(ranges.keySet());
    }
}
