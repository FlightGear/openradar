/**
 * Copyright (C) 2014-2015 Wolfram Wagner 
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
package de.knewcleus.openradar.fgfscontroller;

public class CameraPreset {

    private final String name;
    /** the latitude of the viewpoint of the camera view */
    private double lat;
    /** the longitude of the viewpoint of the camera view */
    private double lon;
    /** the the altitude of the viewpoint of the camera view */
    private double alt;
    /** the heading of the camera view */
    private double viewHeading;
    /** the pitch of the camera view */
    private double viewPitch;
    /** the zoom (field of view in degrees) of the camera view */
    private double viewZoom;
 
    public CameraPreset(String name) {
        this.name=name;
    }

    public synchronized double getLat() {
        return lat;
    }

    public synchronized void setLat(double lat) {
        this.lat = lat;
    }

    public synchronized double getLon() {
        return lon;
    }

    public synchronized void setLon(double lon) {
        this.lon = lon;
    }

    public synchronized double getAlt() {
        return alt;
    }

    public synchronized void setAlt(double alt) {
        this.alt = alt;
    }

    public synchronized double getViewHeading() {
        return viewHeading;
    }

    public synchronized void setViewHeading(double viewHeading) {
        this.viewHeading = viewHeading;
    }

    public synchronized double getViewPitch() {
        return viewPitch;
    }

    public synchronized void setViewPitch(double viewPitch) {
        this.viewPitch = viewPitch;
    }

    public synchronized double getViewZoom() {
        return viewZoom;
    }

    public synchronized void setViewZoom(double viewZoom) {
        this.viewZoom = viewZoom;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setData(double lon, double lat, double alt, double heading, double pitch, double fov) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
        this.viewHeading = heading;
        this.viewPitch = pitch;
        this.viewZoom = fov;
        
    }
}
