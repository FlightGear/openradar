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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
import de.knewcleus.openradar.gui.flightplan.SquawkCodeManager;
import de.knewcleus.openradar.gui.status.radio.Radio;
import de.knewcleus.openradar.gui.status.radio.RadioFrequency;
import de.knewcleus.openradar.gui.status.runways.GuiRunway;
import de.knewcleus.openradar.rpvd.contact.DatablockLayoutManager;
import de.knewcleus.openradar.util.CoreMag;
import de.knewcleus.openradar.view.glasspane.StPView;
import de.knewcleus.openradar.view.navdata.INavPointListener;
import de.knewcleus.openradar.weather.MetarReader;

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
    private int transitionAlt = 5000;
    private Integer transitionFL = null;
    private double magneticDeclination = 0f;

    private List<RadioFrequency> radioFrequencies = new ArrayList<RadioFrequency>();
    private Map<String, Radio> radios = new TreeMap<String, Radio>();
    public Map<String, GuiRunway> runways = Collections.synchronizedMap(new TreeMap<String, GuiRunway>());

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
    private boolean fpExchangeEnabled = false;
    private String fpServerUrl = "";
    private String fpServerUser = "";
    private String fpServerPassword = "";
    private String metarUrl = "http://weather.noaa.gov/pub/data/observations/metar/stations/";
    private String metarSource = null;
    private String addMetarSources = null;

    private boolean chatAliasesEnabled = true;
    private String chatAliasPrefix = ".";

    private volatile String callSign = null;

    private Map<String, Boolean> toggleObjectsMap = new HashMap<String, Boolean>();
    private Map<String, Boolean> visibleLayerMap;

    private NavaidDB navaidDB = new NavaidDB();
    private StPView directionMessageView;

    private final AircraftCodeConverter aircraftCodeConverter = new AircraftCodeConverter();

    private final DatablockLayoutManager datablockLayoutManager = new DatablockLayoutManager(this);

    private final SquawkCodeManager squawkCodeManager = new SquawkCodeManager(this);

    private static Logger log = LogManager.getLogger(AirportData.class);

    public AirportData() {}

    public synchronized void setDirectionMessageView(StPView dmv) {
        this.directionMessageView = dmv;
    }

    public synchronized StPView getDirectionMessageView() {
        return directionMessageView;
    }
    public synchronized String getAirportCode() {
        return airportCode;
    }

    public synchronized void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public synchronized String getAirportName() {
        return name;
    }

    public synchronized void setAirportName(String name) {
        this.name = name;
    }

    public synchronized Point2D getAirportPosition() {
        return towerPosition;
    }

    public synchronized void setAirportPosition(Point2D airportPosition) {
        this.towerPosition = airportPosition;
    }

    public synchronized double getLon() {
        return towerPosition.getX();
    }

    public synchronized double getLat() {
        return towerPosition.getY();
    }

    public synchronized void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public synchronized double getElevationFt() {
        return elevation / Units.FT;
    }

    public synchronized double getElevationM() {
        return elevation;
    }

    public synchronized double getMagneticDeclination() {
        return magneticDeclination;
    }

    public synchronized void setMagneticDeclination(double magneticDeclination) {
        this.magneticDeclination = magneticDeclination;
    }

    public synchronized String getCallSign() {
        return callSign;
    }

    public synchronized void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public synchronized Map<String, Radio> getRadios() {
        return radios;
    }

    public synchronized List<RadioFrequency> getRadioFrequencies() {
        return radioFrequencies;
    }

    public synchronized Map<String, GuiRunway> getRunways() {
        return runways;
    }

    public String getModel() {
        return "OpenRadar";
    }

    public synchronized FgComMode getFgComMode() {
        return fgComMode;
    }

    public synchronized void setFgComMode(FgComMode fgComMode) {
        this.fgComMode = fgComMode;
    }

    public synchronized String getFgComPath() {
        return fgComPath;
    }

    public synchronized void setFgComPath(String fgComPath) {
        this.fgComPath = fgComPath;
    }

    public synchronized String getFgComExec() {
        return fgComExec;
    }

    public synchronized void setFgComExec(String fgComExec) {
        this.fgComExec = fgComExec;
    }

    public synchronized String getFgComHost() {
        return fgComHost;
    }

    public synchronized void setFgComHost(String fgComHost) {
        this.fgComHost = fgComHost;
    }

    public synchronized String getFgComServer() {
        return fgComServer;
    }

    public synchronized void setFgComServer(String fgComServer) {
        this.fgComServer = fgComServer;
    }

    public synchronized String getFgComPorts() {
        StringBuilder sFgComPorts = new StringBuilder();
        for (int port : fgComPorts) {
            if (sFgComPorts.length() > 0)
                sFgComPorts.append(",");
            sFgComPorts.append(port);
        }
        return sFgComPorts.toString();
    }

    public synchronized void setFgComPorts(List<Integer> fgComPorts) {
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

    public synchronized String getMpServer() {
        return mpServer;
    }

    public synchronized void setMpServer(String mpServer) {
        this.mpServer = mpServer;
    }

    public synchronized int getMpServerPort() {
        return mpServerPort;
    }

    public synchronized void setMpServerPort(int mpServerPort) {
        this.mpServerPort = mpServerPort;
    }

    public synchronized int getMpLocalPort() {
        return mpLocalPort;
    }

    public synchronized void setMpLocalPort(int mpLocalPort) {
        this.mpLocalPort = mpLocalPort;
    }

    public synchronized boolean isFpExchangeEnabled() {
        return fpExchangeEnabled;
    }

    public synchronized void setFpExchangeEnabled(boolean fpExchangeEnabled) {
        this.fpExchangeEnabled = fpExchangeEnabled;
    }

    public synchronized String getFpServerUrl() {
        return fpServerUrl;
    }

    public synchronized void setFpServerUrl(String fpServerUrl) {
        this.fpServerUrl = fpServerUrl;
    }

    public synchronized String getFpServerUser() {
        return fpServerUser;
    }

    public synchronized void setFpServerUser(String fpServerUser) {
        this.fpServerUser = fpServerUser;
    }

    public synchronized String getFpServerPassword() {
        return fpServerPassword;
    }

    public synchronized void setFpServerPassword(String fpServerPassword) {
        this.fpServerPassword = fpServerPassword;
    }

    public synchronized String getMetarUrl() {
        return metarUrl;
    }

    public synchronized void setMetarUrl(String metarUrl) {
        this.metarUrl = metarUrl;
    }

    // NavPointListener

    public synchronized String getMetarSource() {
        return metarSource;
    }

    public synchronized void setMetarSource(String metarSource) {
        this.metarSource = metarSource;
    }

    public synchronized String getAddMetarSources() {
        return addMetarSources;
    }

    public synchronized void setAddMetarSources(String addMetarSources) {
        this.addMetarSources = addMetarSources;
    }

    public synchronized boolean isChatAliasesEnabled() {
        return chatAliasesEnabled;
    }

    public synchronized void setChatAliasesEnabled(boolean chatAliasesEnabled) {
        this.chatAliasesEnabled = chatAliasesEnabled;
    }

    public synchronized String getChatAliasPrefix() {
        return chatAliasPrefix;
    }

    public synchronized void setChatAliasPrefix(String chatAliasPrefix) {
        this.chatAliasPrefix = chatAliasPrefix;
    }

    /**
     * This method is called when the navdata files are read. We use it to
     * gather additional information
     */
    @Override
    public synchronized void navPointAdded(INavPoint point) {
        if (point instanceof Aerodrome) {
            Aerodrome aerodrome = (Aerodrome) point;
            if (aerodrome.getIdentification().equals(getAirportCode())) {
                if(aerodrome.getTowerPosition()!=null) {
                    checkTowerPosition(aerodrome.getTowerPosition());
                } else {
                    // some airports have no specified tower position
                    checkTowerPosition(aerodrome.getGeographicPosition());
                }
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
                    log.warn("Warning found glidescope for non existent runway: " + gs);
                }
            }
        } else if (point instanceof MarkerBeacon) {
            MarkerBeacon mb = (MarkerBeacon) point;
            if (mb.getAirportID().equals(getAirportCode())) {
                String runwayNumber = mb.getRunwayID();
                if (runways.containsKey(runwayNumber)) {
                    mb.setRunwayEnd(runways.get(runwayNumber).getRunwayEnd());
                } else {
                    log.warn("Warning found MarkerBeacon for non existent runway: " + mb);
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
            log.fatal("Error: could not parse tower position in file sectors.properties for airport "+airportCode);
            this.towerPosition= towerPos;
        }

    }

    public synchronized String getAirportDir() {
        if (sectorDir == null) {
            sectorDir = "data" + File.separator + airportCode + File.separator;
        }
        return sectorDir;
    }

    public synchronized String getInitialATCCallSign() {
        return getAirportCode() + "_TW";
    }

    public synchronized RunwayData getRunwayData(String runwayCode) {
        if (runways.get(runwayCode) == null)
            return null;
        return runways.get(runwayCode).getRunwayData();
    }

    public synchronized void setRadarObjectFilter(GuiMasterController master, String objectName) {
        boolean oldState = toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : true;
        toggleObjectsMap.put(objectName, !oldState);
        storeAirportData(master);
    }

    public synchronized void changeToggle(GuiMasterController master, String objectName, boolean defaultValue) {
        boolean oldState = toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : defaultValue;
        toggleObjectsMap.put(objectName, !oldState);
        storeAirportData(master);
    }

    public synchronized boolean getRadarObjectFilterState(String objectName) {
        return toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : true;
    }

    public synchronized boolean getToggleState(String objectName, boolean defaultValue) {
        if(toggleObjectsMap.get(objectName) != null) {
            return toggleObjectsMap.get(objectName);
        } else {
            toggleObjectsMap.put(objectName, defaultValue);
            return defaultValue;
        }
    }

    public synchronized void setVisibleLayerMap(Map<String, Boolean> visibleLayerMap) {
        this.visibleLayerMap=visibleLayerMap;
    }

    public synchronized boolean isLayerVisible(String name) {
        return visibleLayerMap.get(name) != null ? visibleLayerMap.get(name) : false;
    }

    public synchronized void loadAirportData(GuiMasterController master) {
        Properties p = new Properties();
        File propertyFile = new File("settings" + File.separator + getAirportCode() + ".properties");
        try {
            p.load(new FileReader(propertyFile));
        } catch (IOException e) {
        }

        callSign = p.getProperty("lastCallsign");
        if(callSign==null) {
            callSign = getInitialATCCallSign();
        }
        
        String sTA = p.getProperty("transitionAlt");
        if(sTA!=null) {
            transitionAlt = Integer.parseInt( sTA );
        } else {
            transitionAlt =  getInitialTransitionAlt();
        }
        
        // metar
        MetarReader metarReader = master.getMetarReader();
        metarSource = p.getProperty("metarSource");
        if(metarSource==null) {
            metarSource = "_"+getAirportCode(); // The underscore marks it as initial setting
        }
        addMetarSources = p.getProperty("addMetarSources");
        metarReader.changeMetarSources(metarSource, addMetarSources);

        if (propertyFile.exists()) {

            // restore runwaydata
            for (GuiRunway rw : runways.values()) {
                rw.getRunwayData().setValuesFromProperties(p);
            }
            // restore zoomlevel values
            master.getRadarBackend().setZoomLevelValuesFromProperties(p);
            // restore layout
            datablockLayoutManager.restoreSelectedLayoutFrom(master, p);

            squawkCodeManager.restoreSquawkRangeFrom(p);
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
        }
        // calculate magnetic declination
        setMagneticDeclination(CoreMag.calc_magvarDeg(getLat(), getLon(), getElevationM(), System.currentTimeMillis()));
    }

    private int getInitialTransitionAlt() {
        if(towerPosition.getX()<-13) {
            // america
            return 8000;
        } else {
            return Math.max(5000, (int)(elevation/Units.FT+3000));
        }
    }

    public synchronized void storeAirportData(GuiMasterController master) {
        Properties p = new Properties();
        if(master.getCurrentATCCallSign()!=null) {
            p.setProperty("lastCallsign", master.getCurrentATCCallSign());
        }
        
        p.setProperty("transitionAlt",""+transitionAlt);
        
        if(metarSource!=null) {
            p.setProperty("metarSource",metarSource);
        }
        if(addMetarSources !=null) {
            p.setProperty("addMetarSources",addMetarSources);
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

        // add layout
        datablockLayoutManager.addSelectedLayoutTo(p);

        squawkCodeManager.addSquawkRangeTo(p);

        File propertiesFile = new File("settings" + File.separator + getAirportCode() + ".properties");

        FileWriter writer = null;
        try {
            if (propertiesFile.exists())
                propertiesFile.delete();
            writer = new FileWriter(propertiesFile);

            p.store(writer, "Open Radar Airport Properties");
        } catch (IOException e) {
            log.error("Error while storing airport properties!",e);
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

    public synchronized AircraftCodeConverter getAircraftCodeConverter() {
        return aircraftCodeConverter;
    }

    public synchronized DatablockLayoutManager getDatablockLayoutManager() {
        return datablockLayoutManager;
    }

    public synchronized SquawkCodeManager getSquawkCodeManager() {
        return squawkCodeManager;
    }

    public ComboBoxModel<String> getRunwayModel(boolean addEmptyEntry) {
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
        if(addEmptyEntry) {
            cbModel.addElement("");
        }
        for(GuiRunway rw : runways.values()) {
            if(rw.getRunwayData().isEnabledAtAll()) {
                cbModel.addElement(rw.getCode());
            }
        }
        return cbModel;
    }

    public ComboBoxModel<String> getGeneralArrivalRunwayModel(boolean addEmptyEntry) {
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
        if(addEmptyEntry) {
            cbModel.addElement("");
        }
        for(GuiRunway rw : runways.values()) {
            if(rw.getRunwayData().isLandingEnabled()) {
                cbModel.addElement(rw.getCode());
            }
        }
        return cbModel;
    }
    
    @Override
    public String toString() {
        return "AirportData: "+getAirportCode();
    }

    public synchronized int getTransitionAlt() {
        return transitionAlt;
    }

    public synchronized void setTransitionAlt(GuiMasterController master, int transitionAlt) {
        this.transitionAlt = transitionAlt;
        updateTransitionFl(master);
    }

    public synchronized void updateTransitionFl(GuiMasterController master) {
        this.transitionFL = ((int)Math.floor((transitionAlt + 30 * (1013 - master.getAirportMetar().getPressureHPa()))/500) + 1) * 5;
    }

    public synchronized int getTransitionFL(GuiMasterController master) {
        if(transitionFL==null) {
            updateTransitionFl(master);
        }
        return transitionFL;
    }

}
