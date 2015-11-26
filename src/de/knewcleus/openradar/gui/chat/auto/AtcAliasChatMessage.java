/**
 * Copyright (C) 2012-2015 Wolfram Wagner
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

import org.apache.log4j.Logger;

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
        String aliasPrefix = master.getAirportData().getChatAliasPrefix();


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
                    resolvedMessage=resolvedMessage.replace("\\.", "{{dot}}");
                    resolvedMessage = containsUnresolvedAlias(aliasPrefix, resolvedMessage) ? null : resolvedMessage;
                    resolvedMessage= resolvedMessage!=null ? resolvedMessage.replace("{{dot}}",".") : null;
                } else {
                    resolvedMessage = null;
                }
            }

        } catch(Exception e) {
            Logger.getLogger(AtcAliasChatMessage.class).warn("Problem to resolve alias in message: "+sMessage,e);
            resolvedMessage = null;
        }
    }

    public static boolean containsUnresolvedAlias(String prefix, String msg) {
        int pos = msg.indexOf(prefix);

        if(pos!=-1) {
            if(pos+1==msg.length()) return false;
    
            
            if(msg.matches(".*https?:.*")) return false; // URLs ... 
            
            if(!msg.substring(pos+1, pos+2).matches("[\\d\\s]") ) return true; // a dot may be followed by a digit, \D means anything but a digit
        }
        return false;
    }



    public String replaceChatTags(String text, GuiMasterController master, List<String> arguments ) {
        AirportData data = master.getAirportData();
        MetarData metar = master.getMetarReader().getMetar(data.getMetarSource());
        GuiRadarContact contact = master.getRadarContactManager().getSelectedContact();

        argumentsToRemember.clear();

        /*
            <icao>               Local departing airport ICAO code
            <aptname>            Local departing airport name
            <icao-altitude>      Local departing airport altitude
            <transitionAlt>      TransitionAlt for this airport
            <transitionFL>       TransitionAlt for this airport
            <alt>                Aircraft's current altitude
            <distance>           Aircraft's current distance to local airport
            <callsign>           ATC callsign.
            <aircraft>           Aircraft's callsign
            <com0>               ATC primary frequency
            <com1>               ATC secondary frequency
            <winds>              Current winds
            <wind-direction>     Current winds direction
            <wind-speed>         Current winds speed
            <metar>              Raw METAR of the local airport
            <atis>               Current ATIS of the local airport
            <hPa>                Current QNH of the local airport in hPa
            <mmHg>               Current QNH of the local airport in mmHg
            <runways>            Current active runways
            <runways-land>       Current active LANDING runways
            
            <assigned-runway>    Runway the pilot can expect for his aircraft
            <cruise-altitude>    Aircraft's filed cruise altitude
            <destination>        Aircraft's destination airport ICAO code
            <squawk>             Assigned squawk code
            <route>              Code of assigned SID/STAR
            <assigned-route>     Code of assigned SID/STAR
         */

        text = replaceAllInsensitive(text,"<icao>",data.getAirportCode());
        text = replaceAllInsensitive(text,"<aptname>",data.getAirportName());
        text = replaceAllInsensitive(text,"<icao-altitude>",String.format("%03.0f",data.getElevationFt()));
        text = replaceAllInsensitive(text,"<transitionAlt>",String.format("%dft",data.getTransitionAlt()));
        text = replaceAllInsensitive(text,"<transitionFL>",String.format("FL%03d",data.getTransitionFL(master)));
        text = replaceAllInsensitive(text,"<alt>",contact!=null ? String.format("%1.0f",contact.getAltitude()):"");
        text = replaceAllInsensitive(text,"<distance>",contact!=null ? contact.getRadarContactDistance():"");
        text = replaceAllInsensitive(text,"<callsign>",contact!=null ? contact.getCallSign() : "");
        text = replaceAllInsensitive(text,"<aircraft>",contact!=null ? contact.getAircraftCode() : "");
        if(text.contains("<com_all>")) {
            String v;
            if(master.getRadioManager().getModels().isEmpty()) {
                v="";
            } else {
                v = "; FGCOM "+master.getRadioManager().getActiveFrequenciesForDisplay();
            }
            if(master.getAirportData().isAltRadioTextEnabled()) {
                v+="; "+master.getAirportData().getAltRadioText();
            }
            text = replaceAllInsensitive(text,"<com_all>",v);
        }
            
        if(master.getRadioManager().getModels().isEmpty()) {
            text = replaceAllInsensitive(text,"<com0>","");
        } else {
            text = replaceAllInsensitive(text,"<com0>",(master.getRadioManager().getModels().get("COM0").getSelectedItem().getFrequency()));
        }
        if(master.getRadioManager().getModels().size()<2) {
            text = replaceAllInsensitive(text,"<com1>","");
        } else {
            text = replaceAllInsensitive(text,"<com1>",(master.getRadioManager().getModels().get("COM1").getSelectedItem().getFrequency()));
        }
        if(master.getRadioManager().getModels().size()<3) {
            text = replaceAllInsensitive(text,"<com2>","");
        } else {
            text = replaceAllInsensitive(text,"<com2>",(master.getRadioManager().getModels().get("COM2").getSelectedItem().getFrequency()));
        }
        if(master.getRadioManager().getModels().size()<4) {
            text = replaceAllInsensitive(text,"<com3>","");
        } else {
            text = replaceAllInsensitive(text,"<com3>",(master.getRadioManager().getModels().get("COM3").getSelectedItem().getFrequency()));
        }
        text = replaceAllInsensitive(text,"<winds>",metar.getWindDisplayString());
        text = replaceAllInsensitive(text,"<wind-direction>",metar.getWindDirectionRounded());
        text = replaceAllInsensitive(text,"<wind-speed>",""+metar.getWindSpeed());
        text = replaceAllInsensitive(text,"<metar>", metar.getMetarBaseData());
        text = replaceAllInsensitive(text,"<atis>",metar.createATIS(master, true).get(0));
        text = replaceAllInsensitive(text,"<hPa>",String.format("%4.0f",metar.getPressureHPa()));
        text = replaceAllInsensitive(text,"<mmHg>",String.format("%2.2f",metar.getPressureInHG()));
        text = replaceAllInsensitive(text,"<runways>",master.getStatusManager().getActiveRunways());
        text = replaceAllInsensitive(text,"<runways-land>",master.getStatusManager().getActiveLandingRunways());

//        <expected-runway>    Runway the pilot can expect for his aircraft
        text = replaceAllInsensitive(text,"<assigned-runway>", contact!=null ? contact.getFlightPlan().getAssignedRunway() : "");
        text = replaceAllInsensitive(text,"<cruise-altitude>",  contact!=null ?  contact.getFlightPlan().getCruisingAltitude():"");
        text = replaceAllInsensitive(text,"<destination>",  contact!=null ? contact.getFlightPlan().getDestinationAirport() :"");
        text = replaceAllInsensitive(text,"<squawk>", contact!=null ? ""+contact.getAssignedSquawk():"");
        if(contact!=null && text.toLowerCase().contains("<squawk-next>")) {
            master.getRadarContactManager().assignSquawkCode();
            text = replaceAllInsensitive(text,"<squawk-next>", ""+contact.getAssignedSquawk());
        }

        text = replaceAllInsensitive(text,"<route>", contact!=null ? contact.getFlightPlan().getRoute():"");
        text = replaceAllInsensitive(text,"<assigned-route>", contact!=null ? contact.getFlightPlan().getAssignedRoute():"");
        // replace the dynamic variables
        int pos = -1;

        for(int i=0 ; 0<arguments.size(); i++) {
            // check for values to be remembered
            String searchFor = "{"+i;
            String arg = arguments.get(0);
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
                    selectedContact.getFlightPlan().setReadyForTransmission();
                    master.getFlightPlanExchangeManager().triggerTransmission();
                } else if(key.equals("RWY")) {
                    selectedContact.getFlightPlan().setAssignedRunway(argumentsToRemember.get(key));
                    selectedContact.getFlightPlan().setReadyForTransmission();
                    master.getFlightPlanExchangeManager().triggerTransmission();
                } else if(key.equals("ROUTE")) {
                    selectedContact.getFlightPlan().setAssignedRoute(argumentsToRemember.get(key));
                    selectedContact.getFlightPlan().setReadyForTransmission();
                    master.getFlightPlanExchangeManager().triggerTransmission();
                } else if(key.equals("SQUAWK")) {
                    try {
                        int s = Integer.parseInt(argumentsToRemember.get(key));
                        if(s>0 && s%10<8 && s/1000<8) {
                            selectedContact.getFlightPlan().setSquawk(""+s);
                            selectedContact.getFlightPlan().setReadyForTransmission();
                            master.getFlightPlanExchangeManager().triggerTransmission();
                        }
                    } catch(Exception e) {}
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

    public String replaceAllInsensitive(String text, String searchFor, String replaceWith) {
        String searchText = text.toLowerCase();
        String searchForLC = searchFor.toLowerCase();

        while(searchText.contains(searchForLC)) {
            int start = searchText.indexOf(searchForLC);
            int end = start+searchFor.length();
            searchText = searchText.substring(0,start)+replaceWith+text.substring(end);
            text = text.substring(0,start)+replaceWith+text.substring(end);
        }
        return text;
    }
}
