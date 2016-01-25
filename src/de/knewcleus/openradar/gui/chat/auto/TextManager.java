/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.chat.auto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

public class TextManager implements ListModel<AtcMenuChatMessage> {

    private final List<String> languages = Collections.synchronizedList(new ArrayList<String>());
    private final List<AtcMenuChatMessage> menuMessages = Collections.synchronizedList(new ArrayList<AtcMenuChatMessage>());
    private final Map<String, String> chatMessages = Collections.synchronizedMap(new TreeMap<String, String>());

    private final List<ListDataListener> dataListeners = Collections.synchronizedList(new ArrayList<ListDataListener>());

    public TextManager() {
        reloadTexts();
    }

    public void reloadTexts() {
        languages.clear();
        menuMessages.clear();
        chatMessages.clear();
        AutoTextReader.loadTexts(languages, menuMessages, chatMessages);
    }

    public synchronized void add(AtcMenuChatMessage msg) {
        menuMessages.add(msg);
    }

    public synchronized List<String> generateMessagesFor(GuiMasterController master, GuiRadarContact contact, int index, String additionalLanguage) {
        return menuMessages.get(index).generateMessages(master, contact, additionalLanguage);
    }

    public synchronized List<String> getLanguages() {
        return languages;
    }

    public String getChatMessageForAllias(String aliasWithoutActivationChar) {
        return chatMessages.get(aliasWithoutActivationChar.toLowerCase());
    }

    // ListModel

    @Override
    public synchronized int getSize() {
        return menuMessages.size();
    }

    @Override
    public synchronized AtcMenuChatMessage getElementAt(int index) {
        return menuMessages.get(index);
    }

    @Override
    public synchronized void addListDataListener(ListDataListener l) {
        dataListeners.add(l);
    }

    @Override
    public synchronized void removeListDataListener(ListDataListener l) {
        dataListeners.remove(l);
    }

}
