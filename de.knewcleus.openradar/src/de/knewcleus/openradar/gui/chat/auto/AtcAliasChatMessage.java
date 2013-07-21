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
package de.knewcleus.openradar.gui.chat.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.sun.istack.internal.logging.Logger;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.weather.MetarData;
/**
 * This class is responsible to resolve an alias based text message into the final message.
 * The resolved alias is returned to be displayed to the user, before it is actually sent.
 * While sending, the attributes that need to be remembered (assignements) are stored.
 *
 * @author Wolfram Wagner
 *
 */
public class AtcAliasChatMessage {

    private Map<String, String> argumentsToRemember = new TreeMap<String, String>();

    private String resolvedMessage = null;

    public AtcAliasChatMessage(GuiMasterController master, String sMessage) {
        TextManager textManager = master.getRadarContactManager().getTextManager();
        String aliasPrefix = master.getDataRegistry().getChatAliasPrefix();


        StringBuilder result = new StringBuilder();
        try {
            int start = sMessage.indexOf(aliasPrefix);
            result.append(sMessage.substring(0,start)); // the part in front
            int end = sMessage.indexOf(" ",start);

            String alias = end>-1 ? sMessage.substring(start, end):sMessage.substring(start);
            if(alias.length()>aliasPrefix.length()+1) {
                // at least one char after the prefix

                ArrayList<String> args = new ArrayList<String>();
                if(end>-1) {
                    // collect arguments
                    StringTokenizer st = new StringTokenizer(sMessage.substring(end)," ");
                    while(st.hasMoreElements()) {
                        args.add(st.nextToken());
                    }
                }

                String msg = textManager.getChatMessageForAllias(alias.substring(aliasPrefix.length()));
                if(msg!=null) {
                    // alias could be resolved
                    String resMessage = replaceChatTags(msg, master, args);
                    // rebuild it
                    result.append(resMessage);
                    while(!args.isEmpty()) {
                        // unused args
                        result.append(" ");
                        result.append(args.remove(0));
                    }
                    resolvedMessage=result.toString();
                } else {
                    resolvedMessage = null;
                }
            }

        } catch(Exception e) {
            Logger.getLogger(AtcAliasChatMessage.class).warning("Problem to resolve alias in message: "+sMessage,e);
            resolvedMessage = null;
        }
    }

    public String replaceChatTags(String text, GuiMasterController master, List<String> arguments ) {
        AirportData data = master.getDataRegistry();
        MetarData metar = master.getMetarReader().getMetar(data.getMetarSource());
        GuiRadarContact contact = master.getRadarContactManager().getSelectedContact();

        argumentsToRemember.clear();

        /*
            <icao>               Local departing airport
            <icao-altitude>      Local departing airport altitude
            <alt>                Aircraft's current altitude
            <distance>           Aircraft's current distance to local airport
            <callsign>           ATC callsign.
            <aircraft>           Aircraft's callsign
            <com1>               ATC primary frequency
            <com2>               ATC secondary frequency
            <winds>              Current winds
            <wind-direction>     Current winds direction
            <wind-speed>         Current winds speed
            <metar>              Raw METAR of the local airport
            <atis>               Current ATIS of the local airport
            <qnh>                Current QNH of the local airport
            <runways>            Current active runways

            <expected-runway>    Runway the pilot can expect for his aircraft
            <assigned-runway>    Runway the pilot can expect for his aircraft
            <cruise-altitude>    Aircraft's filed cruise altitude
            <destination>        Aircraft's destination airport
            <squawk>             Assigned squawk code
            <route>              Code of assigned ATIS/STAR
         */

        text = text.replaceAll("<icao>",data.getAirportCode());
        text = text.replaceAll("<icao-altitude>",String.format("%03.0f",data.getElevationFt()));
        text = text.replaceAll("<alt>",String.format("%1.0f",contact.getAltitude()));
        text = text.replaceAll("<distance>",contact.getRadarContactDistance());
        text = text.replaceAll("<callsign>",contact!=null ? contact.getCallSign() : "");
        text = text.replaceAll("<aircraft>",contact!=null ? contact.getAircraftCode() : "");
        if(master.getRadioManager().getModels().isEmpty()) {
            text = text.replaceAll("<com0>","");
        } else {
            text = text.replaceAll("<com0>",(master.getRadioManager().getModels().get("COM0").getSelectedItem().getFrequency()));
        }
        if(master.getRadioManager().getModels().size()<2) {
            text = text.replaceAll("<com1>","");
        } else {
            text = text.replaceAll("<com1>",(master.getRadioManager().getModels().get("COM1").getSelectedItem().getFrequency()));
        }
        if(master.getRadioManager().getModels().size()<3) {
            text = text.replaceAll("<com2>","");
        } else {
            text = text.replaceAll("<com2>",(master.getRadioManager().getModels().get("COM2").getSelectedItem().getFrequency()));
        }
        if(master.getRadioManager().getModels().size()<4) {
            text = text.replaceAll("<com3>","");
        } else {
            text = text.replaceAll("<com3>",(master.getRadioManager().getModels().get("COM3").getSelectedItem().getFrequency()));
        }
        text = text.replaceAll("<winds>",metar.getWindDisplayString());
        text = text.replaceAll("<wind-direction>",metar.getWindDirectionRounded());
        text = text.replaceAll("<wind-speed>",""+metar.getWindSpeed());
        text = text.replaceAll("<metar>", metar.getMetarBaseData());
        text = text.replaceAll("<atis>",metar.createATIS(master, true).get(0));
        text = text.replaceAll("<qnh>",String.format("%4.1f",metar.getPressureInHG()));
        text = text.replaceAll("<mmHg>",String.format("%2.2f",metar.getPressureHPa()));
        text = text.replaceAll("<runways>",master.getStatusManager().getActiveRunways());
        text = text.replaceAll("<runways-land>",master.getStatusManager().getActiveLandingRunways());

//        <expected-runway>    Runway the pilot can expect for his aircraft
        text = text.replaceAll("<assigned-runway>", contact!=null ? contact.getFlightPlan().getAssignedRunway() : "");
        text = text.replaceAll("<cruise-altitude>",  contact!=null ?  contact.getFlightPlan().getCruisingAltitude():"");
        text = text.replaceAll("<destination>",  contact!=null ? contact.getFlightPlan().getDestinationAirport() :"");
        text = text.replaceAll("<squawk>", ""+contact!=null ? ""+contact.getAssignedSquawk():"");
        text = text.replaceAll("<route>", contact!=null ? contact.getFlightPlan().getRoute():"");

        // replace the dynamic variables
        int pos = -1;

        for(int i=0 ; i<arguments.size(); i++) {
            // check for values to be remembered
            String searchFor = "{"+i;
            String arg = arguments.get(i);
            if(!text.contains(searchFor)) {
                // the argument is not referenced, so this argument is additional text behind the alias
                break;
            }
            while((pos=text.indexOf(searchFor))>-1) {
                int posEnd = text.indexOf("}", pos);
                String wholeTag = text.substring(pos,posEnd+1);
                if(text.length()>pos+searchFor.length()
                     && text.charAt(pos+searchFor.length())=='.') {
                    String key = text.substring(pos+1+searchFor.length(),posEnd);
                    argumentsToRemember.put(key,arguments.get(0));
                }
                wholeTag = escapeForRegex(wholeTag);
                text = text.replaceFirst(wholeTag, arg);
            }
            arguments.remove(0);
        }

        return text;
    }

    private String escapeForRegex(String text) {
        text = text.replaceAll("\\{","\\\\{");
        text = text.replaceAll("\\}","\\\\}");
        text = text.replaceAll("\\[","\\\\[");
        text = text.replaceAll("\\]","\\\\]");
        text = text.replaceAll("\\.","\\\\.");
        return text;
    }

    public void storeArguments(GuiMasterController master) {
        GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();

        if(selectedContact!=null) {
            // store arguments
            for(String key : argumentsToRemember.keySet()) {
                if(key.equals("ALT")) {
                    selectedContact.getFlightPlan().setAssignedAltitude(argumentsToRemember.get(key));
                } else if(key.equals("RWY")) {
                    selectedContact.getFlightPlan().setAssignedRunway(argumentsToRemember.get(key));
                } else if(key.equals("ROUTE")) {
                    selectedContact.getFlightPlan().setAssignedRoute(argumentsToRemember.get(key));
                } else {
                    // unknown

                }
            }
        }
        argumentsToRemember.clear();
    }

    public String getResolvedMessage() {
        return resolvedMessage;
    }
}
