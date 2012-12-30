package de.knewcleus.openradar.gui.chat.auto;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

public class TextManager implements ListModel<AtcMessage> {

    private List<String> languages = new ArrayList<String>();
    private List<AtcMessage> messages = new ArrayList<AtcMessage>();

    private List<ListDataListener> dataListeners = new ArrayList<ListDataListener>();
    
    public TextManager() {
        AutoTextReader.loadTexts(languages, messages);
    }
    
    public void add(AtcMessage msg) {
        messages.add(msg);
    }
    
    public List<String> generateMessagesFor(GuiMasterController master, GuiRadarContact contact, int index, String additionalLanguage) {
        return messages.get(index).generateMessages(master, contact,additionalLanguage);
    }
    
    public List<String> getLanguages() {
        return languages;
    }
    
    // ListModel
    
    @Override
    public int getSize() {
        return messages.size();
    }

    @Override
    public AtcMessage getElementAt(int index) {
        return messages.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        dataListeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        dataListeners.remove(l);
    }

}
