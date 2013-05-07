/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.Helipad;
import de.knewcleus.fgfs.navdata.xplane.RawFrequency;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.status.radio.Radio;
import de.knewcleus.openradar.gui.status.radio.RadioFrequency;
import de.knewcleus.openradar.gui.status.runways.GuiRunway;
import de.knewcleus.openradar.view.glasspane.StPView;
import de.knewcleus.openradar.view.navdata.INavPointListener;

/**
 * This class stores information about Navaids, Runways for use in frontend
 *
 * @author Wolfram Wagner
 */
public class AirportData implements INavPointListener {

    // private final static long
    // APPLICATION_START_TIME_MILLIS=System.currentTimeMillis();

    private String airportCode = null;
    private String name = null;
    private String sectorDir = null;
    private volatile Point2D towerPosition = null;
    /** given in METER */
    private double elevation = 0f;
    private double magneticDeclination = 0f;

    private List<RadioFrequency> radioFrequencies = new ArrayList<RadioFrequency>();
    private Map<String, Radio> radios = new TreeMap<String, Radio>();
    public Map<String, GuiRunway> runways = new TreeMap<String, GuiRunway>();

    public enum FgComMode {
        Internal, External, Off
    };

    private FgComMode fgComMode = FgComMode.Internal;
    private String fgComPath = ".";
    private String fgComExec = "fgcom";
    private String fgComServer = "delta384.server4you.de";
    private String fgComHost = "localhost";
    private List<Integer> fgComPorts = null;
    private String mpServer = "mpserver01.flightgear.org";
    private int mpServerPort = 5000;
    private int mpLocalPort = 5001; // should be different from default, because
                                    // flightgear want to use this port
    private String metarUrl = "http://weather.noaa.gov/pub/data/observations/metar/stations/";
    private String metarSource = null;

    private volatile String callSign = null;

    private Map<String, Boolean> toggleObjectsMap = new HashMap<String, Boolean>();
    private Map<String, Boolean> visibleLayerMap;

    private NavaidDB navaidDB = new NavaidDB();
    private StPView directionMessageView;

    private final static Logger log = Logger.getLogger(AirportData.class.toString());

    public AirportData() {
    }

    public synchronized void setDirectionMessageView(StPView dmv) {
        this.directionMessageView = dmv;
    }

    public synchronized StPView getDirectionMessageView() {
        return directionMessageView;
    }
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
        return towerPosition;
    }

    public void setAirportPosition(Point2D airportPosition) {
        this.towerPosition = airportPosition;
    }

    public double getLon() {
        return towerPosition.getX();
    }

    public double getLat() {
        return towerPosition.getY();
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getElevationFt() {
        return elevation / Units.FT;
    }

    public double getElevationM() {
        return elevation;
    }

    public double getMagneticDeclination() {
        return magneticDeclination;
    }

    public void setMagneticDeclination(double magneticDeclination) {
        this.magneticDeclination = magneticDeclination;
    }

    public synchronized String getCallSign() {
        return callSign;
    }

    public synchronized void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public Map<String, Radio> getRadios() {
        return radios;
    }

    public List<RadioFrequency> getRadioFrequencies() {
        return radioFrequencies;
    }

    public Map<String, GuiRunway> getRunways() {
        return runways;
    }

    public String getModel() {
        return "OpenRadar";
    }

    public FgComMode getFgComMode() {
        return fgComMode;
    }

    public void setFgComMode(FgComMode fgComMode) {
        this.fgComMode = fgComMode;
    }

    public String getFgComPath() {
        return fgComPath;
    }

    public void setFgComPath(String fgComPath) {
        this.fgComPath = fgComPath;
    }

    public String getFgComExec() {
        return fgComExec;
    }

    public void setFgComExec(String fgComExec) {
        this.fgComExec = fgComExec;
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
        for (int port : fgComPorts) {
            if (sFgComPorts.length() > 0)
                sFgComPorts.append(",");
            sFgComPorts.append(port);
        }
        return sFgComPorts.toString();
    }

    public void setFgComPorts(List<Integer> fgComPorts) {
        this.fgComPorts = fgComPorts;
        radios.clear();
        int i = 0;
        if (fgComPorts != null) {
            for (int fgComPort : fgComPorts) {
                String code = "COM" + i++;
                radios.put(code, new Radio(code, fgComHost, fgComPort));
            }
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

    public String getMetarSource() {
        return metarSource;
    }

    public void setMetarSource(String metarSource) {
        this.metarSource = metarSource;
    }

    /**
     * This method is called when the navdata files are read. We use it to
     * gather additional information
     */
    @Override
    public void navPointAdded(INavPoint point) {
        if (point instanceof Aerodrome) {
            Aerodrome aerodrome = (Aerodrome) point;
            if (aerodrome.getIdentification().equals(getAirportCode())) {

                checkTowerPosition(aerodrome.getTowerPosition());
                this.elevation = aerodrome.getElevation();
                this.name = aerodrome.getName();

                // load fgcom phonebook
                List<RawFrequency> frequencies = SetupController.loadRadioFrequencies(getAirportCode());
                for (RawFrequency f : frequencies) {
                    RadioFrequency rf = new RadioFrequency(f.getCode(), f.getFrequency());
                    this.radioFrequencies.add(rf);
                }
                // air to air
                this.radioFrequencies.add(new RadioFrequency("Air2Air1", "122.75"));
                this.radioFrequencies.add(new RadioFrequency("Air2Air2", "123.45"));
                this.radioFrequencies.add(new RadioFrequency("TestFgCom", "910.00"));
            }

        } else if (point instanceof RunwayEnd) {
            RunwayEnd rw = (RunwayEnd) point;
            if (rw.getRunway().getAirportID().equals(getAirportCode())) {
                // runway for this airport
                // System.out.println(rw);
                runways.put(rw.getRunwayID(), new GuiRunway(this, rw));
            }
        } else if (point instanceof Helipad) {
            Helipad hp = (Helipad) point;
            if (hp.getAirportID().equals(getAirportCode())) {
                // runway for this airport
                // System.out.println(hp);
                // runways.put(hp.getRunwayID(),new GuiRunway(hp));
            }
        } else if (point instanceof Glideslope) {
            Glideslope gs = (Glideslope) point;
            if (gs.getAirportID().equals(getAirportCode())) {
                String runwayNumber = gs.getRunwayID();
                if (runways.containsKey(runwayNumber)) {
                    runways.get(runwayNumber).addILS(gs);
                } else {
                    System.out.println("Warning found glidescope for non existent runway: " + gs);
                }
            }
        } else if (point instanceof MarkerBeacon) {
            MarkerBeacon mb = (MarkerBeacon) point;
            if (mb.getAirportID().equals(getAirportCode())) {
                String runwayNumber = mb.getRunwayID();
                if (runways.containsKey(runwayNumber)) {
                    mb.setRunwayEnd(runways.get(runwayNumber).getRunwayEnd());
                } else {
                    System.out.println("Warning found MarkerBeacon for non existent runway: " + mb);
                }
            }
        } else {
            // System.out.println("" + point.getClass());
        }

    }

    /**
     * The tower position is initially loaded from xplane files, but saved in
     * sector.properties file to allow easy correction.
     * In this method we check if the value is already in sectors.properties file and
     * save it if not...
     *
     * @param towerPosition2
     */
    private void checkTowerPosition(Point2D towerPos) {
        Properties p = SetupController.loadSectorProperties(getAirportCode());
        try {
            if(p.getProperty("tower.lat")!=null && p.getProperty("tower.lon")!=null) {
                double lon = Double.parseDouble(p.getProperty("tower.lon", ""));
                double lat = Double.parseDouble(p.getProperty("tower.lat", ""));
                // value found and parsed
                this.towerPosition = new Point2D.Double(lon, lat);
            } else {
                // values not found yet
                p.put("tower.lon", Double.toString(towerPos.getX()));
                p.put("tower.lat", Double.toString(towerPos.getY()));
                SetupController.saveSectorProperties(getAirportCode(), p);
                this.towerPosition= towerPos;
            }
        } catch(Exception e) {
            log.log(Level.SEVERE, "Error: could not parse tower position in file sectors.properties for airport "+airportCode);
            this.towerPosition= towerPos;
        }

    }

    public String getAirportDir() {
        if (sectorDir == null) {
            sectorDir = "data" + File.separator + airportCode + File.separator;
        }
        return sectorDir;
    }

    public String getInitialATCCallSign() {
        return getAirportCode() + "_TW";
    }

    public RunwayData getRunwayData(String runwayCode) {
        if (runways.get(runwayCode) == null)
            return null;
        return runways.get(runwayCode).getRunwayData();
    }

    public void setRadarObjectFilter(GuiMasterController master, String objectName) {
        boolean oldState = toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : true;
        toggleObjectsMap.put(objectName, !oldState);
        storeAirportData(master);
    }

    public void changeToggle(GuiMasterController master, String objectName, boolean defaultValue) {
        boolean oldState = toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : defaultValue;
        toggleObjectsMap.put(objectName, !oldState);
        storeAirportData(master);
    }

    public boolean getRadarObjectFilterState(String objectName) {
        return toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : true;
    }

    public boolean getToggleState(String objectName, boolean defaultValue) {
        return toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : defaultValue;
    }

    public void setVisibleLayerMap(Map<String, Boolean> visibleLayerMap) {
        this.visibleLayerMap=visibleLayerMap;
    }

    public boolean isLayerVisible(String name) {
        return visibleLayerMap.get(name) != null ? visibleLayerMap.get(name) : false;
    }

    public void loadAirportData(GuiMasterController master) {
        File propertyFile = new File("settings" + File.separator + getAirportCode() + ".properties");
        if (propertyFile.exists()) {
            Properties p = new Properties();
            try {
                p.load(new FileReader(propertyFile));
                callSign = p.getProperty("lastCallsign");
                if(callSign==null) {
                    callSign = getInitialATCCallSign();
                }
                // restore runwaydata
                for (GuiRunway rw : runways.values()) {
                    rw.getRunwayData().setValuesFromProperties(p);
                }
                // restore zoomlevel values
                master.getRadarBackend().setZoomLevelValuesFromProperties(p);

                // restore toggles
                Enumeration<?> e = p.propertyNames();
                while (e.hasMoreElements()) {
                    String name = (String) e.nextElement();
                    if (name.startsWith("toggle.")) {
                        String objKey = name.substring(7);
                        boolean b = !"false".equals(p.getProperty(name));
                        toggleObjectsMap.put(objKey, b);
                    }
                }

                // restore saved selected frequencies

                for (Radio r : radios.values()) {
                    String savedFrequency = p.getProperty("radio." + r.getKey());
                    // check if frequency is known
                    if (savedFrequency != null) {
                        r.setRestoredFrequency(savedFrequency);
                    }
                }

            } catch (IOException e) {
            }
        }
    }

    public void storeAirportData(GuiMasterController master) {
        Properties p = new Properties();
        if(master.getCurrentATCCallSign()!=null) {
            p.setProperty("lastCallsign", master.getCurrentATCCallSign());
        }
        // add runway data
        for (GuiRunway rw : runways.values()) {
            rw.getRunwayData().addValuesToProperties(p);
        }
        // add zoom levels and centers
        master.getRadarBackend().addZoomLevelValuesToProperties(p);

        // add toggles
        for (String objKey : toggleObjectsMap.keySet()) {
            p.setProperty("toggle." + objKey, (toggleObjectsMap.get(objKey) ? "true" : "false"));
        }

        // add selected radio frequencies
        master.getRadioManager().addSelectedFrequenciesTo(p);

        File propertiesFile = new File("settings" + File.separator + getAirportCode() + ".properties");

        FileWriter writer = null;
        try {
            if (propertiesFile.exists())
                propertiesFile.delete();
            writer = new FileWriter(propertiesFile);

            p.store(writer, "Open Radar Airport Properties");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }


    public NavaidDB getNavaidDB() {
        return navaidDB;
    }

    public void updateMouseRadarMoved(GuiRadarContact contact, MouseEvent e) {
        directionMessageView.updateMouseRadarMoved(contact,e);

    }
}
