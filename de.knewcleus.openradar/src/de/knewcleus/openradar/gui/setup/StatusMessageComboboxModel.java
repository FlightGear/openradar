package de.knewcleus.openradar.gui.setup;

import javax.swing.DefaultComboBoxModel;

/**
 * This combobox model exists to store status messages..
 * 
 * @author Wolfram Wagner
 */
public class StatusMessageComboboxModel extends DefaultComboBoxModel<String> {

    private static final long serialVersionUID = 1L;

    public void addNewStatusMessage(String message) {
        if(getSize()>0) {
            insertElementAt(message, 0);
        } else {
            addElement(message);
        }
        if(getSize()==100) {
            removeElementAt(99);
        }
    }
}
