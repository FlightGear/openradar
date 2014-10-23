/**
 * Copyright (C) 2014 Wolfram Wagner
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
package de.knewcleus.openradar.gui.flightplan.lenny64;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.setup.AirportData;
/**
 * This class is responsible for parsing the xml delivered by lenny's server
 * 
 * @author Wolfram Wagner
 *
 */
public class Lenny64XmlParser {

    private static final Logger log = LogManager.getLogger(Lenny64XmlParser.class.getName());

    public static List<FlightPlanData> parse(AirportData data, GuiRadarContact contact, Document document) {
        List<FlightPlanData> result = new ArrayList<FlightPlanData>();
       
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String today = sdf.format(new Date());

      SimpleDateFormat xmlTimeFormat = new SimpleDateFormat("HH:mm:ss");
      SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");
        
        List<Element> eFlightPlans = document.getRootElement().getChildren("flightplan");
        if(eFlightPlans!=null) {
            for (Element eFp : eFlightPlans) {
                String flightplanId = eFp.getChildText("flightplanId");
                String callsign = eFp.getChildText("callsign");
                String flightNumber = eFp.getChildText("flightNumber");
                String airportFrom = eFp.getChildText("airportFrom");
                String airportTo = eFp.getChildText("airportTo");
                String alternateDestination = eFp.getChildText("alternateDestination");
                String cruiseAltitude = eFp.getChildText("cruiseAltitude");
                String trueAirspeed = eFp.getChildText("trueAirspeed");
                String dateDeparture = eFp.getChildText("dateDeparture");
                String dateArrival = eFp.getChildText("dateArrival");
                String departureTime = eFp.getChildText("departureTime");
                String arrivalTime = eFp.getChildText("arrivalTime");
                String aircraft = eFp.getChildText("aircraft");
                String soulsOnBoard = eFp.getChildText("soulsOnBoard");
                String fuelTime = eFp.getChildText("fuelTime");
                String pilotName = eFp.getChildText("pilotName");
                String waypoints = eFp.getChildText("waypoints");
                String category = eFp.getChildText("category");
//                String comments = eFp.getChildText("comments");
//                StringBuilder messages = new StringBuilder();
//                List<Element> eComments = document.getRootElement().getChild("comments").getChildren("comment");
//                for (Element eComment : eComments) {
//                    String user = eComment.getChildText("user");
//                    String message = eComment.getChildText("message");
//                    messages.append(user).append(": ").append(message).append("; ");
//                }
                String status = eFp.getChildText("status");
//                String additionalInformation = eFp.getChildText("additionalInformation");
//                String lastUpdated = eFp.getChildText("lastUpdated");
                
                if( /*(airportFrom.equals(data.getAirportCode()) || airportTo.equals(data.getAirportCode()) )
                    &&*/ (dateDeparture.equals(today) || dateArrival.equals(today))
                        ) {
                    // check for departure date
                    try {
                        FlightPlanData fpd = contact.getFlightPlan().copy(); // creates a copy of the existing record
                        // now merge the data we have got and which we do not already know
                        fpd.setFlightPlanId(flightplanId);
                        fpd.setAircraft(aircraft);
                        fpd.setAlternativeDestinationAirports(alternateDestination);
                        // callsign is already known
                        cruiseAltitude = cruiseAltitude.equals("FL")?"":cruiseAltitude;
                        fpd.setCruisingAltitude(cruiseAltitude);
                        departureTime = shortTimeFormat.format(xmlTimeFormat.parse(departureTime));
                        fpd.setDeparture(departureTime); // todo
                        fpd.setDepartureAirport(airportFrom);
                        fpd.setDestinationAirport(airportTo);
                        arrivalTime = shortTimeFormat.format(xmlTimeFormat.parse(arrivalTime));
                        //fpd.setEstimatedFlightTime(estimatedFlightTime); // todo
                        fpd.setEstimatedFuelTime(fuelTime);
                        fpd.setFlightCode(flightNumber);
                        status = status.equals("")?FlightPlanData.FlightPlanStatus.ACTIVE.toString():status.replaceAll("filled", "FILED");
                        fpd.setFpStatus(status);
                        fpd.setPilotName(pilotName);
                       // fpd.setRemarks(messages.toString());
                        fpd.setRoute(waypoints);
                        fpd.setSoulsOnBoard(Integer.parseInt(soulsOnBoard));
                        trueAirspeed = trueAirspeed.equals("TAS")?"":trueAirspeed;
                        fpd.setTrueAirspeed(trueAirspeed);
                        fpd.setType(category);
                        result.add(fpd);
                    } catch(Exception e) {
                        log.error("Error while parsing flightplan #"+flightNumber+" for "+callsign);
                    }
                } else {
                    log.trace("Skipping flightplan, it is for another airport: #"+flightNumber+" from:"+airportFrom+" to:"+airportTo);
                }
            }
        }
            
        
        return result;
    }

}
