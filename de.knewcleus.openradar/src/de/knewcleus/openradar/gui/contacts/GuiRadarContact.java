package de.knewcleus.openradar.gui.contacts;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;
import de.knewcleus.openradar.rpvd.RadarTargetView;

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
    private Alignment alignment = Alignment.RIGHT;

    private RadarContactController manager = null;
    private TargetStatus player = null;
    
    private Operation operation = Operation.UNKNOWN;
    private boolean onEmergency = false;
    private String aircraft = "";
    private String flightPlan = "";
    private String atcComment = null;
    protected RadarTargetView view;

    public GuiRadarContact(RadarContactController manager, TargetStatus player) {
        this.manager=manager;
        this.player=player;
    }
   
    
    public RadarContactController getManager() {
        return manager;
    }


    public void setManager(RadarContactController manager) {
        this.manager = manager;
    }

    public Operation getOperation() {
        if(operation==Operation.UNKNOWN) {
            if(player.getGeodeticPosition().getZ()<10) {
                operation=Operation.GROUND;
            } else
            if(player.getGeodeticPosition().getZ()<500 && player.getLinearVelocity().getZ()>5) {
                operation=Operation.STARTING;
            } else
            if(player.getGeodeticPosition().getZ()<500 && player.getLinearVelocity().getZ()<-5) {
                operation=Operation.LANDING;
            } else
            if(player.getGeodeticPosition().getZ()>500) {
                operation=Operation.TRAVEL;
            }
        }
        else if(operation==Operation.TRAVEL) {
            if(player.getGeodeticPosition().getZ()<500 && player.getLinearVelocity().getZ()<-5) {
                operation=Operation.LANDING;
            }
        }
        else if(operation==Operation.LANDING) {
            if(player.getGeodeticPosition().getZ()<10 && player.getGroundSpeed()<40) {
                operation=Operation.GROUND;
            } else if(player.getGeodeticPosition().getZ()<500 && player.getLinearVelocity().getZ()>5) {
                operation=Operation.STARTING;
            }
        }
        else if(operation==Operation.STARTING) {
            if(player.getGeodeticPosition().getZ()<500 && player.getLinearVelocity().getZ()<-5) {
                operation=Operation.LANDING;
            }
            else if(player.getGeodeticPosition().getZ()>500) {
                operation=Operation.TRAVEL;
            }
        }
        else if(operation==Operation.GROUND) {
            if(player.getGroundSpeed()>50) {
                operation=Operation.STARTING;
            }
        }
        
        
        return operation;
    }

    public String getOperationString() {
        String result=null;
        switch(getOperation()) {
        case GROUND: 
            result = "GND";
            break;
        case LANDING: 
            result = "LND";
            break;
        case STARTING: 
            result = "STA";
            break;
        case TRAVEL:
            result = "TRV";
            break;
        case UNKNOWN:
            result = "N/C";
            break;
        default:
            result = "";
        }
        return result;
    }

    public Alignment getAlignment() {
        return alignment;
    }


    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
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
        this.atcComment = atcComment;
    }

    public String getCallSign() {
        return player.getCallsign();
    }
    
    public String getFlightLevel() {
        return String.format("FL%03.0f", player.getGeodeticPosition().getZ()/100);
    }
    
    public String getGroundSpeed() {
        return String.format("G%03.0f", player.getGroundSpeed());        
    }
    
    public double getGroundSpeedD() {
        return player.getGroundSpeed();        
    }
    
    public String getAirspeed() {
        return String.format("T%03.0f",getAirSpeed());        
    }
    
    public String getTrueCourse() {
        return String.format("%03.0f°", player.getTrueCourse());        
    }
    
    public double getTrueCourseD() {
        return player.getTrueCourse();        
    }

    public String getAircraft() {
        if(aircraft==null || "".equals(aircraft)) {
            aircraft = player.getModel();
            if(aircraft!=null && aircraft.contains(".xml")) aircraft = aircraft.toUpperCase().substring(aircraft.lastIndexOf("/")+1,aircraft.indexOf(".xml"));
        }
        return aircraft;        
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

    public double getAirSpeed() {
        if(manager.getMaster().getMetar()==null) return -1;
        Vector3D v = player.getLinearVelocity();
        // compensate wind to get Airspeed
        // todo replace 500 with 500 above ground
        int windNorth = player.getGeodeticPosition().getZ()>500 ? manager.getMaster().getMetar().getWindNorthComponent() : 0;
        int windEast = player.getGeodeticPosition().getZ()>500 ? manager.getMaster().getMetar().getWindEastComponent() : 0;
        return Math.sqrt(Math.pow(v.getX()-windEast,2) + Math.pow(v.getY()-windNorth,2) + Math.pow(v.getZ(),2) );
    }
    
    public String toString() { return player.getCallsign(); }


    public void setView(RadarTargetView view) {
        this.view = view;
    }
    
    public boolean isVisible() {
        return view.isVisible();
    }


    public double getMilesPerDot() {
        return view.getMilesPerDot();
    }


    public Point2D getCenterViewCoordinates() {
        return view.getCenterViewCoordinates();
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
}
