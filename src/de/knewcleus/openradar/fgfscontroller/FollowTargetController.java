/**
 * Copyright (C) 2015-2016 Wolfram Wagner
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
package de.knewcleus.openradar.fgfscontroller;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.flightgear.fgfsclient.FGFSConnection;

import de.knewcleus.fgfs.location.GeoUtil;
import de.knewcleus.fgfs.location.GeoUtil.GeoUtilInfo;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

public class FollowTargetController implements Runnable {

    private volatile boolean active = false;
    private Thread thread = null;
    private final GuiMasterController master;
    private final FGFSController fgfsController;
    private volatile double fov = 0;
    /** used to switch back to preset only one time, otherwise it would do this reset all the time, making the camera un-movable */
    private boolean contactInRange = false; 

    private Logger log = Logger.getLogger(FollowTargetController.class);

    public FollowTargetController(GuiMasterController master, FGFSController fgfsController) {
        this.master = master;
        this.fgfsController = fgfsController;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (isActive() && fgfsController.isOnline()) {
                    focusOnTarget();
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                fgfsController.pointCameraIntoPresetHeading();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    private void focusOnTarget() {
        if(!fgfsController.isOnline()) {
            return;
        }
        
        String camFollowCallsign = fgfsController.getFollowContact();
        GuiRadarContact selectedContact;
        if (camFollowCallsign == null) {
            selectedContact = master.getRadarContactManager().getSelectedContact();
        } else {
            selectedContact = master.getRadarContactManager().getContactFor(camFollowCallsign);
        }

        if (selectedContact != null && selectedContact.isActive() ) {

            Point2D playerPos = selectedContact.getCenterGeoCoordinates();
            Position cameraPos = fgfsController.getCameraPosition();

            GeoUtilInfo info = GeoUtil.getDistance(cameraPos.getX(), cameraPos.getY(), playerPos.getX(), playerPos.getY());
            double distance = info.length; // distance betweeen camera and contact in meters

            if (distance < 8000) {
                String callsign = selectedContact.getCallSign();

                FGFSConnection con;
                try {
                    con = fgfsController.getFgfsConnection();

                    // determine internal id of contact
                    // int numPlayers = con.getInt("/ai/models/num-players");
                    int numPlayers = (int) con.getDouble("/ai/models/num-players");

                    if (numPlayers > 0) {
                        int id = -1;
                        int mpIndex = -1;
                        if (callsign.equals(con.get("/ai/models/multiplayer/callsign"))) {
                            id = con.getInt("/ai/models/multiplayer/id");
                            mpIndex = 0;
                        } else {
                            for (int i = 1; i <= numPlayers; i++) {
                                if (callsign.equals(con.get("/ai/models/multiplayer[" + i + "]/callsign"))) {
                                    id = con.getInt("/ai/models/multiplayer[" + i + "]/id");
                                    mpIndex = i;
                                    break;
                                }
                            }
                        }
                        if (id > -1) {
                            // ATC1
                            con.setInt("/sim/atc/target-number", mpIndex);
                            con.setBoolean("/sim/atc/tracking", true);

                        } else {
                            con.setBoolean("/sim/atc/tracking", false);
                        }
                    }
                } catch (IOException e) {
                    log.error("Problem to follow contact " + callsign+" (telnet to OrCam) Cause: "+e.getMessage());
                }

                // double heading = info.angle - master.getAirportData().getMagneticDeclination();
                // double pitch = Math.toDegrees(Math.tan((playerPos.getZ()-cameraPos.getZ())/distance2));
                if (fov == 0) {
                    fov = 2 * Math.toDegrees(Math.tan(75 / distance));
                    fov = fov < 1 ? 1 : fov;
                    fgfsController.setCameraZoom(fov);
                }
                contactInRange = true;
            } else {
                // too far away
                if(contactInRange) {
                    contactInRange=false;
                    fgfsController.pointCameraIntoPresetHeading();
                }
            }
        } else {
            // no contact selected turn to preset
            fgfsController.pointCameraIntoPresetHeading();
        }
    }
    
    public synchronized void setActive(boolean b) {
        this.active = b;
        fov = 0;
        //contactInRange = true;
        if (!b) {
            thread.interrupt();
            //fgfsController.pointCameraIntoPresetHeading();
        }
    }

    public synchronized boolean isActive() {
        return active;
    }
}
