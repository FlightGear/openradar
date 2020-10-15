/**
 * Copyright (C) 2012-2016 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.setup;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

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
import de.knewcleus.openradar.gui.flightplan.SquawkCodeManagerOld;
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

    public static final Rectangle MAX_WINDOW_SIZE;
    // private final static long
    // APPLICATION_START_TIME_MILLIS=System.currentTimeMillis();

    private String airportCode = null;
    private String name = null;
    private String sectorDir = null;
    private volatile Point2D towerPosition = null;
    /** given in METER */
    private double elevation = 0f;
    /** the transition altitude */
    private int transitionAlt = 5000;
    /** the minimum width of the transition layer */
    private int transitionLayerWidth = 500;
    /** the fix transition FL, if defined. If defined disables the FL calculation */
    private boolean manualTransitionLevel = false;
    private Integer transitionLevelFix;
    /** the value of the calculated transition (flight) level */
    private Integer transitionFL = null;
    private double magneticDeclination = 0f;

    private List<RadioFrequency> radioFrequencies = new ArrayList<RadioFrequency>();
    private Map<String, Radio> radios = new TreeMap<String, Radio>();
    private final Map<String, GuiRunway> runways = Collections.synchronizedMap(new TreeMap<String, GuiRunway>());

    private HashSet<String> activeLandingRouteRunways = new HashSet<>();
    private HashSet<String> activeStartingRouteRunways = new HashSet<>();

    public enum FgComMode {
        Auto, Internal, External, Mumble, Off
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

    private boolean altRadioTextEnabled = false;
    private String altRadioText = "";

    private Map<String, Boolean> toggleObjectsMap = new HashMap<String, Boolean>();
    private Map<String, Boolean> visibleLayerMap;

    private NavaidDB navaidDB = new NavaidDB();
    private StPView directionMessageView;

    private boolean fpDownloadEnabled = false;
    private String fpDownloadUrl = "http://lenny64.free.fr/dev2014_01_13.php5?getFlightplans";

    private int contactTailLength = 10;

    private int antennaRotationTime = 1000;

    private final AircraftCodeConverter aircraftCodeConverter = new AircraftCodeConverter();

    private final DatablockLayoutManager datablockLayoutManager = new DatablockLayoutManager(this);

    private final SquawkCodeManagerOld squawkCodeManager = new SquawkCodeManagerOld(this);

    private boolean fgfsCamera1Enabled = false;
    private String fgfsCamera1Host = "localhost";
    private int fgfsCamera1Port = 5010;
    private boolean fgfsLocalMPPacketForward1 = false;
    private int fgfsLocalMPPacketPort1 = 5010;

    private boolean fgfsCamera2Enabled = false;
    private boolean fgfsSlave2To1 = false;
    private String fgfsCamera2Host = "localhost";
    private int fgfsCamera2Port = 5020;
    private boolean fgfsLocalMPPacketForward2 = false;
    private int fgfsLocalMPPacketPort2 = 5020;

    private static Logger log = LogManager.getLogger(AirportData.class);

    static {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        MAX_WINDOW_SIZE = env.getMaximumWindowBounds();
    }

    public AirportData() {
    }

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

    public synchronized boolean isAltRadioTextEnabled() {
        return altRadioTextEnabled;
    }

    public synchronized void setAltRadioTextEnabled(boolean altRadioTextEnabled) {
        this.altRadioTextEnabled = altRadioTextEnabled;
    }

    public synchronized String getAltRadioText() {
        return altRadioText;
    }

    public synchronized void setAltRadioText(String altRadioText) {
        this.altRadioText = altRadioText;
    }

    /**
     * This method is called when the navdata files are read. We use it to gather additional information
     */
    @Override
    public synchronized void navPointAdded(INavPoint point) {
        if (point instanceof Aerodrome) {
            Aerodrome aerodrome = (Aerodrome) point;
            if (aerodrome.getIdentification().equals(getAirportCode())) {
                if (aerodrome.getTowerPosition() != null) {
                    checkTowerPosition(aerodrome.getTowerPosition());
                } else {
                    // some airports have no specified tower position
                    checkTowerPosition(aerodrome.getGeographicPosition());
                }
                this.elevation = aerodrome.getElevation();
                this.name = aerodrome.getName();

                // load fgcom phonebook
                Set<RawFrequency> frequencies;
                boolean includeFgCom = getFgComMode() != FgComMode.Off;
                if (includeFgCom) {
                    // fgcom3
                    frequencies = SetupController.loadRadioFrequenciesFgCom3(this, getAirportCode()); // fgcom 3

                    for (RawFrequency f : frequencies) {
                        RadioFrequency rf = new RadioFrequency(f.getCode(), f.getFrequency());
                        this.radioFrequencies.add(rf);
                    }
                    // air to air
                    this.radioFrequencies.add(new RadioFrequency("Air2Air1", "122.75"));
                    this.radioFrequencies.add(new RadioFrequency("Air2Air2", "123.45"));
                    this.radioFrequencies.add(new RadioFrequency("TestFgCom", "910.00"));
                }
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
     * The tower position is initially loaded from xplane files, but saved in sector.properties file to allow easy
     * correction. In this method we check if the value is already in sectors.properties file and save it if not...
     *
     * @param towerPosition2
     */
    private void checkTowerPosition(Point2D towerPos) {
        Properties p = SetupController.loadSectorProperties(getAirportCode());
        try {
            if (p.getProperty("tower.lat") != null && p.getProperty("tower.lon") != null) {
                double lon = Double.parseDouble(p.getProperty("tower.lon", ""));
                double lat = Double.parseDouble(p.getProperty("tower.lat", ""));
                // value found and parsed
                this.towerPosition = new Point2D.Double(lon, lat);
            } else {
                // values not found yet
                p.put("tower.lon", Double.toString(towerPos.getX()));
                p.put("tower.lat", Double.toString(towerPos.getY()));
                SetupController.saveSectorProperties(getAirportCode(), p);
                this.towerPosition = towerPos;
            }
        } catch (Exception e) {
            log.fatal("Error: could not parse tower position in file sectors.properties for airport " + airportCode);
            this.towerPosition = towerPos;
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

    /** master can be null, but settings will not be saved in this case */
    public synchronized void setRadarObjectFilter(GuiMasterController master, String objectName) {
        boolean oldState = toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : true;
        toggleObjectsMap.put(objectName, !oldState);
        if (master != null) {
            storeAirportData(master);
        }
    }

    /** master can be null, but settings will not be saved in this case */
    public synchronized void changeToggle(GuiMasterController master, String objectName, boolean defaultValue) {
        boolean oldState = toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : defaultValue;
        toggleObjectsMap.put(objectName, !oldState);
        if (master != null) {
            storeAirportData(master);
        }
    }

    /** master can be null, but settings will not be saved in this case */
    public synchronized void setToggle(String objectName, boolean value) {
        toggleObjectsMap.put(objectName, value);
    }

    public synchronized boolean getRadarObjectFilterState(String objectName) {
        return toggleObjectsMap.get(objectName) != null ? toggleObjectsMap.get(objectName) : true;
    }

    public synchronized boolean getToggleState(String objectName, boolean defaultValue) {
        if (toggleObjectsMap.get(objectName) != null) {
            return toggleObjectsMap.get(objectName);
        } else {
            toggleObjectsMap.put(objectName, defaultValue);
            return defaultValue;
        }
    }

    public synchronized void setVisibleLayerMap(Map<String, Boolean> visibleLayerMap) {
        this.visibleLayerMap = visibleLayerMap;
    }

    public synchronized boolean isLayerVisible(String name) {
        return visibleLayerMap.get(name) != null ? visibleLayerMap.get(name) : false;
    }

    public synchronized void loadLastCallSign() {
        Properties p = new Properties();
        File propertyFile = new File("settings" + File.separator + getAirportCode() + ".properties");
        try {
            p.load(new FileReader(propertyFile));
        } catch (IOException e) {
        }

        callSign = p.getProperty("lastCallsign");
        if (callSign == null) {
            callSign = getInitialATCCallSign();
        }
    }

    public synchronized void loadAirportData(GuiMasterController master) {

        Properties p = new Properties();
        File propertyFile = new File("settings" + File.separator + getAirportCode() + ".properties");
        try {
            p.load(new FileReader(propertyFile));
        } catch (IOException e) {
        }

        if (callSign == null) {
            // for some reason the callsign was not set in setup dialog
            callSign = p.getProperty("lastCallsign");
            if (callSign == null) {
                // pilot has never been here
                callSign = getInitialATCCallSign();
            }
        }

        String sTA = p.getProperty("transitionAlt");
        if (sTA != null) {
            transitionAlt = Integer.parseInt(sTA);
        } else {
            transitionAlt = getInitialTransitionAlt();
        }

        // metar
        MetarReader metarReader = master.getMetarReader();
        metarSource = p.getProperty("metarSource");
        if (metarSource == null) {
            metarSource = "_" + getAirportCode(); // The underscore marks it as initial setting
        }
        addMetarSources = p.getProperty("addMetarSources");
        metarReader.changeMetarSources(metarSource, addMetarSources);

        if (propertyFile.exists()) {

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

            // restore alternative radio data
            altRadioText = p.getProperty("altRadioText.text", "");

            contactTailLength = Integer.parseInt(p.getProperty("contact.tailLength", "10"));

            antennaRotationTime = Integer.parseInt(p.getProperty("antennaRotationTime", "1000"));

            master.getFgfsController1().loadData(p);
        }
        // calculate magnetic declination
        setMagneticDeclination(CoreMag.calc_magvarDeg(getLat(), getLon(), getElevationM(), System.currentTimeMillis()));

    }

    public void restoreRunwaySettings() {
        Properties p = new Properties();
        File propertyFile = new File("settings" + File.separator + getAirportCode() + ".properties");
        try {
            p.load(new FileReader(propertyFile));
        } catch (IOException e) {
        }

        if (propertyFile.exists()) {
            // restore runwaydata
            for (GuiRunway rw : runways.values()) {
                rw.getRunwayData().setValuesFromProperties(p);
            }
        }
    }

    private int getInitialTransitionAlt() {
        if (towerPosition.getX() < -13) {
            // america
            return 8000;
        } else {
            return Math.max(5000, (int) (elevation / Units.FT + 3000));
        }
    }

    public synchronized void storeAirportData(GuiMasterController master) {
        Properties p = new Properties();
        if (master.getCurrentATCCallSign() != null) {
            p.setProperty("lastCallsign", master.getCurrentATCCallSign());
        }

        p.setProperty("transitionAlt", "" + transitionAlt);

        if (metarSource != null) {
            p.setProperty("metarSource", metarSource);
        }
        if (addMetarSources != null) {
            p.setProperty("addMetarSources", addMetarSources);
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

        // add alternative radio data
        p.setProperty("altRadioText.text", altRadioText);

        squawkCodeManager.addSquawkRangeTo(p);

        p.setProperty("contact.tailLength", "" + contactTailLength);

        p.setProperty("antennaRotationTime", "" + antennaRotationTime);

        master.getFgfsController1().storeData(p);

        File propertiesFile = new File("settings" + File.separator + getAirportCode() + ".properties");

        FileWriter writer = null;
        try {
            if (propertiesFile.exists())
                propertiesFile.delete();
            writer = new FileWriter(propertiesFile);

            p.store(writer, "Open Radar Airport Properties");
        } catch (IOException e) {
            log.error("Error while storing airport properties!", e);
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
        directionMessageView.updateMouseRadarMoved(contact, e);

    }

    public synchronized AircraftCodeConverter getAircraftCodeConverter() {
        return aircraftCodeConverter;
    }

    public synchronized DatablockLayoutManager getDatablockLayoutManager() {
        return datablockLayoutManager;
    }

    public synchronized SquawkCodeManagerOld getSquawkCodeManager() {
        return squawkCodeManager;
    }

    public void updateRunwayModel(DefaultComboBoxModel<String> cbModel, boolean addEmptyEntry) {
        synchronized (cbModel) {
            cbModel.removeAllElements();
            if (addEmptyEntry) {
                cbModel.addElement("");
            }
            for (GuiRunway rw : runways.values()) {
                if (rw.getRunwayData().isEnabledAtAll()) {
                    if (rw.isLandingActive() || rw.isStartingActive()) {
                        cbModel.addElement(rw.getCode());
                    }
                }
            }
        }
    }

//    public void updateGeneralArrivalRunwayModel(DefaultComboBoxModel<String> cbModel, boolean addEmptyEntry) {
//        synchronized (cbModel) {
//            cbModel.removeAllElements();
//            if (addEmptyEntry) {
//                cbModel.addElement("");
//            }
//            for (GuiRunway rw : runways.values()) {
//                if (rw.getRunwayData().isLandingEnabled()) {
//                    cbModel.addElement(rw.getCode());
//                }
//            }
//        }
//    }

    public synchronized HashSet<String> getActiveRunways() {
        HashSet<String> activeRWs = new HashSet<String>();
        for (GuiRunway rw : runways.values()) {
            if (rw.isLandingActive() || rw.isStartingActive()) {
                activeRWs.add(rw.getCode());
            }
        }
        return activeRWs;
    }

    @Override
    public String toString() {
        return "AirportData: " + getAirportCode();
    }

    public synchronized int getTransitionAlt() {
        return transitionAlt;
    }

    public synchronized void setTransitionAlt(GuiMasterController master, int transitionAlt) {
        this.transitionAlt = transitionAlt;
        updateTransitionFl(master);
    }

    public synchronized int getTransitionLayerWidth() {
        return transitionLayerWidth;
    }

    public synchronized void setTransitionLayerWidth(int transitionLayerWidth) {
        this.transitionLayerWidth = transitionLayerWidth;
    }

    public synchronized boolean isManualTransitionLevel() {
        return manualTransitionLevel;
    }

    public synchronized void setManualTransitionLevel(boolean manualTransitionLevel) {
        this.manualTransitionLevel = manualTransitionLevel;
    }

    public synchronized Integer getTransitionLevelFix() {
        return transitionLevelFix;
    }

    public synchronized void setTransitionLevelFix(Integer transitionLevelFix) {
        this.transitionLevelFix = transitionLevelFix;
    }

    public synchronized void updateTransitionFl(GuiMasterController master) {
        // initial value
        if (transitionLevelFix == null && master.getAirportMetar().getPressureHPa() > 0) { // first call comes before
                                                                                           // metar is loaded...
            this.transitionLevelFix = ((int) Math.ceil((transitionAlt + 27 * (1013 - master.getAirportMetar().getPressureHPa())) / transitionLayerWidth) + 1)
                    * transitionLayerWidth / 100;
        }

        if (!manualTransitionLevel) {
            // calculate it
            this.transitionFL = ((int) Math.ceil((transitionAlt + 27 * (1013 - master.getAirportMetar().getPressureHPa())) / transitionLayerWidth) + 1)
                    * transitionLayerWidth / 100;
        } else {
            // manual case
            this.transitionFL = transitionLevelFix;
        }
    }

    public synchronized int getTransitionFL(GuiMasterController master) {
        if (transitionFL == null) {
            updateTransitionFl(master);
        }
        return transitionFL;
    }

    public synchronized boolean isFpDownloadEnabled() {
        return fpDownloadEnabled;
    }

    public synchronized void setFpDownloadEnabled(boolean fpDownloadEnabled) {
        this.fpDownloadEnabled = fpDownloadEnabled;
    }

    public synchronized String getFpDownloadUrl() {
        return fpDownloadUrl;
    }

    public synchronized void setFpDownloadUrl(String fpDownloadUrl) {
        this.fpDownloadUrl = fpDownloadUrl;
    }

    public synchronized int getContactTailLength() {
        return contactTailLength;
    }

    public synchronized void setContactTailLength(int contactTailLength) {
        this.contactTailLength = contactTailLength;
    }

    public synchronized int getAntennaRotationTime() {
        return antennaRotationTime;
    }

    public synchronized void setAntennaRotationTime(int antennaRotationTime) {
        this.antennaRotationTime = antennaRotationTime;
    }

    public synchronized boolean isFgfsCamera1Enabled() {
        return fgfsCamera1Enabled;
    }

    public synchronized String getFgfsCamera1Host() {
        return fgfsCamera1Host;
    }

    public synchronized void setFgfsCamera1Host(String fgfsCameraHost) {
        this.fgfsCamera1Host = fgfsCameraHost;
    }

    public synchronized int getFgfsCamera1Port() {
        return fgfsCamera1Port;
    }

    public synchronized void setFgfsCamera1Port(int fgfsCameraPort) {
        this.fgfsCamera1Port = fgfsCameraPort;
    }

    public synchronized void setFgfsCamera1Enabled(boolean fgfsCameraEnabled) {
        this.fgfsCamera1Enabled = fgfsCameraEnabled;
    }

    public synchronized boolean isFgfsLocalMPPacketForward1() {
        return fgfsLocalMPPacketForward1;
    }

    public synchronized void setFgfsLocalMPPacketForward1(boolean fgfsLocalMPPacketForward) {
        this.fgfsLocalMPPacketForward1 = fgfsLocalMPPacketForward;
    }

    public synchronized int getFgfsLocalMPPacketPort1() {
        return fgfsLocalMPPacketPort1;
    }

    public synchronized void setFgfsLocalMPPacketPort1(int fgfsLocalMPPacketPort) {
        this.fgfsLocalMPPacketPort1 = fgfsLocalMPPacketPort;
    }

    public synchronized boolean isFgfsCamera2Enabled() {
        return fgfsCamera2Enabled;
    }

    public synchronized boolean isFgfsSlave2To1() {
        return fgfsSlave2To1;
    }

    public synchronized void setFgfsSlave2To1(boolean fgfsSlave2To1) {
        this.fgfsSlave2To1 = fgfsSlave2To1;
    }

    public synchronized String getFgfsCamera2Host() {
        return fgfsCamera2Host;
    }

    public synchronized void setFgfsCamera2Host(String fgfsCameraHost) {
        this.fgfsCamera2Host = fgfsCameraHost;
    }

    public synchronized int getFgfsCamera2Port() {
        return fgfsCamera2Port;
    }

    public synchronized void setFgfsCamera2Port(int fgfsCameraPort) {
        this.fgfsCamera2Port = fgfsCameraPort;
    }

    public synchronized void setFgfsCamera2Enabled(boolean fgfsCameraEnabled) {
        this.fgfsCamera2Enabled = fgfsCameraEnabled;
    }

    public synchronized boolean isFgfsLocalMPPacketForward2() {
        return fgfsLocalMPPacketForward2;
    }

    public synchronized void setFgfsLocalMPPacketForward2(boolean fgfsLocalMPPacketForward) {
        this.fgfsLocalMPPacketForward2 = fgfsLocalMPPacketForward;
    }

    public synchronized int getFgfsLocalMPPacketPort2() {
        return fgfsLocalMPPacketPort2;
    }

    public synchronized void setFgfsLocalMPPacketPort2(int fgfsLocalMPPacketPort) {
        this.fgfsLocalMPPacketPort2 = fgfsLocalMPPacketPort;
    }

    public void refreshRunwayDefinitions() {
        synchronized (activeLandingRouteRunways) {
            activeLandingRouteRunways.clear();
            for (GuiRunway rw : runways.values()) {
                if (rw.isLandingActive() && rw.isLandingRouteEnabled()) {
                    activeLandingRouteRunways.add(rw.getCode());
                }
            }
        }
        synchronized (activeStartingRouteRunways) {
            activeStartingRouteRunways.clear();
            for (GuiRunway rw : runways.values()) {
                if (rw.isStartingActive() && rw.isStartRouteEnabled()) {
                    activeStartingRouteRunways.add(rw.getCode());
                }
            }
        }
        
        navaidDB.refreshRouteVisibility();
    }

    public boolean isActiveRouteRunwayContained(Collection<String> routeStartingSettings, Collection<String> routeLandingSettings) {
        synchronized (activeLandingRouteRunways) {
            for (String code : activeLandingRouteRunways) {
                if (routeLandingSettings.contains(code)) {
                    return true;
                }
            }
        }

        synchronized (activeStartingRouteRunways) {
            for (String code : activeStartingRouteRunways) {
                if (routeStartingSettings.contains(code)) {
                    return true;
                }
            }
        }
        return false;
    }

}
