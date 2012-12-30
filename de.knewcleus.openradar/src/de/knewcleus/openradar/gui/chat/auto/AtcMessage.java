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

public class AtcMessage {

    private final String displayMessage;
    private Map<String,String> translations = new TreeMap<String, String>();
    private List<String> variables = new ArrayList<String>();

    public AtcMessage(String displayMessage) {
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

        result.add(replaceVariables(translations.get("en"), master, contact));

        if(additionalLanguage!=null) {
            if(!"en".equals(additionalLanguage)) {
                String localizedText = replaceVariables(translations.get(additionalLanguage), master, contact); 
                if(!localizedText.trim().isEmpty()) {
                    result.add(localizedText);
                }
            }
        }        
        return result;
    }

    private String replaceVariables(String text, GuiMasterController master, GuiRadarContact contact) {
        AirportData data = master.getDataRegistry();
        MetarData metar = master.getMetarReader().getMetar();
        
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
            if("/environment/pressure-sea-level-inhg".equals(varName)) {
                values.add(metar.getPressureInHG());
            } else if("/environment/wind-speed-kt".equals(varName)) {
                values.add((float)metar.getWindSpeed()); // todo add gusts
            } else if("/instrumentation/comm/frequencies/selected-mhz".equals(varName)) {
                if(master.getRadioManager().getModels().isEmpty()) {
                    values.add(new Double("0"));
                } else {
                    values.add(Double.parseDouble(master.getRadioManager().getModels().get("COM0").getSelectedItem().getFrequency())); // todo multiple frequencies?
                }
            } else if("/sim/atc/activeRW".equals(varName)) {
                values.add(master.getStatusManager().getActiveRunways()); 
            } else if("/sim/atc/wind-from-display".equals(varName)) {
                values.add((float)metar.getWindDirection()); // todo add variation
            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APalt".equals(varName)) {
                values.add(data.getElevationFt()); 
            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APname".equals(varName)) {
                values.add(data.getAirportName()); 
            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target".equals(varName)) {
                values.add(master.getRadarContactManager().getSelectedContact().getCallSign()); 
            } else if("/sim/tower/airport-id".equals(varName)) {
                values.add(data.getAirportCode()); 
            } else if("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target-range".equals(varName)) {
                values.add(contact.getRadarContactDistanceD()); 
            }
        }
        
        return String.format(text,values.toArray()).trim()+" ";
    }
    
    public String toString() {
        return displayMessage;
    }
}
