/**
 * Copyright (C) 2013 Wolfram Wagner
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
package de.knewcleus.openradar.gui.flightplan;

import java.util.Date;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.Alignment;
import de.knewcleus.openradar.gui.setup.AirportData;

public class FlightPlanData {

    private boolean changed = false;

    public enum FlightPlanStatus {
        FILED, ACTIVE, CLOSED, EXPIRED, DELETED
    };

    public enum FlightType {
        VFR, IFR
    };

    private final AirportData airportData;

    private final GuiRadarContact contact;

    private String assignedAltitude;
    private String assignedRoute;
    private String assignedRunway;

    private String flightCode;
    private String callsign;
    private String owner;
    private String handover;
    private String squawk;
    private String fpStatus;
    private String type;
    private String aircraft;
    private String trueAirspeed;
    private String departureAirport;
    private String departure;
    private String cruisingAltitude;
    private String route;
    private String destinationAirport;
    private String alternativeDestinationAirports;
    private String estimatedFlightTime;
    private String estimatedFuelTime;
    private String pilotName;
    private int soulsOnBoard;
    private String remarks;

    private static FlightPlanTypeModel flightPlanTypeModel;

    public FlightPlanData(AirportData airportData, GuiRadarContact contact) {

        this.airportData = airportData;

        this.assignedAltitude = null;
        this.assignedRoute = null;
        this.assignedRunway = null;

        this.contact = contact;
        this.callsign = contact.getCallSign();
        this.owner = null;
        this.handover = null;
        this.squawk = "" + contact.getAssignedSquawk();
        this.fpStatus = FlightPlanStatus.FILED.toString();
        this.type = FlightType.IFR.toString();
        this.aircraft = contact.getAircraftCode();
        this.trueAirspeed = null;
        this.departureAirport = null;
        this.departure = null;
        this.cruisingAltitude = null;
        this.route = null;
        this.destinationAirport = null;
        this.alternativeDestinationAirports = null;
        this.estimatedFlightTime = null;
        this.estimatedFuelTime = null;
        this.pilotName = null;
        this.soulsOnBoard = 1;
        this.remarks = null;

        this.changed = true;
    }

    public FlightPlanData(AirportData airportData, GuiRadarContact contact, String flightCode, String callSign, String owner, String handover, String squawk,
            String assignedAltitude, String fpStatus, String type, String aircraft, String trueAirspeed, String departureAirport, String departureTime,
            String cruisingAltitude, String route, String destinationAirport, String alternativeDestinationAirports, String estimatedFlightTime,
            String estimatedFuelTime, String pilotName, String soulsOnBoard, String remarks) {

        this.airportData = airportData;

        this.assignedAltitude = null;
        this.assignedRoute = null;
        this.assignedRunway = null;

        this.contact = contact;
        this.flightCode = flightCode;
        this.callsign = callSign;
        this.owner = owner;
        this.handover = handover;
        this.squawk = squawk;
        this.fpStatus = fpStatus;
        this.type = type;
        this.aircraft = aircraft;
        this.trueAirspeed = trueAirspeed;
        this.departureAirport = departureAirport;
        this.departure = departureTime;
        this.cruisingAltitude = cruisingAltitude;
        this.route = route;
        this.destinationAirport = destinationAirport;
        this.alternativeDestinationAirports = alternativeDestinationAirports;
        this.estimatedFlightTime = estimatedFlightTime;
        this.estimatedFuelTime = estimatedFuelTime;
        this.pilotName = pilotName;
        try {
            this.soulsOnBoard = Integer.parseInt(soulsOnBoard);
        } catch (Exception e) {
            this.soulsOnBoard = 1;
        }
        this.remarks = remarks;

        this.changed = true;
    }

    public synchronized void update(String flightCode, String callSign, String owner, String handover, String squawk, String assignedAltitude, String fpStatus,
                                    String type, String aircraft, String trueAirspeed, String departureAirport, String departureTime, String cruisingAltitude,
                                    String route, String destinationAirport, String alternativeDestinationAirports, String estimatedFlightTime,
                                    String estimatedFuelTime, String pilotName, String soulsOnBoard, String remarks) {

        this.assignedAltitude = null;
        this.assignedRoute = null;
        this.assignedRunway = null;

        this.flightCode = flightCode;
        this.callsign = callSign;
        this.owner = owner;
        this.handover = handover;
        this.squawk = squawk;
        this.fpStatus = fpStatus;
        this.type = type;
        this.aircraft = aircraft;
        this.trueAirspeed = trueAirspeed;
        this.departureAirport = departureAirport;
        this.departure = departureTime;
        this.cruisingAltitude = cruisingAltitude;
        this.route = route;
        this.destinationAirport = destinationAirport;
        this.alternativeDestinationAirports = alternativeDestinationAirports;
        this.estimatedFlightTime = estimatedFlightTime;
        this.estimatedFuelTime = estimatedFuelTime;
        this.pilotName = pilotName;
        try {
            this.soulsOnBoard = Integer.parseInt(soulsOnBoard);
        } catch (Exception e) {
            this.soulsOnBoard = 1;
        }
        this.remarks = remarks;

        this.changed = true;
    }

    public synchronized FlightPlanData copy() {
        FlightPlanData c = new FlightPlanData(airportData, contact, flightCode, callsign, owner, handover, squawk, assignedAltitude,
                fpStatus, type, aircraft, trueAirspeed, departureAirport, departure, cruisingAltitude, route, destinationAirport,
                alternativeDestinationAirports, estimatedFlightTime, estimatedFuelTime, pilotName, "" + soulsOnBoard, remarks);
        c.changed = true;

        return c;
    }

    public synchronized GuiRadarContact getContact() {
        return contact;
    }

    public synchronized String getFlightCode() {
        return removeNull(flightCode);
    }

    public synchronized void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
        this.changed = true;
    }

    public synchronized String getCallsign() {
        return callsign;
    }

    public synchronized void setCallsign(String callSign) {
        this.callsign = callSign;
        this.changed = true;
    }

    public synchronized String getOwner() {
        return owner;
    }

    public synchronized void setOwner(String owner) {
        this.owner = owner;
        this.changed = true;
    }

    public synchronized String getHandover() {
        return handover;
    }

    public synchronized void setHandover(String handover) {
        this.handover = handover;
        this.changed = true;
    }

    public synchronized String getSquawk() {
        return removeNull(squawk);
    }

    public synchronized void setSquawk(String squawk) {
        this.squawk = squawk;
        this.changed = true;
    }

    public synchronized String getAssignedAltitude() {
        return removeNull(assignedAltitude);
    }

    public synchronized void setAssignedAltitude(String assignedAltitude) {
        this.assignedAltitude = assignedAltitude;
        this.changed = true;
    }

    public String getAssignedRoute() {
        return removeNull(assignedRoute);
    }

    public void setAssignedRoute(String assignedRoute) {
        this.assignedRoute = assignedRoute;
        this.changed = true;
    }

    public String getAssignedRunway() {
        return removeNull(assignedRunway);
    }

    public void setAssignedRunway(String assignedRunway) {
        this.assignedRunway = assignedRunway;
        this.changed = true;
    }

    public synchronized String getFpStatus() {
        return fpStatus;
    }

    public synchronized void setFpStatus(String fpStatus) {
        this.fpStatus = fpStatus;
        this.changed = true;
    }

    public synchronized String getType() {
        return type;
    }

    public synchronized void setType(String type) {
        this.type = type;
        this.changed = true;
    }

    public synchronized String getAircraft() {
        return removeNull(aircraft);
    }

    public synchronized void setAircraft(String aircraft) {
        this.aircraft = aircraft;
        this.changed = true;
    }

    public synchronized String getTrueAirspeed() {
        return removeNull(trueAirspeed);
    }

    public synchronized void setTrueAirspeed(String trueAirspeed) {
        this.trueAirspeed = trueAirspeed;
        this.changed = true;
    }

    public synchronized String getDepartureAirport() {
        return removeNull(departureAirport);
    }

    public synchronized void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
        this.changed = true;
    }

    public synchronized String getDeparture() {
        return removeNull(departure);
    }

    public synchronized void setDeparture(String departureTime) {
        this.departure = departureTime;
        this.changed = true;
    }

    public synchronized String getCruisingAltitude() {
        return removeNull(cruisingAltitude);
    }

    public synchronized void setCruisingAltitude(String cruisingAltitude) {
        this.cruisingAltitude = cruisingAltitude;
        this.changed = true;
    }

    public synchronized String getRoute() {
        return removeNull(route);
    }

    public synchronized void setRoute(String route) {
        this.route = route;
        this.changed = true;
    }

    public synchronized String getDestinationAirport() {
        return removeNull(destinationAirport);
    }

    public synchronized void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
        this.changed = true;
    }

    public synchronized String getAlternativeDestinationAirports() {
        return removeNull(alternativeDestinationAirports);
    }

    public synchronized void setAlternativeDestinationAirports(String alternativeDestinationAirports) {
        this.alternativeDestinationAirports = alternativeDestinationAirports;
        this.changed = true;
    }

    public synchronized String getEstimatedFlightTime() {
        return removeNull(estimatedFlightTime);
    }

    public synchronized void setEstimatedFlightTime(String estimatedFlightTime) {
        this.estimatedFlightTime = estimatedFlightTime;
        this.changed = true;
    }

    public synchronized String getEstimatedFuelTime() {
        return removeNull(estimatedFuelTime);
    }

    public synchronized void setEstimatedFuelTime(String estimatedFuelTime) {
        this.estimatedFuelTime = estimatedFuelTime;
        this.changed = true;
    }

    public synchronized String getPilotName() {
        return removeNull(pilotName);
    }

    public synchronized void setPilotName(String pilotName) {
        this.pilotName = pilotName;
        this.changed = true;
    }

    public synchronized int getSoulsOnBoard() {
        return soulsOnBoard;
    }

    public synchronized void setSoulsOnBoard(int soulsOnBoard) {
        this.soulsOnBoard = soulsOnBoard;
        this.changed = true;
    }

    public synchronized String getRemarks() {
        return removeNull(remarks);
    }

    public synchronized void setRemarks(String remarks) {
        this.remarks = remarks;
        this.changed = true;
    }

    // ----------------------- end of bean ---------------------

    public synchronized boolean isChanged() {
        return changed;
    }

    private String removeNull(String s) {
        return s != null ? s : "";
    }

    public synchronized boolean isOwnedByMe(AirportData data) {
        return data.getCallSign().equals(this.getOwner());
    }

    public synchronized boolean isOfferedToMe(AirportData data) {
        return data.getCallSign().equals(this.getHandover());
    }

    public static ComboBoxModel<String> getFpTypeModel() {
        if (flightPlanTypeModel == null) {
            flightPlanTypeModel = new FlightPlanTypeModel();
        }
        return flightPlanTypeModel;
    }

    public static class FlightPlanTypeModel extends DefaultComboBoxModel<String> {
        private static final long serialVersionUID = 1L;

        public FlightPlanTypeModel() {
            for (FlightType ft : FlightType.values()) {
                this.addElement(ft.toString());
            }
        }
    }

    public synchronized String getArrivalTime() {
        // calculate!
        return null;
    }

    public synchronized void reset(AirportData data) {
        this.assignedAltitude = null;
        this.assignedRoute = null;
        this.assignedRunway = null;

        this.fpStatus = FlightPlanStatus.FILED.toString();
        this.aircraft = contact.getAircraftCode();
        this.departureAirport = (contact.getRadarContactDistanceD() < 10 && contact.getGroundSpeedD() < 5) ? data.getAirportCode() : "";
        this.departure = (contact.getRadarContactDistanceD() < 10 && contact.getGroundSpeedD() < 5) ? FpTimeUtil.getUTCTimeString4Digits(new Date()) : "";
        this.cruisingAltitude = null;
        this.route = null;
        this.destinationAirport = null;
        this.alternativeDestinationAirports = null;
        this.estimatedFlightTime = null;
        this.estimatedFuelTime = null;

    }

    public synchronized void startFromHere(AirportData data) {
        this.assignedAltitude = null;
        this.assignedRoute = null;
        this.assignedRunway = null;

        this.fpStatus = FlightPlanStatus.FILED.toString();
        this.aircraft = contact.getAircraftCode();
        this.departureAirport = (contact.getRadarContactDistanceD() < 10 && contact.getGroundSpeedD() < 5) ? data.getAirportCode() : "";
        this.departure = (contact.getRadarContactDistanceD() < 10 && contact.getGroundSpeedD() < 5) ? FpTimeUtil.getUTCTimeString4Digits(new Date()) : "";
        this.cruisingAltitude = null;
        this.route = null;
        this.destinationAirport = null;
        this.alternativeDestinationAirports = null;
        this.estimatedFlightTime = null;
        this.estimatedFuelTime = null;
    }

    public synchronized void landHere(AirportData data) {
        this.destinationAirport = data.getAirportCode();
    }

    public synchronized void releaseControl() {
        this.owner = null;
        this.handover = null;
        contact.setAlignment(Alignment.RIGHT);
    }

    public synchronized void takeControl(AirportData dataRegistry) {
        this.owner = dataRegistry.getCallSign();
        this.handover = null;
        contact.setAlignment(Alignment.LEFT);
    }

    public boolean contactWillLandHere() {
        return destinationAirport != null && destinationAirport.contains(airportData.getAirportCode());
    }

    public String toString() {
        return flightCode+": "+callsign+" "+departureAirport+"("+departure+") > "+ destinationAirport+" ("+aircraft+","+fpStatus+")";
    }
}
