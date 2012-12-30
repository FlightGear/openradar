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
    private String metarSource;
    private Point2D position;
    private double magneticDeclination;
    private boolean sectorDownloaded = false;
    
    public SectorBean(String airportCode, String airportDescription, String metarSource, boolean sectorDownloaded) {
        this.airportCode=airportCode;
        this.airportDescription= airportDescription;
        this.metarSource = metarSource;
        this.sectorDownloaded = sectorDownloaded;
    }
    
    public SectorBean(String airportCode, String airportDescription, String metarSource, Point2D position, double magneticDeclination, boolean sectorDownloaded) {
        this(airportCode,airportDescription,metarSource,sectorDownloaded);
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

    public String getMetarSource() {
        return metarSource;
    }

    public void setMetarSource(String metarSource) {
        this.metarSource = metarSource;
    }

    public double getMagneticDeclination() {
        return magneticDeclination;
    }

    public void setMagneticDeclination(double magneticDeclination) {
        this.magneticDeclination = magneticDeclination;
    }
}
