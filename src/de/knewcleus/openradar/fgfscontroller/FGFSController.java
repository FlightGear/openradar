/**
 * Copyright (C) 2015 Wolfram Wagner
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
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.fgfscontroller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.flightgear.fgfsclient.FGFSConnection;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;

public class FGFSController {

    private final GuiMasterController master;
    private final AirportData data;
    private volatile FGFSConnection fgfsConnection;
    private final FollowTargetController followTargetController;
    private volatile CameraPreset currentPreset = null;
    private final static Map<String, CameraPreset> presets = Collections.synchronizedMap(new TreeMap<String, CameraPreset>());
    private volatile String followContact = null;
    private volatile boolean setposActive = false;
    private final String telnetHost;
    private final int telnetPort;

    private CameraPresetControlPanel cameraPresetControlPanel = null;

    private final SetPosMouseListener setPosMouseListener = new SetPosMouseListener();

    private final static Logger log = Logger.getLogger(FGFSController.class);

    private volatile FGFSController slave = null;
    private volatile FGFSController otherInstance = null;
    
    private boolean online = true;
    private volatile long lastCheck = 0; 

    public FGFSController(GuiMasterController master, String telnetHost, int telnetPort) {
        this.master = master;
        this.telnetHost=telnetHost;
        this.telnetPort=telnetPort;
        this.data = master.getAirportData();
        followTargetController = new FollowTargetController(master, this);
    }

    public synchronized boolean isOnline() {
        return online || System.currentTimeMillis() - lastCheck>5000;
    }
    
    public void start() {
        activatePreset("P1");
        pointCameraIntoPresetHeading();
    }

    public void setCameraPresetControlPanel(CameraPresetControlPanel panel) {
        this.cameraPresetControlPanel = panel;
    }

    public synchronized boolean isSetposActive() {
        return setposActive;
    }

    public synchronized void setSetposActive(boolean setposActive) {
        this.setposActive = setposActive;
    }

    public synchronized void startFGFS() {
        // todo read from data and start FGFS with airport, aircraft, name and --telnet +port
    }

    public synchronized void setSlave(FGFSController slave) {
        this.slave = slave;
    }
    public synchronized void setOtherInstance(FGFSController other) {
        this.otherInstance = other;
    }

    public synchronized FGFSConnection getFgfsConnection() throws IOException {
        if(!isOnline()) {
            throw new IOException("Offline, because FGFS telnet not found. Last Check: "+(new Date(lastCheck)));
        }
        
        // todo re-init the FGFSConnection, if it exists, close and re-establish it
        if (fgfsConnection == null || !fgfsConnection.isAlive()) {
            try {
                fgfsConnection = new FGFSConnection(telnetHost, telnetPort);
            } catch(IOException e) {
                online=false;
                lastCheck = System.currentTimeMillis();
                throw e;
            }
            online=true;
            fgfsConnection.setBoolean("/sim/panel/visibility", false);
            fgfsConnection.setDouble("instrumentation/radar/range", 1024); // radar contacts are filtered
            fgfsConnection.setDouble("/position/altitude-ft", (data.getElevationM() + 30) / Units.FT);
        }
        return fgfsConnection;
    }

    /**
     * Reads the current view settings from
     */
    public synchronized void savePreset(String presetName) {
        CameraPreset preset = presets.get(presetName);
        if (preset == null) {
            preset = new CameraPreset(presetName);
        }
        // read values from fgfs and store them in preset
        FGFSConnection con;
        try {
            con = getFgfsConnection();
            double alt = con.getDouble("/position/altitude-ft");
            double lat = con.getDouble("/position/latitude-deg");
            double lon = con.getDouble("/position/longitude-deg");
            double heading = con.getDouble("/sim/current-view/heading-offset-deg");
            double pitch = con.getDouble("/sim/current-view/pitch-offset-deg");
            double fov = con.getDouble("/sim/current-view/field-of-view");

//            if (alt + 10 < data.getElevationFt()) {
//                alt = (data.getElevationM() + 30) / Units.FT;
//                con.setDouble("/position/altitude-ft", alt);
//            }

            preset.setData(lon, lat, alt, heading, pitch, fov);
            presets.put(presetName, preset);

            // save to properties
            savePresetsToProperties();

            currentPreset = preset;
            data.storeAirportData(master);
        } catch (IOException e) {
            log.error("Problem to save data for preset " + presetName + ": " + e.getMessage());
        }
        if(otherInstance!=null) {
            otherInstance.revalidatePreset(presetName);
        }
    }

    /**
     * Activates the preset
     */
    public synchronized void activatePreset(String presetName) {
        CameraPreset preset = presets.get(presetName);
        if (preset == null) {
            preset = getInitialPreset(presetName);
            presets.put(presetName, preset);
        }
        currentPreset = preset;

        if (currentPreset != null) {
            // set preset values in FGFS
            FGFSConnection con;
            try {
                con = getFgfsConnection();
                con.setDouble("/position/altitude-ft", currentPreset.getAlt());
                con.setDouble("/sim/tower/altitude-ft", currentPreset.getAlt());
                con.setDouble("/position/latitude-deg", currentPreset.getLat());
                con.setDouble("/position/longitude-deg", currentPreset.getLon());
                con.setDouble("/sim/tower/latitude-deg", currentPreset.getLat());
                con.setDouble("/sim/tower/longitude-deg", currentPreset.getLon());
                con.setDouble("/sim/current-view/heading-offset-deg", currentPreset.getViewHeading());
                con.setDouble("/sim/current-view/pitch-offset-deg", currentPreset.getViewPitch());
                con.setDouble("/sim/current-view/field-of-view", currentPreset.getViewZoom());
                // manage possibly enabled follow mode in ATC after late startup of fgfs
                followTargetController.setActive(followContact != null);
            } catch (IOException e) {
                log.error("Problem to activate preset " + presetName + ": " + e.getMessage());
            }
        }
        if(slave!=null) {
            slave.activatePreset(presetName);
        }
    }

    private CameraPreset getInitialPreset(String name) {
        CameraPreset preset = new CameraPreset(name);
        preset.setLon(data.getAirportPosition().getX());
        preset.setLat(data.getAirportPosition().getY());
        preset.setAlt((data.getElevationM() + 30) / Units.FT);
        preset.setViewHeading(0);
        preset.setViewPitch(0);
        preset.setViewZoom(55);
        return preset;
    }

    /**
     * Sets the location of the current view
     */
    public synchronized void setLocationOfCurrentView(MouseEvent evt) {
        if (currentPreset != null) {
            Point2D loc = master.getRadarBackend().getGeoLocationOfMouse(evt);
            if (loc != null) {
                currentPreset.setLon(loc.getX());
                currentPreset.setLat(loc.getY());

                // FGFSConnection con;
                // try {
                // con = getFgfsConnection();
                // double alt = con.getDouble("/position/ground-elev-m");
//                double alt = (data.getElevationM() + 30) / Units.FT;
//                currentPreset.setAlt(alt);

                // presets.put(currentPreset.getName(), currentPreset);

                savePresetsToProperties();

                activatePreset(currentPreset.getName());
                // } catch (IOException e) {
                // log.error("Problem to retrieve altitude at new camera location",e);
                // }
            }
            if(otherInstance!=null) {
                otherInstance.revalidatePreset(currentPreset.getName());
            }
        }
        setSetposActive(false);
        cameraPresetControlPanel.disableSetPosInView();
    }

    /** 
     * Called from other instance to tell, that the given preset has changed. If this instance uses this preset, it has to be activated again. 
     */
    private void revalidatePreset(String presetName) {
        if(currentPreset!=null && presetName.equals(currentPreset.getName())) {
            activatePreset(presetName);
        }
        
    }

    private void savePresetsToProperties() {
        // TODO save it to file
    }

    public void setFollow(boolean b) {
        followTargetController.setActive(b);
        if (slave != null) {
            slave.setFollow(b);
        }
    }

    public boolean isFollowing() {
        return followTargetController.isActive();
    }

    /**
     * Activates to follow a contact
     */
    public synchronized void followContact(String contactName) {
        this.followContact = contactName;
        followTargetController.setActive(contactName != null);
        if (followContact == null) {
            pointCameraIntoPresetHeading();
        }
        if(slave!=null) {
            slave.followContact(contactName);
        }
    }

    public synchronized boolean followSelectedContact() {
        if(slave!=null) {
            slave.followSelectedContact();
        }
        GuiRadarContact contact = master.getRadarContactManager().getSelectedContact();
        if (contact != null && contact.isActive() && !contact.getCallSign().equals(followContact)) {
            this.followContact = contact.getCallSign();
            followTargetController.setActive(true);
            return true;
        } else {
            this.followContact = null;
            followTargetController.setActive(false);
            return false;
        }
    }

    public synchronized String getFollowContact() {
        return followContact;
    }

    public synchronized Position getCameraPosition() {
        if (currentPreset != null) {
            return new Position(currentPreset.getLon(), currentPreset.getLat(), currentPreset.getAlt() * Units.FT);
        } else {
            FGFSConnection con;
            try {
                con = getFgfsConnection();
                double alt = con.getDouble("/position/altitude-ft") * Units.FT;
                double lat = con.getDouble("/position/latitude-deg");
                double lon = con.getDouble("/position/longitude-deg");
                con.setBoolean("/sim/atc/tracking", false);

                return new Position(lon, lat, alt);
            } catch (IOException e) {
                log.error("Problem to load camera position from fgfs: " + e.getMessage());
                return new Position(data.getLon(), data.getLat(), data.getElevationM());
            }
        }
    }

    public synchronized void setCameraZoom(double fov) {
        FGFSConnection con;
        try {
            con = getFgfsConnection();
            con.setDouble("/sim/current-view/field-of-view", fov);
        } catch (IOException e) {
            log.error("Problem to move camera to target: " + e.getMessage());
        }
        setSetposActive(false);
    }

    // public synchronized void pointCameraTo(double heading, double pitch, double fov) {
    // FGFSConnection con;
    // try {
    // con = getFgfsConnection();
    // // con.setDouble("/sim/current-view/heading-offset-deg",-1*heading);
    // // con.setDouble("/sim/current-view/pitch-offset-deg",pitch);
    // con.setDouble("/sim/current-view/field-of-view", fov);
    // } catch (IOException e) {
    // log.error("Problem to move camera to target", e);
    // }
    // setSetposActive(false);
    // }

    public synchronized void pointCameraIntoPresetHeading() {
        if (currentPreset != null) {
            FGFSConnection con;
            try {
                con = getFgfsConnection();
                con.setBoolean("/sim/atc/tracking", false);

                con.setDouble("/sim/current-view/heading-offset-deg", currentPreset.getViewHeading());
                con.setDouble("/sim/current-view/pitch-offset-deg", currentPreset.getViewPitch());
                con.setDouble("/sim/current-view/field-of-view", currentPreset.getViewZoom());
            } catch (IOException e) {
                log.error("Problem to move camera to target: " + e.getMessage());
            }
        }
    }

    private class SetPosMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isSetposActive()) {
                setLocationOfCurrentView(e);
            }

        }
    }

    public synchronized SetPosMouseListener getSetPosMouseListener() {
        return setPosMouseListener;
    }

    public void storeData(Properties p) {
        for (CameraPreset pr : presets.values()) {
            p.setProperty("fgfs.camPreset." + pr.getName() + ".lat", "" + pr.getLat());
            p.setProperty("fgfs.camPreset." + pr.getName() + ".lon", "" + pr.getLon());
            p.setProperty("fgfs.camPreset." + pr.getName() + ".alt", "" + pr.getAlt());
            p.setProperty("fgfs.camPreset." + pr.getName() + ".head", "" + pr.getViewHeading());
            p.setProperty("fgfs.camPreset." + pr.getName() + ".pitch", "" + pr.getViewPitch());
            p.setProperty("fgfs.camPreset." + pr.getName() + ".fov", "" + pr.getViewZoom());
        }
    }

    public void loadData(Properties p) {
        try {
            for (int i = 1; i < 10; i++) {
                if (p.getProperty("fgfs.camPreset.P" + i + ".lat") == null) {
                    break;
                }
                CameraPreset pr = new CameraPreset("P" + i);
                pr.setLat(Double.parseDouble(p.getProperty("fgfs.camPreset." + pr.getName() + ".lat")));
                pr.setLon(Double.parseDouble(p.getProperty("fgfs.camPreset." + pr.getName() + ".lon")));
                pr.setAlt(Double.parseDouble(p.getProperty("fgfs.camPreset." + pr.getName() + ".alt")));
                pr.setViewHeading(Double.parseDouble(p.getProperty("fgfs.camPreset." + pr.getName() + ".head")));
                pr.setViewPitch(Double.parseDouble(p.getProperty("fgfs.camPreset." + pr.getName() + ".pitch")));
                pr.setViewZoom(Double.parseDouble(p.getProperty("fgfs.camPreset." + pr.getName() + ".fov")));

                presets.put(pr.getName(), pr);
            }
            if (presets.size() > 0) {
                currentPreset = new ArrayList<CameraPreset>(presets.values()).get(0);
            }
        } catch (Exception e) {
            log.error("Problem while restoring fgfs camera presets: " + e.getMessage());
        }
    }
}