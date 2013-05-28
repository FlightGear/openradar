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
package de.knewcleus.openradar.gui.contacts;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AircraftCodeConverter;
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

    public long appeared = System.currentTimeMillis();
    public enum State {UNCONTROLLED,CONTROLLED,IMPORTANT};
    public enum Operation {
        GROUND, LANDING, STARTING, TRAVEL, EMERGENCY, UNKNOWN
    };
    public final List<FlightState> flightStates = new ArrayList<FlightState>();

    public enum Alignment {LEFT,CENTER,RIGHT}
    private volatile Alignment alignment = Alignment.RIGHT;

    private int hightlightCounter = 0;

    private volatile boolean neglect = false;
    private volatile boolean onEmergency = false;

    private RadarContactController manager = null;
    private volatile TargetStatus player = null;
    private volatile FlightState currentFlightState;

    private String aircraft = "";
    private String activeModel = "";
    private String formerModel = "";
    private String flightPlan = "";
    private volatile String atcComment = null;
    protected volatile RadarTargetView view;
    protected final double airportElevationFt;
    protected final AirportData airportData;
    protected volatile double lastHeading=-1;
    protected volatile String atcLanguage = "en";
    private volatile boolean fgComSupport = false;
    protected volatile Integer assignedSquawk = null;

    public GuiRadarContact(GuiMasterController master, RadarContactController manager, TargetStatus player, String atcComment) {
        this.manager=manager;
        this.player=player;
        this.airportData = master.getDataRegistry();
        this.airportElevationFt = master.getDataRegistry().getElevationFt();
        this.atcComment = atcComment;

        flightStates.add(new FlightState("","UNCONTROLLED","???","Uncontrolled/cold"));
        flightStates.add(new FlightState("GND","PARKED","PRKNG","At Gate/Parking warm"));
        flightStates.add(new FlightState("GND","TAXI_TO_RW","TXRW","Taxi to Runway"));
        flightStates.add(new FlightState("TWR","START","START","Starting"));
        flightStates.add(new FlightState("APP","DEPARTING","DEPART","Leaving aiport"));
        flightStates.add(new FlightState("APP","TRANSITION","TRANS","Transition through area"));
        flightStates.add(new FlightState("APP","APPROACH","APPRCH","Approaching airport"));
        flightStates.add(new FlightState("TWR","LANDING","LNDNG","Landing"));
        flightStates.add(new FlightState("GND","TAXI_TO_GATE","TXGT","Taxi to Gate/Parking"));
        currentFlightState=flightStates.get(0);
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

    public synchronized boolean isHighlighted() {
        if(hightlightCounter>0) {
            hightlightCounter--;
            return true;
        }
        else {
            return false;
        }
    }

    public synchronized void setHighlighted() {
        this.hightlightCounter=2;
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

    public synchronized FlightState getFlightState() {
        return currentFlightState;
    }

    public synchronized void setFlightState(FlightState fs) {
        currentFlightState = fs;
    }

    public synchronized void setNextFlightState() {
        int currentIndex = flightStates.indexOf(currentFlightState);
        if(currentIndex<flightStates.size()-1) {
            currentFlightState = flightStates.get(currentIndex+1);
        } else {
            currentFlightState = flightStates.get(0);
        }
    }

    public synchronized void setPrevFlightState() {
        int currentIndex = flightStates.indexOf(currentFlightState);
        if(currentIndex>0) {
            currentFlightState = flightStates.get(currentIndex-1);
        } else {
            currentFlightState = flightStates.get(flightStates.size());
        }
    }

    public Alignment getAlignment() {
        return alignment;
    }


    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

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

    public synchronized boolean hasFgComSupport() {
        return fgComSupport;
    }

    public synchronized void setFgComSupport(boolean fgComSupport) {
        this.fgComSupport = fgComSupport;
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

    public double getAltitude() {
        return getElevationFt();
    }

    public String getFlightLevel() {
        return String.format("FL%03.0f", getElevationFt()/100);
    }

    public synchronized String getGroundSpeed() {
        return String.format("G%03.0f", player.getCalculatedGroundspeed()/Units.KNOTS);
    }

    public double getGroundSpeedD() {
        return player.getCalculatedGroundspeed()/Units.KNOTS;
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
    /**
     * Returns the model name that the contact uses.
     *
     * @return
     */
    public synchronized String getModel() {
        checkModel();
        return activeModel;
    }

    public synchronized String getAircraftCode() {
        checkModel();
        return aircraft;
    }

    /** common code for getModel() and getAircraftCode()*/
    private void checkModel() {
        String model = player.getModel();
        if(model!=null && !model.equals(formerModel)) {
            boolean checkForAutoAtcNeglect = formerModel.equals("");
            formerModel=model;
            if(model.contains(".xml")) {
                model = model.toUpperCase().substring(model.lastIndexOf("/")+1,model.indexOf(".xml"));
            }
            activeModel = AircraftCodeConverter.checkLength(model,10);
            aircraft = airportData.getAircraftCodeConverter().convert(model);

            if(checkForAutoAtcNeglect && isAtc()) {
                this.setNeglect(true); // auto neglect for ATCs
            }
        }
    }

    public synchronized double getVerticalSpeedD() {
        double dz = player.getLinearVelocityGlobal().getZ();//player.getGeodeticPosition().getZ() - player.getLastPostion().getZ();
        return getGroundSpeedD()>5 ? -1 * dz / Units.FT * 60d : 0d;
    }
    public synchronized String getVerticalSpeed() {
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

        return (long)Converter2D.getDirection(apPos, plPos);
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

    public long getLastUpdate() {
        return player.getLastMessageTime();
    }

    public Integer getTranspSquawkCode() {
        return player.getTranspSquawkCode();
    }

    public Integer getTranspAltitude() {
        return player.getTranspAltitude();
    }

    public boolean isIdentActive() {
        return player.isIdentActive();
    }

    public synchronized Integer getAssignedSquawk() {
        return assignedSquawk;
    }

    public synchronized void setAssignedSquawk(Integer assignedSquawk) {
        this.assignedSquawk = assignedSquawk;
    }

    public synchronized void reAppeared() {
        appeared = System.currentTimeMillis();
    }

    public synchronized boolean isNew() {
        return System.currentTimeMillis() - appeared < 30000;
    }

    public boolean isAtc() {
        String acrft = getModel().toUpperCase();
        return acrft.startsWith("ATC") || acrft.startsWith("OPENRA");
    }

    public String getAtcLetter() {
        return null;
    }
}
