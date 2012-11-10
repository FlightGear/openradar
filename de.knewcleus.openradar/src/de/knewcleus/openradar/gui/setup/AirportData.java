package de.knewcleus.openradar.gui.setup;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.Helipad;
import de.knewcleus.fgfs.navdata.xplane.RawFrequency;
import de.knewcleus.openradar.gui.status.radio.Radio;
import de.knewcleus.openradar.gui.status.radio.RadioFrequency;
import de.knewcleus.openradar.gui.status.runways.GuiRunway;
import de.knewcleus.openradar.view.navdata.INavPointListener;

/**
 * This class stores information about Navaids, Runways for use in frontend
 * 
 * @author Wolfram Wagner
 */
public class AirportData implements INavPointListener {


    private String airportCode = null;
    private String name = null;
    private String sectorDir = null;
    private Point2D airportPosition = null;
    private double elevation = 0f;

    private List<RadioFrequency> radioFrequencies = new ArrayList<RadioFrequency>();
    private Map<String, Radio> radios = new TreeMap<String, Radio>();
    public Map<String, GuiRunway> runways = new TreeMap<String, GuiRunway>();

    private String fgComPath = "/home/wolfram/bin/fgcomgui-win32-bundle-01192010/wine fgcom";
    private String fgComServer = "delta384.server4you.de";
    private String fgComHost = "localhost";
    private  List<Integer> fgComPorts = null;
    private String mpServer = "mpserver01.flightgear.org";
    private int mpServerPort = 5000;
    private int mpLocalPort = 5001; // should be different from default, because flightgear want to use this port
    private String metarUrl = "http://weather.noaa.gov/pub/data/observations/metar/stations/";
    
    public AirportData() {}
    
    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAirportName() {
        return name;
    }

    public void setAirportName(String name) {
        this.name = name;
    }

    public Point2D getAirportPosition() {
        return airportPosition;
    }

    public void setAirportPosition(Point2D airportPosition) {
        this.airportPosition=airportPosition;
    }
 
    public double getLon() {
        return airportPosition.getX();
    }

    public double getLat() {
        return airportPosition.getY();
    }

    public void setElevation(double elevation) {
        this.elevation=elevation;
    }
    
    public double getElevation() {
        return elevation;
    }
    
    public Map<String, Radio> getRadios() {
        return radios;
    }

    public List<RadioFrequency> getRadioFrequencies() {
        return radioFrequencies;
    }

    public Map<String,GuiRunway> getRunways() {
        return runways;
    }
    
    public String getModel() {
        return "OpenRadar";
    }

    public String getFgComPath() {
        return fgComPath; 
    }

    public void setFgComPath(String fgComPath) {
        this.fgComPath = fgComPath;
    }

    public String getFgComHost() {
        return fgComHost;
    }

    public void setFgComHost(String fgComHost) {
        this.fgComHost = fgComHost;
    }

    public String getFgComServer() {
        return fgComServer;
    }

    public void setFgComServer(String fgComServer) {
        this.fgComServer = fgComServer;
    }

    public String getFgComPorts() {
        StringBuilder sFgComPorts = new StringBuilder();  
        for(int port : fgComPorts) {
            if(sFgComPorts.length()>0) sFgComPorts.append(",");
            sFgComPorts.append(port);
        }
        return sFgComPorts.toString();
    }

    public void setFgComPorts(List<Integer> fgComPorts) {
        this.fgComPorts = fgComPorts;
        radios.clear();
        int i=0;
        for(int fgComPort : fgComPorts) {
            String code = "COM"+i++;
            radios.put(code, new Radio(code, fgComHost, fgComPort));
        }
    }

    public String getMpServer() {
        return mpServer;
    }

    public void setMpServer(String mpServer) {
        this.mpServer = mpServer;
    }

    public int getMpServerPort() {
        return mpServerPort;
    }

    public void setMpServerPort(int mpServerPort) {
        this.mpServerPort = mpServerPort;
    }

    public int getMpLocalPort() {
        return mpLocalPort;
    }

    public void setMpLocalPort(int mpLocalPort) {
        this.mpLocalPort = mpLocalPort;
    }

    public String getMetarUrl() {
        return metarUrl;
    }

    public void setMetarUrl(String metarUrl) {
        this.metarUrl = metarUrl;
    }
    
    // NavPointListener

    /**
     * This method is called when the navdata files are read.
     * We use it to gather additional information
     */
    @Override
    public void navPointAdded(INavPoint point) {
        if (point instanceof Aerodrome) {
            Aerodrome aerodrome = (Aerodrome) point;
            if (aerodrome.getIdentification().equals(getAirportCode())) {
                airportPosition = aerodrome.getGeographicPosition();
                this.elevation = aerodrome.getElevation();
                this.name = aerodrome.getName();
                
                for (RawFrequency f : aerodrome.getFrequencies()) {
                    this.radioFrequencies.add(new RadioFrequency(f.getCode(), f.getFrequency()));
                }
                // air to air
                this.radioFrequencies.add(new RadioFrequency("Air2Air1", "122.75"));
                this.radioFrequencies.add(new RadioFrequency("Air2Air2", "123.45"));
            }

        } else if (point instanceof RunwayEnd) {
            RunwayEnd rw = (RunwayEnd) point;
            if (rw.getRunway().getAirportID().equals(getAirportCode())) {
                // runway for this airport
                // System.out.println(rw);
                runways.put(rw.getRunwayID(), new GuiRunway(rw));
            }
        } else if (point instanceof Helipad) {
            Helipad hp = (Helipad) point;
            if (hp.getAirportID().equals(getAirportCode())) {
                // runway for this airport
                //System.out.println(hp);
                // runways.put(hp.getRunwayID(),new GuiRunway(hp));
            }
        } else if (point instanceof Glideslope) {
            Glideslope gs = (Glideslope)point;
            if (gs.getAirportID().equals(getAirportCode())) {
                String runwayNumber = gs.getRunwayID();
                if(runways.containsKey(runwayNumber)) {
                    runways.get(runwayNumber).addILS(gs);
                } else {
                    System.out.println("Warning found glidescope for non existent runway: "+gs);
                }
            }
        } else if (point instanceof MarkerBeacon) {
            MarkerBeacon mb = (MarkerBeacon)point;
            if (mb.getAirportID().equals(getAirportCode())) {
                String runwayNumber = mb.getRunwayID();
                if(runways.containsKey(runwayNumber)) {
                    mb.setRunwayEnd(runways.get(runwayNumber).getRunwayEnd());
                } else {
                    System.out.println("Warning found glidescope for non existent runway: "+mb);
                }
            }
        } else {
            // System.out.println("" + point.getClass());
        }

    }

    public String getAirportDir() {
        if(sectorDir==null) {
            sectorDir = "sectors"+File.separator+airportCode+File.separator;
        }
        return sectorDir;
    }

    public String getInitialATCCallSign() {
        return getAirportCode()+"_TW";
    }
}
