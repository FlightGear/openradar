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

    public RadioModel(GuiMasterController master, String radioKey, List<RadioFrequency> frequencies, String restoredFrequency, int preselectedIndex) {
        this.radioKey = radioKey;
        this.frequencyList = new ArrayList<RadioFrequency>(frequencies);
        for(RadioFrequency f : frequencyList) {
            mapFrequencies.put(f.getFrequency(), f);
        }
        if(restoredFrequency!=null) {
            // restore last settings
            if(mapFrequencies.containsKey(restoredFrequency)) {
                selectedFrequency = mapFrequencies.get(restoredFrequency);
            } else {
                selectedFrequency = setUserFrequency(restoredFrequency);
            }
        } else {
            // automatic pre-selection
            if (preselectedIndex > -1 && preselectedIndex < frequencyList.size()) {
                setSelectedItem(frequencyList.get(preselectedIndex));
            }
        }
        if(radioKey.equals("COM0")) {
            master.getMpChatManager().getMpBackend().setFrequency(selectedFrequency.getFrequency());
        }
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
