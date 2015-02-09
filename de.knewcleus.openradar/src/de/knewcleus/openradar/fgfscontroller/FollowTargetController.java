/**
 * Copyright (C) 2015 Wolfram Wagner
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
package de.knewcleus.openradar.fgfscontroller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.flightgear.fgfsclient.FGFSConnection;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.GeoUtil;
import de.knewcleus.fgfs.location.GeoUtil.GeoUtilInfo;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;

public class FollowTargetController implements Runnable {

    private volatile boolean active = false;
    private Thread thread = null;
    private final GuiMasterController master;
    private final FGFSController fgfsController;
    private volatile double fov = 35;
    
    private Logger log = Logger.getLogger(FollowTargetController.class);
    
    public FollowTargetController(GuiMasterController master, FGFSController fgfsController) {
        this.master = master;
        this.fgfsController = fgfsController;
        thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        while(true) {
            try{
               if(isActive()) {
                   focusOnTarget();
               }
               Thread.sleep(500);
            } catch (InterruptedException e) {
            } catch (Exception e) {
                log.error(e);;
            }
        }
        
    }

    private void focusOnTarget() {
        String camFollowCallsign = fgfsController.getFollowContact();
        GuiRadarContact selectedContact;
        if(camFollowCallsign==null) {
            selectedContact = master.getRadarContactManager().getSelectedContact();
        } else {
            selectedContact = master.getRadarContactManager().getContactFor(camFollowCallsign);
        }
        
        double distance = selectedContact.getRadarContactDistanceD()*Units.NM; // meters
        if(selectedContact!=null && distance<10000) {
            String callsign = selectedContact.getCallSign();

            FGFSConnection con;
            try {
                con = fgfsController.getFgfsConnection();
                
                // determine internal id of contact
    //                int numPlayers = con.getInt("/ai/models/num-players");
                int numPlayers = (int)con.getDouble("/ai/models/num-players");
                
                if(numPlayers>0) {
                    int id = -1;
                    int mpIndex = -1;
                    if(callsign.equals(con.get("/ai/models/multiplayer/callsign"))) {
                        id = con.getInt("/ai/models/multiplayer/id");
                        mpIndex = 0;
                    } else {
                        for(int i=1;i<numPlayers;i++) {
                            if(callsign.equals(con.get("/ai/models/multiplayer["+i+"]/callsign"))) {
                                id = con.getInt("/ai/models/multiplayer["+i+"]/id");
                                mpIndex = i;
                                break;
                            }
                        }
                    }
                    if(id>-1) {
                        // ATC1
                        con.setInt("/sim/atc/target-number",mpIndex);
                        con.setBoolean("/sim/atc/tracking", true);
                        
                    } else {
                        con.setBoolean("/sim/atc/tracking", false);
                    }
                }
            } catch (IOException e) {
                log.error("Problem to follow contact "+callsign,e);
            }
            
            
//             String callsign = fgfsController.getFollowContact();
            if(callsign==null) return;
            TargetStatus player = master.getRadarProvider().getPlayerRegistry().getPlayer(callsign);  
    
            if(player!=null) {

                Position playerPos = player.getGeodeticPosition();
                Position cameraPos = fgfsController.getCameraPosition();
                
                GeoUtilInfo info = GeoUtil.getDistance(cameraPos.getX(), cameraPos.getY(),playerPos.getX(), playerPos.getY());
                double heading = info.angle - master.getAirportData().getMagneticDeclination();
                double distance2 = info.length;
                double pitch = Math.toDegrees(Math.tan((playerPos.getZ()-cameraPos.getZ())/distance2));
                double newFov = 2 * Math.toDegrees(Math.tan( 100 / distance));
                newFov = newFov<1 ? 1 : newFov;
                if(Math.abs(newFov-fov)>5) {
                    fov = newFov;
                }
                //fov=55;
                fgfsController.pointCameraTo(heading,pitch, fov);
            }
        } else {
            // no contact selected turn to preset
            fgfsController.pointCameraIntoPresetHeading();
        }
    }

    public synchronized void setActive(boolean b) {
        this.active=b;
        if(!b) {
            thread.interrupt();
            fgfsController.pointCameraIntoPresetHeading();
        }
    }
    
    public synchronized boolean isActive() { return active; }
}
