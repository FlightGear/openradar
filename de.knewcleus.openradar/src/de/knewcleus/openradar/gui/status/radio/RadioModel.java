package de.knewcleus.openradar.gui.status.radio;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import de.knewcleus.openradar.gui.GuiMasterController;

/**
 * This class encapsulates the management of one radio and its display in
 * the JList. The backend is interfaced via IRadioBackend to be able to add more
 * backends later.
 * 
 * To add: * modes: active, listen, off (silent) * possibility set radio volume
 * 
 * @author Wolfram Waner
 * 
 */

public class RadioModel extends AbstractListModel<RadioFrequency> implements ComboBoxModel<RadioFrequency> {

    private static final long serialVersionUID = 1L;

    private String radioKey;

    private volatile List<RadioFrequency> frequencyList = new ArrayList<RadioFrequency>();
    private volatile Map<String,RadioFrequency> mapFrequencies = Collections.synchronizedMap(new HashMap<String,RadioFrequency>());
    private volatile RadioFrequency selectedFrequency = null;

    public RadioModel(GuiMasterController master, String radioKey, List<RadioFrequency> frequencies, int preselectedIndex) {
        this.radioKey = radioKey;
        this.frequencyList = new ArrayList<RadioFrequency>(frequencies);
        for(RadioFrequency f : frequencyList) {
            mapFrequencies.put(f.getFrequency(), f);
        }
        if (preselectedIndex > -1 && preselectedIndex < frequencyList.size())
            setSelectedItem(frequencyList.get(preselectedIndex));
    }

    public String getRadioKey() {
        return radioKey;
    }

    public String getCurrentAtcCode() {
        String cs = "";

        if (selectedFrequency != null) {
            cs = selectedFrequency.getCode();
        }
        return cs;
    }

    @Override
    public int getSize() {
        return frequencyList.size();
    }

    @Override
    public RadioFrequency getElementAt(int index) {
        return frequencyList.get(index);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if(this.selectedFrequency!=anItem) {
            if(anItem instanceof String) {
                this.selectedFrequency=setUserFrequency((String)anItem);
                fireContentsChanged(this, -1, -1);
            } else        
            if ((selectedFrequency != null && !selectedFrequency.equals(anItem)) || selectedFrequency == null && anItem != null) {
                this.selectedFrequency = (RadioFrequency) anItem;
                fireContentsChanged(this, -1, -1);
            }
        }
    }

    @Override
    public RadioFrequency getSelectedItem() {
        return selectedFrequency;
    }

    public RadioFrequency setUserFrequency(String newFrequency) {
        try{
            newFrequency = newFrequency.replaceAll(",",".");
            DecimalFormat df = new DecimalFormat("000.00");
            newFrequency = df.format(Double.parseDouble(newFrequency));
            newFrequency = newFrequency.replaceAll(",",".");
        } catch(Exception e) {
            newFrequency = "123.45";
        }
        
        
        RadioFrequency f = frequencyList.get(frequencyList.size()-1);
        if(f.getCode().equalsIgnoreCase("Manual")) {
            frequencyList.remove(f);
        }
        f = new RadioFrequency("Manual", newFrequency);
        frequencyList.add(f);
        f.setFrequency(newFrequency);
        
        return f;
    }

    public boolean containsFrequency(String f) {
        return mapFrequencies.containsKey(f);
    }

    public RadioFrequency get(String frequency) {
        return mapFrequencies.get(frequency);
    }
    
    
}
