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
package de.knewcleus.openradar.gui.setup;

import java.awt.geom.Point2D;

/**
 * This bean stores the information about the available/searched/loaded sectors for the setup dialog
 *
 * @author Wolfram Wagner
 */
public class SectorBean {

    private String airportCode;
    private String airportDescription;
    private Point2D position;
    private double magneticDeclination;
    private boolean sectorDownloaded = false;

    public SectorBean(String airportCode, String airportDescription, boolean sectorDownloaded) {
        this.airportCode=airportCode;
        this.airportDescription= airportDescription;
        this.sectorDownloaded = sectorDownloaded;
    }

    public SectorBean(String airportCode, String airportDescription, Point2D position, double magneticDeclination, boolean sectorDownloaded) {
        this(airportCode,airportDescription,sectorDownloaded);
        this.position=position;
        this.magneticDeclination=magneticDeclination;
    }

    public Point2D getPosition() {
        return position;
    }

    public boolean isSectorDownloaded() {
        return sectorDownloaded;
    }

    public String getAirportCode() {
        return airportCode;
    }
    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }
    public String getAirportName() {
        return airportDescription;
    }
    public void setAirportDescription(String airportDescription) {
        this.airportDescription = airportDescription;
    }

    public double getMagneticDeclination() {
        return magneticDeclination;
    }

    public void setMagneticDeclination(double magneticDeclination) {
        this.magneticDeclination = magneticDeclination;
    }
}
