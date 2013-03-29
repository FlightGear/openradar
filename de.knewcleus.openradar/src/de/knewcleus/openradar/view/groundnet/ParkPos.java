/**
 * Copyright (C) 2012 Wolfram Wagner
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
package de.knewcleus.openradar.view.groundnet;

public class ParkPos extends TaxiPoint {

    private String name;
    private String type;
    private String number;
    private double heading;
    private double radius;
    private String displayName;
    private String airlineCodes;

    public ParkPos(String index, String type, String name, String number, String lat, String lon, String heading, String radius, String airlineCodes) {
        super(index,lat,lon,false,"parkPos");
        this.name=name;
        this.type=type;
        this.number= number;
        this.heading=Double.parseDouble(heading);
        this.radius=Double.parseDouble(radius);
        this.airlineCodes=airlineCodes;

        StringBuffer sb = new StringBuffer();
        if(!name.isEmpty() && name.length()<5) {
            sb.append(name);
        }
        if(!number.isEmpty() && number.length()<5) {
//            if(sb.length()>0) {
//                sb.append(" ");
//            }
            sb.append(number);
        }
        displayName=sb.toString();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public double getHeading() {
        return heading;
    }

    public double getRadius() {
        return radius;
    }

    public String getAirlineCodes() {
        return airlineCodes;
    }

    public String getDisplayName() {
        return displayName;
    }

}
