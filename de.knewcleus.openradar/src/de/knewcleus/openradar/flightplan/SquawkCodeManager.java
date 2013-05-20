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
package de.knewcleus.openradar.flightplan;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;

public class SquawkCodeManager {

    private int squawkFrom = 2000;
    private int squawkTo = 6000;
    private volatile int lastSquawkCode = squawkFrom-1;
    private Set<Integer> usedCodes = new HashSet<Integer>();
//    private AirportData data;

    public SquawkCodeManager(AirportData data) {
//        this.data = data;
    }

    public synchronized int getSquawkFrom() {
        return squawkFrom;
    }

    public synchronized int getSquawkTo() {
        return squawkTo;
    }

    public synchronized void setSquawkRange(int squawkFrom, int squawkTo) {
        this.squawkFrom = squawkFrom;
        this.squawkTo = squawkTo;
        this.lastSquawkCode=squawkFrom-1;
    }

    public synchronized Integer getNextSquawkCode(Integer formerCode) {
        if(formerCode!=null) {
            usedCodes.remove(formerCode);
        }
        int nextSquawkCode = getNexCode(lastSquawkCode);
        while(usedCodes.contains(nextSquawkCode)) {
            if(nextSquawkCode==lastSquawkCode) {
                return null; // all in use
            }
            nextSquawkCode = getNexCode(nextSquawkCode);
        }
        usedCodes.add(nextSquawkCode);
        lastSquawkCode=nextSquawkCode;
        return nextSquawkCode;
    }

    private int getNexCode(int lastcode) {
        int nextCode = lastcode +1;
        if(nextCode > squawkTo) {
            nextCode = squawkFrom;
        }
        return nextCode;
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
            } else if(c.getTranspSquawkCode()!=null && c.getTranspSquawkCode()>=squawkFrom && c.getTranspSquawkCode()<=squawkTo) {
                // unassigned, but transmitted codes in range
                usedCodes.add(c.getAssignedSquawk());
            }
        }
    }

    public synchronized void addSquawkRangeTo(Properties p) {
        p.setProperty("squawk.first", ""+squawkFrom);
        p.setProperty("squawk.last", ""+squawkTo);
    }

    public synchronized void restoreSquawkRangeFrom(Properties p) {
        squawkFrom = Integer.parseInt(p.getProperty("squawk.first",""+squawkFrom));
        squawkTo = Integer.parseInt(p.getProperty("squawk.last",""+squawkTo));
    }

    public synchronized void revokeSquawkCode(Integer assignedSquawk) {
        // called when an ATC revokes a squawk code. The code stays in the list of used codes, until it is actually gone...
    }

}
