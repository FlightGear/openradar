/**
 * Copyright (C) 2015 Wolfram Wagner
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
package de.knewcleus.openradar.gui.flightplan;

public class SquawkCode implements Comparable<SquawkCode>{

    private final String value;
    
    public SquawkCode(String value) {
        if(!checkValue(value)) {
            throw new IllegalArgumentException("Value "+value+" is not a valid squawk code!");
        }
        this.value=value;
    }
    
    public static boolean checkValue(String v) {
        return v!=null && v.matches("[0-7]{4}") ;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj==null || !(obj instanceof SquawkCode)) return false;
        
        return ((SquawkCode)obj).value.equals(value);
    }
    
    @Override
    public int compareTo(SquawkCode o) {
        Integer iValue = Integer.parseInt(value);
        Integer iOtherValue = Integer.parseInt(o.value);
        
        return iValue.compareTo(iOtherValue);
    }
}
