/**
 * Copyright (C) 2014 Wolfram Wagner
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

import java.util.Properties;

import de.knewcleus.openradar.rpvd.contact.ContactShape;
import de.knewcleus.openradar.rpvd.contact.ContactShape.Symbol;

public class SquawkRange {

    private String name;
    private volatile int squawkFirst;
    private volatile int squawkLast;
    private volatile int lastIssued;
    private ContactShape.Symbol symbol = Symbol.Asterix;

    public SquawkRange(String name, int first, int last, ContactShape.Symbol symbol) {
        this.name=name;
        this.squawkFirst = first;
        this.squawkLast = last;
        this.lastIssued = squawkFirst-1;
        this.symbol = symbol;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized int getSquawkFirst() {
        return squawkFirst;
    }

    public synchronized void setSquawkFirst(int squawkFirst) {
        this.squawkFirst = squawkFirst;
    }

    public synchronized int getSquawkLast() {
        return squawkLast;
    }

    public synchronized void setSquawkLast(int squawkLast) {
        this.squawkLast = squawkLast;
    }

    public synchronized int getLastIssued() {
        return lastIssued;
    }

    public synchronized void setLastIssued(int lastIssued) {
        this.lastIssued = lastIssued;
    }

    public synchronized ContactShape.Symbol getSymbol() {
        return symbol;
    }

    public synchronized void setSymbol(ContactShape.Symbol symbol) {
        this.symbol = symbol;
    }
    
    public synchronized boolean isValid() {
        return inRange(squawkFirst) && inRange(squawkLast);
    }

    private boolean inRange(int s) {
        if(s<0) {
            return false;
        }
        String ss = Integer.toString(s);
        for(int i=0;i<4;i++) {
            int d = Integer.parseInt(ss.substring(i,i+1));
            if(d>7) return false;
        }
        return true;
    }
    
    public synchronized int getNexCode(int lastcode) {
        int nextCode = lastcode +1;
        int lastDigit = nextCode % 10;
        if(lastDigit>7) {
            nextCode = nextCode + 10-lastDigit;
        }
        if(nextCode > squawkLast) {
            nextCode = squawkFirst;
        }
        return nextCode;
    }

    public void addValuesToProperties(Properties p) {
        if(isValid()) {
            p.setProperty("squawk."+name+".first", ""+squawkFirst);
            p.setProperty("squawk."+name+".last", ""+squawkLast);
            p.setProperty("squawk."+name+".symbol", ""+symbol.toString());
        }
    }
}