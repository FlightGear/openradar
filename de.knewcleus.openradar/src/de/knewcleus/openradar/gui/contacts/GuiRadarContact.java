/**
 * Copyright (C) 2012 Wolfram Wagner 
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
package de.knewcleus.openradar.gui.contacts;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;
import de.knewcleus.openradar.rpvd.RadarTargetView;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.weather.MetarData;

/**
 * This class is a facade in front of the internal radar contact details. 
 * 
 * @author Wolfram Wagner
 *
 */
public class GuiRadarContact {

    public enum State {UNCONTROLLED,CONTROLLED,IMPORTANT};
    public enum Operation {
        GROUND, LANDING, STARTING, TRAVEL, EMERGENCY, UNKNOWN
    };
    
    public enum Alignment {LEFT,CENTER,RIGHT}
    private volatile Alignment alignment = Alignment.RIGHT;
    
    private volatile boolean neglect = false; 

    private RadarContactController manager = null;
    private volatile TargetStatus player = null;
//    
//    private volatile Operation operation = Operation.UNKNOWN;
//    private volatile String frequency = null;
    private volatile boolean onEmergency = false;
    private String aircraft = "";
    private String flightPlan = "";
    private volatile String atcComment = null;
    protected volatile RadarTargetView view;
    protected final double airportElevationFt;
    protected final AirportData airportData;
    protected volatile double lastHeading=-1;
    protected volatile String atcLanguage = "en";
    
    public GuiRadarContact(GuiMasterController master, RadarContactController manager, TargetStatus player, String atcComment) {
        this.manager=manager;
        this.player=player;
        this.airportData = master.getDataRegistry();
        this.airportElevationFt = master.getDataRegistry().getElevationFt();
        this.atcComment = atcComment;
    }
   
    public synchronized void setTargetStatus(TargetStatus player) {
        this.player=player;
    }

    public synchronized boolean isActive() {
        return player.getLastMessageTime() + 5000 > System.currentTimeMillis();
    }
    
    public synchronized boolean isExpired() {
        return player.getLastMessageTime() + 1*60*1000 < System.currentTimeMillis();
    }

    public boolean isNeglect() {
        return neglect;
    }

    public void setNeglect(boolean neglect) {
        this.neglect = neglect;
        if(neglect) {
            setAlignment(Alignment.RIGHT);
        }
    }

    public RadarContactController getManager() {
        return manager;
    }


    public void setManager(RadarContactController manager) {
        this.manager = manager;
    }

//    public Operation getOperation() {
//        if(operation==Operation.UNKNOWN) {
//            if(player.getGeodeticPosition().getZ()<airportElevation+20) {
//                operation=Operation.GROUND;
//            } else
//            if(player.getGeodeticPosition().getZ()<airportElevation+500 && player.getLinearVelocityGlobal().getZ()>5) {
//                operation=Operation.STARTING;
//            } else
//            if(player.getGeodeticPosition().getZ()<airportElevation+500 && player.getLinearVelocityGlobal().getZ()<-5) {
//                operation=Operation.LANDING;
//            } else
//            if(player.getGeodeticPosition().getZ()>airportElevation+500) {
//                operation=Operation.TRAVEL;
//            }
//        }
//        else if(operation==Operation.TRAVEL) {
//            if(player.getGeodeticPosition().getZ()<airportElevation+500 && player.getLinearVelocityGlobal().getZ()<-5) {
//                operation=Operation.LANDING;
//            }
//        }
//        else if(operation==Operation.LANDING) {
//            if(player.getGeodeticPosition().getZ()<airportElevation+20 && player.getGroundSpeed()<40) {
//                operation=Operation.GROUND;
//            } else if(player.getGeodeticPosition().getZ()>airportElevation+20 && player.getLinearVelocityGlobal().getZ()>5) {
//                operation=Operation.STARTING;
//            }
//        }
//        else if(operation==Operation.STARTING) {
//            if(player.getGeodeticPosition().getZ()<airportElevation+500 && player.getLinearVelocityGlobal().getZ()<-5) {
//                operation=Operation.LANDING;
//            }
//            else if(player.getGeodeticPosition().getZ()>airportElevation+500) {
//                operation=Operation.TRAVEL;
//            }
//        }
//        else if(operation==Operation.GROUND) {
//            if(player.getGroundSpeed()>50) {
//                operation=Operation.STARTING;
//            }
//        }
//        
//        
//        return operation;
//    }

//    public String getOperationString() {
//        String result=null;
//        switch(getOperation()) {
//        case GROUND: 
//            result = "GND";
//            break;
//        case LANDING: 
//            result = "LND";
//            break;
//        case STARTING: 
//            result = "STA";
//            break;
//        case TRAVEL:
//            result = "TRV";
//            break;
//        case UNKNOWN:
//            result = "N/C";
//            break;
//        default:
//            result = "";
//        }
//        return result;
//    }

    public Alignment getAlignment() {
        return alignment;
    }


    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

//    public void setOperation(Operation operation) {
//        this.operation = operation;
//    }


    public String getFlightPlan() {
        return flightPlan;
    }


    public void setFlightPlan(String flightPlan) {
        this.flightPlan = flightPlan;
    }


    public String getAtcComment() {
        return atcComment;
    }


    public void setAtcComment(String atcComment) {
        if((this.atcComment==null && atcComment!=null) ||
                !this.atcComment.equals(atcComment)) {
            this.atcComment = atcComment;
            manager.saveAtcNotes();
        }
    }

    public String getCallSign() {
        return player.getCallsign();
    }
    /** in feet! **/
    public synchronized double getElevationFt() {
        return player.getGeodeticPosition().getZ()/Units.FT;
    }

    public double getElevationFtRounded() {
        long elev = Math.round(getElevationFt());
        return elev/100;
    }
    
    public String getFlightLevel() {
        return String.format("%03.0f", getElevationFt()/100);
    }
    
    public synchronized String getGroundSpeed() {
        return String.format("G%03.0f", player.getGroundSpeed()/Units.KNOTS);        
    }
    
    public double getGroundSpeedD() {
        return player.getGroundSpeed()/Units.KNOTS;        
    }
    
    public String getAirSpeed() {
        return String.format("A%03.0f",getAirSpeedD());        
    }
    
    public String getTrueCourse() {
        return String.format("%03.0f°",getTrueCourseD());        
    }
    
    public double getTrueCourseD() {
        if(getGroundSpeedD()>0.5 || lastHeading==-1) {
            lastHeading = player.getTrueCourse();
        }
        return lastHeading;
    }

    public String getMagnCourse() {
        return String.format("%03.0f°",getMagnCourseD());        
    }
    
    public double getMagnCourseD() {
        if(getGroundSpeedD()>0.5 || lastHeading==-1) {
            lastHeading = player.getTrueCourse() - airportData.getMagneticDeclination();
        }
        return lastHeading;
    }

    public synchronized String getAircraft() {
        if(aircraft==null || "".equals(aircraft)) {
            aircraft = player.getModel();
            if(aircraft!=null && aircraft.contains(".xml")) aircraft = aircraft.toUpperCase().substring(aircraft.lastIndexOf("/")+1,aircraft.indexOf(".xml"));
        }
        return aircraft;        
    }
    
    public double getVerticalSpeedD() {
        double dz = player.getLinearVelocityGlobal().getZ();//player.getGeodeticPosition().getZ() - player.getLastPostion().getZ();
        return getGroundSpeedD()>5 ? dz * 197.9d : 0d;
    }
    public String getVerticalSpeed() {
        return String.format("%+1.0f",getVerticalSpeedD()/100d);
    }
    
    // GuiSelectable
    
    public String getFormatedDetails() {
        return atcComment;
    }

    public boolean isSelected() {
        return manager.isSelected(this);
    }


    public boolean isOnEmergency() {
        return onEmergency;
    }

    public void setOnEmergency(boolean b) {
        onEmergency=b;
    }

    public double getAirSpeedD() {
        if(manager.getMaster().getMetar()==null) return -1;
        
        MetarData metar = manager.getMaster().getMetar();
        
        if(airportElevationFt+50<getElevationFt()) {
            // in air
            Vector3D v = player.getLinearVelocityGlobal();
            double windFromNorth = metar.getWindNorthComponent()*Units.KNOTS;
            double windFromWest = metar.getWindWestComponent()*Units.KNOTS;
            double result = Math.sqrt(Math.pow(v.getX()-windFromWest,2) + Math.pow(v.getY()-windFromNorth,2)+Math.pow(v.getZ(),2))/Units.KNOTS;
            //System.out.println(String.format("FlightDir %s Speed (%1.0f,%1.0f,%1.0f)=%3.0f Wind (%1.0f,%1.0f) %.0f",getTrueCourse(),v.getX(),v.getY(), v.getZ(),v.getLength()/Units.KNOTS, windFromNorth,windFromWest,result));
            return result;
        } else {
            // on ground
            return getGroundSpeedD();
        }
    }
    
    public String toString() { return player.getCallsign(); }


    public void setView(RadarTargetView view) {
        this.view = view;
    }
    
    public boolean isVisible() {
        return view.isVisible();
    }


    public Point2D getCenterViewCoordinates() {
        return view.getCenterViewCoordinates();
    }


    public double getRadarContactDistanceD() {
        if(view==null) return 0d;
        Point2D apPos = view.convertToDeviceLocation(airportData.getAirportPosition());
        Point2D plPos = view.getPlayersMapPosition();
        return Math.sqrt( Math.pow(plPos.getX()-apPos.getX(),2) + Math.pow(plPos.getY()-apPos.getY(),2) ) * Converter2D.getMilesPerDot(getMapViewerAdapter());
    }

    public String getRadarContactDistance() {
        return String.format("%1.1f",getRadarContactDistanceD());
    }
    
    public long getRadarContactDirectionD() {
        if(view==null) return -1;
        Point2D apPos = view.convertToDeviceLocation(airportData.getAirportPosition());
        Point2D plPos = getCenterViewCoordinates();
        double dx = plPos.getX()-apPos.getX();
        double dy = apPos.getY()-plPos.getY();

        double distance = (double)plPos.distance(apPos);
        Long angle = null;
        if(distance!=0) {
            if(dx>0 && dy>0) angle = Math.round(Math.asin(dx/distance)/2d/Math.PI*360d); 
            if(dx>0 && dy<0) angle = 180-Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
            if(dx<0 && dy<0) angle = 180+-1*Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
            if(dx<0 && dy>0) angle = 360+Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
        }
        long degrees = angle!=null ? ( angle<0 ? angle+360 : angle) : -1;
        
        return degrees;
    }

    public String getRadarContactDirection() {
        return String.format("%d",getRadarContactDirectionD());
    }
    
    public State getState() {
        State state = State.UNCONTROLLED;
        switch(alignment) {
        case CENTER:
            state=State.CONTROLLED;
            break;
        case LEFT:
            state=State.IMPORTANT;
            break;
        default:
            state = State.UNCONTROLLED;
            break;
        }
        return state;
    }

    public IMapViewerAdapter getMapViewerAdapter() {
        return view.getMapViewAdapter();
    }

    public Point2D getCenterGeoCoordinates() {
        Position p = player.getGeodeticPosition();
        return new Point2D.Double(p.getX(),p.getY());
    }

    public String getFrequency() {
        return player.getFrequency();
    }

    public double getHeadingD() {
        return Converter2D.normalizeAngle(player.getHeading());
    }

    public String getAtcLanguage() {
        return atcLanguage;
    }

    public void setAtcLanguage(String atcLanguage) {
        this.atcLanguage = atcLanguage;
    }
}
