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
    private boolean sectorDownloaded = false;
    
    public SectorBean(String airportCode, String airportDescription, boolean sectorDownloaded) {
        this.airportCode=airportCode;
        this.airportDescription= airportDescription;
        this.sectorDownloaded = sectorDownloaded;
    }
    
    public SectorBean(String airportCode, String airportDescription, Point2D position, boolean sectorDownloaded) {
        this(airportCode,airportDescription,sectorDownloaded);
        this.position=position;
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
}
