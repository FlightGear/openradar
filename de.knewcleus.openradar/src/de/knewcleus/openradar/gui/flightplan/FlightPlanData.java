/**
 * Copyright (C) 2013 Wolfram Wagner
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
package de.knewcleus.openradar.gui.flightplan;

import java.util.Date;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;

public class FlightPlanData {

    public enum FlightPlanStatus { FILED, ACTIVE, CLOSED, EXPIRED, DELETED };
    public enum FlightType { VFR, IFR };

    private final GuiRadarContact contact;

    private String assignedAltitude;
    private String assignedRoute;
    private String assignedRunway;

    private String flightCode;
    private String callSign;
    private String owner;
    private String handover;
    private String squawk;
    private String fpStatus;
    private String type;
    private String aircraft;
    private String departureAirport;
    private String departureTime;
    private String cruisingAltitude;
    private String route;
    private String destinationAirport;
    private String alternativeDestinationAirports;
    private String estimatedFlightTime;
    private String estimatedFuelTime;
    private String pilotName;
    private int soulsOnBoard;
    private String remarks;



    public FlightPlanData(AirportData airportData, GuiRadarContact contact) {

        this.assignedAltitude = null;
        this.assignedRoute = null;
        this.assignedRunway = null;

        this.contact = contact;
        this.callSign = contact.getCallSign();
        this.owner = airportData.getCallSign();
        this.handover = null;
        this.squawk = ""+contact.getAssignedSquawk();
        this.fpStatus = FlightPlanStatus.FILED.toString();
        this.type = FlightType.IFR.toString();
        this.aircraft = contact.getAircraftCode();
        this.departureAirport = (contact.getRadarContactDistanceD()<10 && contact.getGroundSpeedD()<5) ? airportData.getAirportCode() : "";
        this.departureTime = FpTimeUtil.getUTCTimeString4Digits(new Date());
        this.cruisingAltitude = null;
        this.route = null;
        this.destinationAirport = null;
        this.alternativeDestinationAirports = null;
        this.estimatedFlightTime = null;
        this.estimatedFuelTime = null;
        this.pilotName = null;
        this.soulsOnBoard = 1;
        this.remarks = null;
    }

    public FlightPlanData(GuiRadarContact contact, String flightCode, String callSign, String owner, String handover, String squawk, String assignedAltitude,
            String fpStatus, String type, String aircraft, String departureAirport, String departureTime, String cruisingAltitude, String route,
            String destinationAirport, String alternativeDestinationAirports, String estimatedFlightTime, String estimatedFuelTime, String pilotName,
            int soulsOnBoard, String remarks) {

        this.assignedAltitude = null;
        this.assignedRoute = null;
        this.assignedRunway = null;

        this.contact = contact;
        this.flightCode = flightCode;
        this.callSign = callSign;
        this.owner = owner;
        this.handover = handover;
        this.squawk = squawk;
        this.fpStatus = fpStatus;
        this.type = type;
        this.aircraft = aircraft;
        this.departureAirport = departureAirport;
        this.departureTime = departureTime;
        this.cruisingAltitude = cruisingAltitude;
        this.route = route;
        this.destinationAirport = destinationAirport;
        this.alternativeDestinationAirports = alternativeDestinationAirports;
        this.estimatedFlightTime = estimatedFlightTime;
        this.estimatedFuelTime = estimatedFuelTime;
        this.pilotName = pilotName;
        this.soulsOnBoard = soulsOnBoard;
        this.remarks = remarks;
    }

    public synchronized GuiRadarContact getContact() {
        return contact;
    }
    public synchronized String getFlightCode() {
        return flightCode;
    }
    public synchronized void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }
    public synchronized String getCallSign() {
        return callSign;
    }
    public synchronized void setCallSign(String callSign) {
        this.callSign = callSign;
    }
    public synchronized String getOwner() {
        return owner;
    }
    public synchronized void setOwner(String owner) {
        this.owner = owner;
    }
    public synchronized String getHandover() {
        return handover;
    }
    public synchronized void setHandover(String handover) {
        this.handover = handover;
    }
    public synchronized String getSquawk() {
        return squawk;
    }
    public synchronized void setSquawk(String squawk) {
        this.squawk = squawk;
    }
    public synchronized String getAssignedAltitude() {
        return assignedAltitude;
    }
    public synchronized void setAssignedAltitude(String assignedAltitude) {
        this.assignedAltitude = assignedAltitude;
    }
    public String getAssignedRoute() {
        return assignedRoute;
    }

    public void setAssignedRoute(String assignedRoute) {
        this.assignedRoute = assignedRoute;
    }

    public String getAssignedRunway() {
        return assignedRunway;
    }

    public void setAssignedRunway(String assignedRunway) {
        this.assignedRunway = assignedRunway;
    }

    public synchronized String getFpStatus() {
        return fpStatus;
    }
    public synchronized void setFpStatus(String fpStatus) {
        this.fpStatus = fpStatus;
    }
    public synchronized String getType() {
        return type;
    }
    public synchronized void setType(String type) {
        this.type = type;
    }
    public synchronized String getAircraft() {
        return aircraft;
    }
    public synchronized void setAircraft(String aircraft) {
        this.aircraft = aircraft;
    }
    public synchronized String getDepartureAirport() {
        return departureAirport;
    }
    public synchronized void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }
    public synchronized String getDepartureTime() {
        return departureTime;
    }
    public synchronized void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
    public synchronized String getCruisingAltitude() {
        return cruisingAltitude;
    }
    public synchronized void setCruisingAltitude(String cruisingAltitude) {
        this.cruisingAltitude = cruisingAltitude;
    }
    public synchronized String getRoute() {
        return route;
    }
    public synchronized void setRoute(String route) {
        this.route = route;
    }
    public synchronized String getDestinationAirport() {
        return destinationAirport;
    }
    public synchronized void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }
    public synchronized String getAlternativeDestinationAirports() {
        return alternativeDestinationAirports;
    }
    public synchronized void setAlternativeDestinationAirports(String alternativeDestinationAirports) {
        this.alternativeDestinationAirports = alternativeDestinationAirports;
    }
    public synchronized String getEstimatedFlightTime() {
        return estimatedFlightTime;
    }
    public synchronized void setEstimatedFlightTime(String estimatedFlightTime) {
        this.estimatedFlightTime = estimatedFlightTime;
    }
    public synchronized String getEstimatedFuelTime() {
        return estimatedFuelTime;
    }
    public synchronized void setEstimatedFuelTime(String estimatedFuelTime) {
        this.estimatedFuelTime = estimatedFuelTime;
    }
    public synchronized String getPilotName() {
        return pilotName;
    }
    public synchronized void setPilotName(String pilotName) {
        this.pilotName = pilotName;
    }
    public synchronized int getSoulsOnBoard() {
        return soulsOnBoard;
    }
    public synchronized void setSoulsOnBoard(int soulsOnBoard) {
        this.soulsOnBoard = soulsOnBoard;
    }
    public synchronized String getRemarks() {
        return remarks;
    }
    public synchronized void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // ----------------------- end of bean ---------------------

    public synchronized boolean isOwnedByMe(AirportData data) {
        return data.getCallSign().equals(this.getOwner());
    }

    public synchronized boolean isOfferedToMe(AirportData data) {
        return data.getCallSign().equals(this.getHandover());
    }

}
