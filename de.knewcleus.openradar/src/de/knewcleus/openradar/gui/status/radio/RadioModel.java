package de.knewcleus.openradar.gui.status.radio;

import java.util.ArrayList;
import java.util.List;

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
    private volatile RadioFrequency selectedFrequency = null;

    public RadioModel(GuiMasterController master, String radioKey, List<RadioFrequency> frequencies, int preselectedIndex) {
        this.radioKey = radioKey;
        this.frequencyList = new ArrayList<RadioFrequency>(frequencies);
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
        if ((selectedFrequency != null && !selectedFrequency.equals(anItem)) || selectedFrequency == null && anItem != null) {
            this.selectedFrequency = (RadioFrequency) anItem;
            fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public RadioFrequency getSelectedItem() {
        return selectedFrequency;
    }
    
    
}
