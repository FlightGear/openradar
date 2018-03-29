/**
 * Copyright (C) 2012,2013-2016, 2018 Wolfram Wagner
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.contacts;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.GeoUtil;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AircraftCodeConverter;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;
import de.knewcleus.openradar.rpvd.RadarTargetView;
import de.knewcleus.openradar.view.Converter2D;
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
//    public final List<FlightState> flightStates = new ArrayList<FlightState>();

    public enum Alignment {LEFT,CENTER,RIGHT}
    private volatile Alignment alignment = Alignment.RIGHT;

    private int hightlightCounter = 0;

    private volatile boolean newContact = true;
    private volatile boolean neglect = false;

    private RadarContactController manager = null;
    private volatile TargetStatus player = null;
//    private volatile FlightState currentFlightState;

    private String aircraft = "";
    private String activeModel = "";
    private String formerModel = "";
    private FlightPlanData flightPlan;
    private volatile String atcComment = null;
    protected volatile RadarTargetView view;
    protected final double airportElevationFt;
    protected final AirportData airportData;
    protected volatile double lastHeading=-1;
    protected volatile double lastHeadingMag=-1;
    protected volatile double distanceD;
    protected volatile String atcLanguage = "en";
    private volatile boolean fgComSupport = false;

    protected FlightStrip flightstrip = null;  
    
    public GuiRadarContact(GuiMasterController master, RadarContactController manager, TargetStatus player, String atcComment) {
        this.manager=manager;
        this.player=player;
        this.airportData = master.getAirportData();
        this.airportElevationFt = master.getAirportData().getElevationFt();
        this.atcComment = atcComment;

//        flightStates.add(new FlightState("","UNCONTROLLED","???","Uncontrolled/cold"));
//        flightStates.add(new FlightState("GND","PARKED","PRKNG","At Gate/Parking warm"));
//        flightStates.add(new FlightState("GND","TAXI_TO_RW","TXRW","Taxi to Runway"));
//        flightStates.add(new FlightState("TWR","START","START","Starting"));
//        flightStates.add(new FlightState("APP","DEPARTING","DEPART","Leaving aiport"));
//        flightStates.add(new FlightState("APP","TRANSITION","TRANS","Transition through area"));
//        flightStates.add(new FlightState("APP","APPROACH","APPRCH","Approaching airport"));
//        flightStates.add(new FlightState("TWR","LANDING","LNDNG","Landing"));
//        flightStates.add(new FlightState("GND","TAXI_TO_GATE","TXGT","Taxi to Gate/Parking"));
//        currentFlightState=flightStates.get(0);

        flightPlan = new FlightPlanData(master.getAirportData(), this);
    }
    /**
     * New since 20151223
     * 
     * @param master
     * @param manager
     * @param player
     * @param atcComment
     */
    public GuiRadarContact(GuiMasterController master, RadarContactController manager, TargetStatus player) {
        this.manager=manager;
        this.player=player;
        this.airportData = master.getAirportData();
        this.airportElevationFt = master.getAirportData().getElevationFt();

//        flightStates.add(new FlightState("","UNCONTROLLED","???","Uncontrolled/cold"));
//        flightStates.add(new FlightState("GND","PARKED","PRKNG","At Gate/Parking warm"));
//        flightStates.add(new FlightState("GND","TAXI_TO_RW","TXRW","Taxi to Runway"));
//        flightStates.add(new FlightState("TWR","START","START","Starting"));
//        flightStates.add(new FlightState("APP","DEPARTING","DEPART","Leaving aiport"));
//        flightStates.add(new FlightState("APP","TRANSITION","TRANS","Transition through area"));
//        flightStates.add(new FlightState("APP","APPROACH","APPRCH","Approaching airport"));
//        flightStates.add(new FlightState("TWR","LANDING","LNDNG","Landing"));
//        flightStates.add(new FlightState("GND","TAXI_TO_GATE","TXGT","Taxi to Gate/Parking"));
//        currentFlightState=flightStates.get(0);

        flightPlan = new FlightPlanData(master.getAirportData(), this);
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

    public synchronized boolean isNeglect() {
        return neglect;
    }

    public synchronized void setNeglect(boolean neglect) {
        this.neglect = neglect;
        disableNewContact();
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

//    public synchronized FlightState getFlightState() {
//        return currentFlightState;
//    }
//
//    public synchronized void setFlightState(FlightState fs) {
//        currentFlightState = fs;
//    }
//
//    public synchronized void setNextFlightState() {
//        int currentIndex = flightStates.indexOf(currentFlightState);
//        if(currentIndex<flightStates.size()-1) {
//            currentFlightState = flightStates.get(currentIndex+1);
//        } else {
//            currentFlightState = flightStates.get(0);
//        }
//    }
//
//    public synchronized void setPrevFlightState() {
//        int currentIndex = flightStates.indexOf(currentFlightState);
//        if(currentIndex>0) {
//            currentFlightState = flightStates.get(currentIndex-1);
//        } else {
//            currentFlightState = flightStates.get(flightStates.size());
//        }
//    }

    public Alignment getAlignment() {
        return alignment;
    }


    synchronized void setAlignment(Alignment alignment) {
        disableNewContact();
        this.alignment = alignment;
    }

    public synchronized FlightPlanData getFlightPlan() {
        return flightPlan;
    }


    public synchronized  void setFlightPlan(FlightPlanData  flightPlan) {
        this.flightPlan = flightPlan;
    }


    public synchronized String getAtcComment() {
        return atcComment;
    }


    public synchronized void setAtcComment(String atcComment) {
        if((this.atcComment==null && atcComment!=null) ||
                !this.atcComment.equals(atcComment)) {
            this.atcComment = atcComment;
        }
    }

    public synchronized boolean hasFgComSupport() {
        return fgComSupport;
    }

    public synchronized void setFgComSupport(boolean fgComSupport) {
    	if(this.fgComSupport!=fgComSupport) {
    		this.fgComSupport = fgComSupport;
    	}
    }

    public String getCallSign() {
        return player.getCallsign();
    }
    /** in feet! **/
    public synchronized double getElevationFt() {
        return player.getGeodeticPosition().getZ()/Units.FT;
    }

    public synchronized double getElevationFtRounded() {
        long elev = Math.round(getElevationFt());
        return elev/100;
    }

    public synchronized double getAltitude() {
        return getElevationFt();
    }

    public synchronized String getAltitudeString(GuiMasterController master) {
        if(getElevationFt() > airportData.getTransitionAlt() && master.getAirportMetar().getPressureHPa()>1) {
            // show flight level
            int flAlt = 0;
            if(null==getTranspAltitude() || getTranspAltitude()==-9999) {
                // data from MP Protocol => real alt
                
                /* This calculation is too simple to be correct. A better implementation would retrieve the metar, used by the contact,
                 * simulate the ambient pressure and derive the pressure altitute out of it. As doing this away from FGFS this is pretty 
                 * error prone, so I did no implement it yet  */
                
                // convert to pressure alt
                flAlt = (int)Math.round((int)getElevationFt() + (30 * (1013.25-master.getAirportMetar().getPressureHPa())));
                return String.format("FL%03d", (Math.round(flAlt/100.0)));
            } else {
                // data from transponder => pressure alt
                flAlt = getTranspAltitude();
                return String.format("FL%03d", (Math.round(flAlt/100.0)));
            }
        } else {
            // show altitude
            int realAlt = 0;
            realAlt = (int)getElevationFt();
            
            // alternative implementation, problem: works with local pressure from METAR
//            if(null==getTranspAltitude() || getTranspAltitude()==-9999) {
//                // data from MP Protocol => real alt
//                realAlt = (int)getElevationFt();
//            } else {
//                // data from transponder => pressure alt
//                // convert to real alt
//                realAlt = getTranspAltitude() - 30 * (1013-(int)master.getAirportMetar().getPressureHPa());;
//            }
            return String.format("%04d", Math.round((realAlt/100.0))*100);
        }
    }
    
    public synchronized String getFlightLevel() {
        return String.format("FL%03.0f", getElevationFt()/100);
    }

    public synchronized String getGroundSpeed() {
        return String.format("%03.0f", player.getCalculatedGroundspeed()/Units.KNOTS);
    }

    public synchronized double getGroundSpeedD() {
        return player.getCalculatedGroundspeed()/Units.KNOTS;
    }

    public synchronized String getAirSpeed() {
        return String.format("A%03.0f",getAirSpeedD());
    }

    public synchronized String getTrueCourse() {
        return String.format("%03.0f°",getTrueCourseD());
    }

    public synchronized double getTrueCourseD() {
        if(getGroundSpeedD()>0.5 || lastHeading==-1) {
            lastHeading = player.getTrueCourse();
        }
        return lastHeading;
    }

    public synchronized String getMagnCourse() {
        return String.format("%03.0f°",getMagnCourseD());
    }

    public synchronized double getMagnCourseD() {
        if(getGroundSpeedD()>0.5 || lastHeadingMag==-1) {
            lastHeadingMag = Converter2D.normalizeAngle(player.getTrueCourse() - airportData.getMagneticDeclination());
        }
        return lastHeadingMag;
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

            getFlightPlan().setTrueAirspeed(airportData.getAircraftCodeConverter().getCruiseSpeed(activeModel));
            
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

    public synchronized String getFormatedDetails() {
        return atcComment;
    }

    public boolean isSelected() {
    	// not synchronized because it forwards to synchronized manager method
        return manager.isSelected(this);
    }


    public synchronized boolean isOnEmergency() {
        return getTranspSquawkCode() != null && (7700 == getTranspSquawkCode() || 7600 == getTranspSquawkCode() || 7500 == getTranspSquawkCode());
    }

    public synchronized double getAirSpeedD() {
        MetarData metar = manager.getMaster().getAirportMetar();
        if(metar==null) return -1;


        if(airportElevationFt+200<getElevationFt()) {
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


    public synchronized void setView(GuiMasterController master, RadarTargetView view) {
        this.view = view;
        
        if(getFlightPlan().getDestinationAirport().isEmpty() && getRadarContactDistanceD()<3 && getGroundSpeedD()<1) {
            getFlightPlan().setDepartureAirport(master.getAirportData().getAirportCode());
        }
    }

    public boolean isVisible() {
    	// not synchronized because it forwards to synchronized view
        return view.isVisible();
    }


    public Point2D getCenterViewCoordinates() {
    	// not synchronized because it forwards to synchronized view
        return view.getCenterViewCoordinates();
    }


    public synchronized double getRadarContactDistanceD() {
        if(view==null) return 0d;
        Point2D apPos = airportData.getAirportPosition();
        Position plPos = player.getGeodeticPosition();
        return GeoUtil.getDistance(apPos.getX(), apPos.getY(), plPos.getX(), plPos.getY()).length/Units.NM;
    }

    public synchronized String getRadarContactDistance() {
        return String.format("%1.1f",getRadarContactDistanceD());
    }

    public synchronized long getRadarContactDirectionD() {
        if(view==null) return -1;
        Point2D apPos = airportData.getAirportPosition();
        Position plPos = player.getGeodeticPosition();
        return (long)GeoUtil.getDistance(apPos.getX(), apPos.getY(), plPos.getX(), plPos.getY()).angle;
    }

    public synchronized String getRadarContactDirection() {
        return String.format("%d",getRadarContactDirectionD());
    }

    public synchronized State getState() {
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

//    public IMapViewerAdapter getMapViewerAdapter() {
//        return view.getMapViewAdapter();
//    }

    public synchronized Point2D getCenterGeoCoordinates() {
        Position p = player.getGeodeticPosition();
        return new Point2D.Double(p.getX(),p.getY());
    }

    public synchronized String getFrequency() {
        return player.getFrequency();
    }

    public synchronized double getHeadingD() {
        return Converter2D.normalizeAngle(player.getHeading());
    }

    public synchronized String getAtcLanguage() {
        return atcLanguage;
    }

    public synchronized void setAtcLanguage(String atcLanguage) {
        this.atcLanguage = atcLanguage;
    }

    public synchronized long getLastUpdate() {
        return player.getLastMessageTime();
    }

    public synchronized Integer getTranspSquawkCode() {
        return player.getTranspSquawkCode();
    }

    public static String formatSquawk(Integer squawk) {
    	return ((squawk == null) ? "" : String.format("%04d", squawk));
    }
    
    public synchronized String getTranspSquawkDisplay() {
        //if(getTranspSquawkCode()!=null) System.out.println(getCallSign()+": Sq:"+getTranspSquawkCode()+" A:"+getTranspAltitude());
        Integer squawk = getTranspSquawkCode();
        if ((squawk != null) && (squawk == -9999)) squawk = null; 
        return formatSquawk (squawk);
    }

    public synchronized String getSquawkDisplay() {
    	Integer aSquawk = getAssignedSquawk();
    	Integer tSquawk = getTranspSquawkCode();
        //if(getTranspSquawkCode()!=null) System.out.println(getCallSign()+": Sq:"+getTranspSquawkCode()+" A:"+getTranspAltitude());
    	if (tSquawk == null) return (aSquawk == null) ? "" : (getAssignedSquawkDisplay() + "(standby)");
        if (tSquawk == -9999) return getAssignedSquawkDisplay() + "(standby)";
        if (tSquawk.equals(aSquawk)) return getTranspSquawkDisplay();
        return getAssignedSquawkDisplay() + "(" + getTranspSquawkDisplay() + ")";
    }

    public synchronized Integer getAssignedSquawk() {
        Integer squawk = null; 
        try {
            squawk = Integer.parseInt(flightPlan.getSquawk());
        } catch(Exception e) {
        }
        return squawk;
    }

    public synchronized String getAssignedSquawkDisplay() {
        //if(getAssignedSquawk()!=null) System.out.println(getCallSign()+": Sq:"+getAssignedSquawk());
    	return formatSquawk (getAssignedSquawk());
    }

    public synchronized void setAssignedSquawk(Integer assignedSquawk) {
        flightPlan.setSquawk(assignedSquawk!=null?assignedSquawk.toString():"");;
    }

    public synchronized Integer getTranspAltitude() {
        return player.getTranspAltitude();
    }

    public synchronized String getTranspMode() {
        return player.getTranspModeS();
    }

    public synchronized boolean isIdentActive() {
        return player.isIdentActive();
    }

    public synchronized void reAppeared() {
        appeared = System.currentTimeMillis();
    }

    public synchronized void disableNewContact() {
        newContact=false;
    }
    
    public synchronized boolean isNew() {
        return newContact;
        //return System.currentTimeMillis() - appeared < 30000;
    }

    public synchronized boolean isAtc() {
        String acrft = getModel().toUpperCase();
        return acrft.startsWith("ATC") || acrft.startsWith("OPENRA");
    }

    public synchronized String getAtcLetter() {
        return null;
    }

    public AirportData getAirportData() {
    	return airportData;
    }
    
    public void setFlightstrip(FlightStrip flightstrip) {
    	this.flightstrip = flightstrip;
    }
    
    public FlightStrip getFlightStrip() {
    	return flightstrip;
    }
    
}
