/**
 * Copyright (C) 2012,2015 Wolfram Wagner
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
package de.knewcleus.openradar.gui.chat.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.weather.MetarData;

public class AtcMenuChatMessage {

    private final String displayMessage;
    private Map<String,String> translations = new TreeMap<String, String>();
    private List<String> variables = new ArrayList<String>();

    public AtcMenuChatMessage(String displayMessage) {
        this.displayMessage=displayMessage;
    }

    public void setVariables(String variableList) {
        StringTokenizer st = new StringTokenizer(variableList,",");
        while (st.hasMoreElements()) {
            variables.add(st.nextToken().trim());
        }
    }

    public void addTranslation(String language, String text) {
        translations.put(language, text);
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public List<String> generateMessages(GuiMasterController master, GuiRadarContact contact, String additionalLanguage) {
        List<String> result = new ArrayList<String>();

        result.add(replaceVariables(translations.get("en"), master, contact, variables));

        if(additionalLanguage!=null) {
            if(!"en".equals(additionalLanguage)) {
                String localizedText = replaceVariables(translations.get(additionalLanguage), master, contact, variables);
                if(!localizedText.trim().isEmpty()) {
                    result.add(localizedText);
                }
            }
        }
        return result;
    }

    public static String replaceVariables(String text, GuiMasterController master, GuiRadarContact selectedContact, List<String> variables ) {
        AirportData data = master.getAirportData();
        MetarData metar = master.getMetarReader().getMetar(data.getMetarSource());

        /*
         *
         * /environment/pressure-sea-level-inhg,
         * /environment/wind-speed-kt
         * /instrumentation/comm/frequencies/selected-mhz
         * /sim/atc/activeRW
         * /sim/atc/wind-from-display
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APalt
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APname
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target
         * /sim/tower/airport-id
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target-range
         *
         */
        ArrayList<Object> values = new ArrayList<Object>();
        for(String varName : variables) {
            if("/openradar/metar/pressure-sea-level".equals(varName)) {
                values.add(metar.getPressureDisplayString());

            } else if("/instrumentation/comm/frequencies/selected-mhz".equals(varName)) {
                if(master.getRadioManager().getModels().isEmpty()) {
                    values.add(new Double("0"));
                } else {
                    values.add(Double.parseDouble(master.getRadioManager().getModels().get("COM0").getSelectedItem().getFrequency())); // todo multiple frequencies?
                }
            } else if("/openradar/comm/frequencies".equals(varName)) {
                String v;
                if(master.getRadioManager().getModels().isEmpty()) {
                    v="";
                } else {
                    v = " FGCOM " + master.getRadioManager().getActiveFrequenciesForDisplay();
                }
                if(master.getAirportData().isAltRadioTextEnabled()) {
                    if(v.length()>0) {
                       v+="; ";
                    }
                    v+=master.getAirportData().getAltRadioText();
                }
                values.add(v);
            } else if("/openradar/transitionAlt".equals(varName)) {
                values.add(String.format("%dft",data.getTransitionAlt()));

            } else if("/openradar/transitionFL".equals(varName)) {
                values.add(String.format("FL%03d",data.getTransitionFL(master)));

            } else if("/sim/atc/activeRW".equals(varName)) {
                values.add(master.getStatusManager().getActiveRunways());

            } else if("/openradar/activeLandingRW".equals(varName)) {
                values.add(master.getStatusManager().getActiveLandingRunways());

            } else if("/openradar/activeStartingRW".equals(varName)) {
                values.add(master.getStatusManager().getActiveLandingRunways());

            } else if("/sim/atc/wind-from-display".equals(varName)) {
                values.add((float)metar.getWindDirectionI());

            } else if("/environment/wind-speed-kt".equals(varName)) {
                values.add((float)metar.getWindSpeed()); // todo add gusts

            } else if("/openradar/metar/wind".equals(varName)) {
                values.add(metar.getWindDisplayString());

            } else if("/openradar/metar/visibility".equals(varName)) {
                values.add(metar.getVisibility()+""+metar.getVisibilityUnit());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APalt".equals(varName)) {
                values.add(data.getElevationFt());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APname".equals(varName)) {
                values.add(data.getAirportName());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target".equals(varName)) {
                values.add(selectedContact!=null ? selectedContact.getCallSign() : "");

            } else if("/sim/tower/airport-id".equals(varName)) {
                values.add(data.getAirportCode());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target-range".equals(varName)) {
                values.add(selectedContact.getRadarContactDistanceD());
            }
        }

        return String.format(text,values.toArray()).trim()+" ";
    }

    public String toString() {
        return displayMessage;
    }

    public List<String> generateMessagesWithArgs(GuiMasterController master, GuiRadarContact contact, String additionalLanguage, Object... args) {
        List<String> result = new ArrayList<String>();

        result.add(replaceVariablesWithArgs(translations.get("en"), master, contact, variables, args));

        if(additionalLanguage!=null) {
            if(!"en".equals(additionalLanguage)) {
                String localizedText = replaceVariablesWithArgs(translations.get(additionalLanguage), master, contact, variables, args);
                if(!localizedText.trim().isEmpty()) {
                    result.add(localizedText);
                }
            }
        }
        return result;
    }

    public static String replaceVariablesWithArgs(String text, GuiMasterController master, GuiRadarContact selectedContact, List<String> variables, Object... args) {
        AirportData data = master.getAirportData();
        MetarData metar = master.getMetarReader().getMetar(data.getMetarSource());

        /*
         *
         * /environment/pressure-sea-level-inhg,
         * /environment/wind-speed-kt
         * /instrumentation/comm/frequencies/selected-mhz
         * /sim/atc/activeRW
         * /sim/atc/wind-from-display
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APalt
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APname
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target
         * /sim/tower/airport-id
         * /sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target-range
         *
         */
        ArrayList<Object> values = new ArrayList<Object>();
        for(String varName : variables) {
            if("/openradar/metar/pressure-sea-level".equals(varName)) {
                values.add(metar.getPressureDisplayString());

            } else if("/instrumentation/comm/frequencies/selected-mhz".equals(varName)) {
                if(master.getRadioManager().getModels().isEmpty()) {
                    values.add(new Double("0"));
                } else {
                    values.add(Double.parseDouble(master.getRadioManager().getModels().get("COM0").getSelectedItem().getFrequency())); // todo multiple frequencies?
                }
            } else if("/openradar/comm/frequencies".equals(varName)) {
                String v;
                if(master.getRadioManager().getModels().isEmpty()) {
                    v="";
                } else {
                    v = " FGCOM " + master.getRadioManager().getActiveFrequenciesForDisplay();
                }
                if(master.getAirportData().isAltRadioTextEnabled()) {
                    if(v.length()>0) {
                       v+="; ";
                    }
                    v+=master.getAirportData().getAltRadioText();
                }
                values.add(v);
            } else if("/openradar/transitionAlt".equals(varName)) {
                values.add(String.format("%dft",data.getTransitionAlt()));

            } else if("/openradar/transitionFL".equals(varName)) {
                values.add(String.format("FL%03d",data.getTransitionFL(master)));

            } else if("/sim/atc/activeRW".equals(varName)) {
                values.add(master.getStatusManager().getActiveRunways());

            } else if("/openradar/activeLandingRW".equals(varName)) {
                values.add(master.getStatusManager().getActiveLandingRunways());

            } else if("/sim/atc/wind-from-display".equals(varName)) {
                values.add((float)metar.getWindDirectionI());

            } else if("/environment/wind-speed-kt".equals(varName)) {
                values.add((float)metar.getWindSpeed()); // todo add gusts

            } else if("/openradar/metar/wind".equals(varName)) {
                values.add(metar.getWindDisplayString());

            } else if("/openradar/metar/visibility".equals(varName)) {
                values.add(metar.getVisibility()+""+metar.getVisibilityUnit());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APalt".equals(varName)) {
                values.add(data.getElevationFt());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APname".equals(varName)) {
                values.add(data.getAirportName());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target".equals(varName)) {
                values.add(selectedContact!=null ? selectedContact.getCallSign() : "");

            } else if("/sim/tower/airport-id".equals(varName)) {
                values.add(data.getAirportCode());

            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target-range".equals(varName)) {
                values.add(selectedContact.getRadarContactDistanceD());
            }
        }
        
        for (Object arg : args) {
        	values.add(arg);
        }
        
        return String.format(text, values.toArray()).trim() + " ";
    }

}
