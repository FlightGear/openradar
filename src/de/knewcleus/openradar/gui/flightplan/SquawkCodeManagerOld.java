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
package de.knewcleus.openradar.gui.flightplan;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;

public class SquawkCodeManagerOld {

    private int ifrCode = 2000;  
    private int vfrCode = 1200; // Europe, US 7000
    private int squawkFromVFR = 2001;
    private int squawkToVFR = 2777;
    private int squawkFromIFR = 4000;
    private int squawkToIFR = 4777;
    private volatile int lastSquawkCodeVFR = squawkFromVFR-1;
    private volatile int lastSquawkCodeIFR = squawkFromIFR-1;
    private Set<Integer> usedCodes = new HashSet<Integer>();
    
//    private AirportData data;

    public SquawkCodeManagerOld(AirportData data) {
//        this.data = data;
    }

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

    public synchronized int getSquawkFromVFR() {
        return squawkFromVFR;
    }

    public synchronized int getSquawkToVFR() {
        return squawkToVFR;
    }

    public synchronized int getSquawkFromIFR() {
        return squawkFromIFR;
    }

    public synchronized int getSquawkToIFR() {
        return squawkToIFR;
    }

    public synchronized void setSquawkRange(int squawkFromVFR, int squawkToVFR, int squawkFromIFR, int squawkToIFR) {
        this.squawkFromVFR = squawkFromVFR;
        this.squawkToVFR = squawkToVFR;
        this.squawkFromIFR = squawkFromIFR;
        this.squawkToIFR = squawkToIFR;
        this.lastSquawkCodeVFR=squawkFromVFR-1;
        this.lastSquawkCodeIFR=squawkFromIFR-1;
    }

    public synchronized Integer getNextVFRSquawkCode(Integer formerCode) {
        if(formerCode!=null) {
            usedCodes.remove(formerCode);
        }
        int nextSquawkCode = getNexCode(lastSquawkCodeVFR, true);
        while(usedCodes.contains(nextSquawkCode)) {
            if(nextSquawkCode==lastSquawkCodeVFR) {
                return null; // all in use
            }
            nextSquawkCode = getNexCode(nextSquawkCode, true);
        }
        usedCodes.add(nextSquawkCode);
        lastSquawkCodeVFR=nextSquawkCode;
        return nextSquawkCode;
    }

    public synchronized Integer getNextIFRSquawkCode(Integer formerCode) {
        if(formerCode!=null) {
            usedCodes.remove(formerCode);
        }
        int nextSquawkCode = getNexCode(lastSquawkCodeIFR, false);
        while(usedCodes.contains(nextSquawkCode)) {
            if(nextSquawkCode==lastSquawkCodeIFR) {
                return null; // all in use
            }
            nextSquawkCode = getNexCode(nextSquawkCode, false);
        }
        usedCodes.add(nextSquawkCode);
        lastSquawkCodeIFR=nextSquawkCode;
        return nextSquawkCode;
    }

    private int getNexCode(int lastcode, boolean vfrMode) {
        int nextCode = lastcode +1;
        int lastDigit = nextCode % 10;
        if(lastDigit>7) {
            nextCode = nextCode + 10-lastDigit;
        }
        if(vfrMode) {
            if(nextCode > squawkToVFR) {
                nextCode = squawkFromVFR;
            }
        } else {
            // ifr
            if(nextCode > squawkToIFR) {
                nextCode = squawkFromIFR;
            }
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
            } else if(c.getTranspSquawkCode()!=null 
                    && (( c.getTranspSquawkCode()>=squawkFromVFR && c.getTranspSquawkCode()<=squawkToVFR) 
                       || (c.getTranspSquawkCode()>=squawkFromIFR && c.getTranspSquawkCode()<=squawkToIFR) ) ) {
                // unassigned, but transmitted codes in range
                usedCodes.add(c.getAssignedSquawk());
            }
        }
    }

    public synchronized void addSquawkRangeTo(Properties p) {
        p.setProperty("squawk.ifrCode",""+ifrCode);
        p.setProperty("squawk.vfrCode",""+vfrCode);
        p.setProperty("squawk.vfr.first", ""+squawkFromVFR);
        p.setProperty("squawk.vfr.last", ""+squawkToVFR);
        p.setProperty("squawk.ifr.first", ""+squawkFromIFR);
        p.setProperty("squawk.ifr.last", ""+squawkToIFR);
    }

    public synchronized void restoreSquawkRangeFrom(Properties p) {
        ifrCode = Integer.parseInt(p.getProperty("squawk.ifrCode","2000"));
        vfrCode = Integer.parseInt(p.getProperty("squawk.vfrCode","1200"));
        squawkFromVFR = Integer.parseInt(p.getProperty("squawk.vfr.first",""+squawkFromVFR));
        squawkToVFR = Integer.parseInt(p.getProperty("squawk.vfr.last",""+squawkToVFR));
        squawkFromIFR = Integer.parseInt(p.getProperty("squawk.ifr.first",""+squawkFromIFR));
        squawkToIFR = Integer.parseInt(p.getProperty("squawk.ifr.last",""+squawkToIFR));
    }

    public synchronized void revokeSquawkCode(Integer assignedSquawk) {
        // called when an ATC revokes a squawk code. The code stays in the list of used codes, until it is actually gone...
    }

}
