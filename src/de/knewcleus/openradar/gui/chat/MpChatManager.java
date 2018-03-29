/**
 * Copyright (C) 2012,2015-2016,2018 Wolfram Wagner
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
package de.knewcleus.openradar.gui.chat;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.knewcleus.fgfs.multiplayer.IChatListener;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.SoundManager;
import de.knewcleus.openradar.gui.chat.auto.AtcAliasChatMessage;
import de.knewcleus.openradar.gui.chat.auto.AtcMenuChatMessage;
import de.knewcleus.openradar.gui.chat.auto.ChatMessagesManager;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.view.IRadarViewChangeListener;
import de.knewcleus.openradar.view.ViewerAdapter;

/**
 * This class is a doing the jobs around the MP part of the GUI:
 *
 * It is the MODEL and CONTROLLER, providing the data for display and it manages
 * the filtering (Filters: Show all, Show messages of selected user, Show
 * messages of users in range and show messages of users which are currently
 * displayed.
 *
 * @author Wolfram Wagner
 *
 */

public class MpChatManager implements ListModel<GuiChatMessage>, ListSelectionListener, IChatListener, KeyListener, IRadarViewChangeListener {

    private GuiMasterController master = null;
    @SuppressWarnings("unchecked")
    private final static List<GuiChatMessage> emptyList = (List<GuiChatMessage>) Collections.EMPTY_LIST;

    public enum Filter {
        FILTER_NONE, FILTER_SELECTED_USER, FILTER_FREQUENCY, FILTER_RANGE, FILTER_VISIBLE
    };

    private volatile Filter filter = Filter.FILTER_NONE;
    /**
     * used to flag, that the list needs to be repainted
     * if true, the model is updating the active list, before it returns the size.
     */
    private volatile boolean dirty = true;
    /* The container of all chat messages */
    private volatile List<GuiChatMessage> globalList = new ArrayList<GuiChatMessage>();
    /** the container of all chat messgaes that are displayed right now */
    private volatile List<GuiChatMessage> activeMessageList = emptyList;

    private volatile Map<String, List<GuiChatMessage>> mapCallSignMsgList = new TreeMap<String, List<GuiChatMessage>>();

    private List<ListDataListener> dataListeners = new ArrayList<ListDataListener>();

    private static int MAX_MSG_COUNT = 1000;

    private MpChatPanel chatPanel = null;
    private JList<GuiChatMessage> chatHistory = null;

    private ChatFilterMouseListener filterMouseListener = new ChatFilterMouseListener();

    private boolean updaterisRunning = true;
    private GuiUpdater guiUpdater = new GuiUpdater();

    private volatile AtcMenuChatMessage autoAtcMessage = null;
    private volatile AtcAliasChatMessage aliasAtcMessage = null;
    
    private List<String> ownChatHistory = new ArrayList<String>();
    private volatile int currentHistoryIndex = -1;

    private final ChatMessagesManager messagesManager = new ChatMessagesManager();
    
    public MpChatManager(GuiMasterController master) {
        this.master = master;

        guiUpdater.setDaemon(true);
    }

    public void start() {
    	setFilter(Filter.FILTER_RANGE);
        guiUpdater.start();
    }

    public void setChatPanel(MpChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    public void setChatHistory(JList<GuiChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public void setSelectedCallSign(String callSign, boolean exclusive) {
//        String newPrefix = "";
        if(filter==Filter.FILTER_SELECTED_USER) setFilter(Filter.FILTER_NONE);
        if(callSign!= null) {
//            newPrefix = callSign + ":";
            setFilter(exclusive ? Filter.FILTER_SELECTED_USER : filter);
        }
        resetChatField();
/*        synchronized(chatPanel.getChatMessageLock()) {
            String currentText = (String) chatPanel.getChatMessage();
            if (currentText.isEmpty()) {
                currentText = newPrefix + " ";
            } else if (currentText.contains(":")) {
                currentText = currentText.replaceFirst(".*:", newPrefix);
            } else {
                currentText = newPrefix + " " + currentText.trim(); 
            }
            chatPanel.setChatMessage(currentText);
            validateTextLength(currentText);
        }
        requestGuiUpdate();
*/
    }

    public void setFilter(Filter filter) {
        synchronized(this) {
            if(this.filter!=filter) {
                // only if needed
                this.filter = filter;
                chatPanel.selectFilter(filter);
                requestGuiUpdate();
            }
        }
    }
    /**
     * Registers a new message to the global list
     *
     * @param msg
     */
    public synchronized void addMessage(GuiChatMessage msg) {

        List<GuiChatMessage> callSignList = getCallSignMsgList(msg.getCallSign());
        if (callSignList == null) {
            callSignList = new ArrayList<GuiChatMessage>();
            mapCallSignMsgList.put(msg.getCallSign(), callSignList);
        }
        // skip duplicates
        if (callSignList.size() > 0 && msg.getMessage().equals(callSignList.get(0).getMessage())
        /*
         * && msg . getCreated ( ) . getTime ( ) - callSignList . get ( 0 ) .
         * getCreated ( ) . getTime ( ) < 30000
         */) {
            return;
        }

        // add msg to lists
        globalList.add(0, msg);
        callSignList.add(0, msg);
        currentHistoryIndex = -1;
        
        // shrink lists to max size
        if (globalList.size() == MAX_MSG_COUNT) {
            GuiChatMessage lastMessage = globalList.remove(MAX_MSG_COUNT - 1);
            callSignList = getCallSignMsgList(lastMessage.getCallSign());
            callSignList.remove(callSignList.size() - 1);
            if (callSignList.isEmpty()) {
                mapCallSignMsgList.remove(lastMessage.getCallSign());
                if (activeMessageList == callSignList) {
                    // cleanup activeMesageList, if last message was from
                    // filtered user
                    activeMessageList = emptyList;
                }
            }
        }
        requestGuiUpdate();
        if( !msg.getCallSign().equals(master.getCurrentATCCallSign())
            && msg.isAirportMentioned()
            && !msg.isNeglectOrInactive()) {
            SoundManager.playChatSound();
        }
    }
    /**
     * DONT call directly! Use requestGuiUpdate()!!!
     *
     * This method is called by the model when "getSize" is called and dirty is true. It updates the activeList using the filter settings.
     */
    private void applyChanges() {
        if(!dirty) return;
        dirty=false; // must be here to avoid an endless loop

        List<GuiChatMessage> filteredMessageList = new ArrayList<GuiChatMessage>();
        GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact(); // to avoid thread locks

        synchronized (this) {
            List<GuiChatMessage> formerList = new ArrayList<GuiChatMessage>(activeMessageList);
            String ownCallSign = master.getCurrentATCCallSign();
            Set<String> allCallSigns = new HashSet<String>(mapCallSignMsgList.keySet());

            switch (filter) {
            case FILTER_RANGE:
            case FILTER_VISIBLE:
                // filter global list
                Set<String> callSignsInFilter = new HashSet<String>();

                for (String currentCallSign : mapCallSignMsgList.keySet()) {
                    if (filter == Filter.FILTER_RANGE) {
                        if (master.getRadarContactManager().isCallSignInRange(currentCallSign)) {
                            callSignsInFilter.add(currentCallSign);
                        }
                    } else if (filter == Filter.FILTER_VISIBLE) {
                        if (master.getRadarContactManager().isCallSignVisible(currentCallSign)) {
                            callSignsInFilter.add(currentCallSign);
                        }
                    }
                }
                for (GuiChatMessage m : globalList) {
                    if (callSignsInFilter.contains(m.getCallSign())
                            || ( messageContainsCallSigns(m.getMessage(),callSignsInFilter) && ownCallSign.equals(m.getCallSign()))
                            || ( !messageContainsCallSigns(m.getMessage(),allCallSigns) && ownCallSign.equals(m.getCallSign())) ) {
                        filteredMessageList.add(m);
                    }
                }
                activeMessageList = filteredMessageList;
                break;
            case FILTER_FREQUENCY:
                String atcFrequencyList = master.getRadioManager().getActiveFrequencyList();
                for (GuiChatMessage m : globalList) {
                    if (atcFrequencyList.contains(m.getFrequency())) {
                        filteredMessageList.add(m);
                    }
                }
                activeMessageList = filteredMessageList;
                break;
            case FILTER_SELECTED_USER:
                if(selectedContact!=null) {
                String selectedCallSign = selectedContact.getCallSign();
                    for (GuiChatMessage m : globalList) {
                        if (selectedCallSign.equals(m.getCallSign())
                            || (m.getMessage().contains(selectedCallSign) && ownCallSign.equals(m.getCallSign()))
                            || ( !messageContainsCallSigns(m.getMessage(),allCallSigns) && ownCallSign.equals(m.getCallSign())) ) {

                            filteredMessageList.add(m);
                        }
                    }
                }
                activeMessageList = filteredMessageList;
                break;
            case FILTER_NONE:
                activeMessageList = globalList;
            }

            if (formerList != activeMessageList) {
                notifyListenersListChange(formerList);
            }
        }
    }

    private boolean messageContainsCallSigns(String message, Set<String> callSignsInFilter) {
        for(String callSign : callSignsInFilter) {
            if(message.contains(callSign)) return true;
        }
        return false;
    }

    private synchronized List<GuiChatMessage> getCallSignMsgList(String callSign) {
        return mapCallSignMsgList.get(callSign);
    }

    private void notifyListenersListChange(List<GuiChatMessage> formerList) {
        for (ListDataListener dl : dataListeners) {
            dl.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, formerList.size()));
            if (!activeMessageList.isEmpty()) {
                dl.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, activeMessageList.size()));
            }
        }
    }

    // List model

    @Override
    public synchronized int getSize() {
        if(dirty) applyChanges(); // this updates the list, before it returns the size
        return activeMessageList.size();
    }

    @Override
    public synchronized GuiChatMessage getElementAt(int index) {
        return activeMessageList.get(index);
    }

    @Override
    public synchronized void addListDataListener(ListDataListener l) {
        dataListeners.add(l);
    }

    @Override
    public synchronized void removeListDataListener(ListDataListener l) {
        dataListeners.remove(l);
    }

    // List Selection listener

    @Override
    public synchronized void valueChanged(ListSelectionEvent e) {

    }

    // IChatListeners

    @Override
    public void newChatMessageReceived(String callSign, String frequency, String message) {
        GuiChatMessage msg = new GuiChatMessage(master, new Date(), callSign, frequency, message);
        msg.setKnownRadarContact(master.getRadarContactManager().getContactFor(callSign));
        addMessage(msg);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        synchronized(chatPanel.getChatMessageLock()) {
            String inputText = chatPanel.getChatMessage();
            validateTextLength(inputText);
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                if(inputText.length()<129) {
                    sendChatMessage();
                    chatPanel.resetChatMsgColor();
                }
                e.consume();
            } 
        }
    }

    private void displayPrevMsg() {
        if(currentHistoryIndex > 0) {
            currentHistoryIndex-=1;
        } else {
            return;
        }
        String newChatMessage = ownChatHistory.get(currentHistoryIndex);
        chatPanel.setChatMessage(newChatMessage);
        if(newChatMessage.contains(":")) {
            newChatMessage = newChatMessage.substring(newChatMessage.indexOf(":")+1).trim();
        }
        GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();
        if(selectedContact!=null) {
            chatPanel.setChatMessage(selectedContact.getCallSign()+": "+newChatMessage);
        } else {
            chatPanel.setChatMessage(newChatMessage);
        }
    }

    private void displayNextMsg() {
        if(currentHistoryIndex < ownChatHistory.size()-1) {
            currentHistoryIndex+=1;
        } else {
            return;
        }
        String newChatMessage = ownChatHistory.get(currentHistoryIndex);
        if(newChatMessage.contains(":")) {
            newChatMessage = newChatMessage.substring(newChatMessage.indexOf(":")+1).trim();
        }
        GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();
        if(selectedContact!=null) {
            chatPanel.setChatMessage(selectedContact.getCallSign()+": "+newChatMessage);
        } else {
            chatPanel.setChatMessage(newChatMessage);
        }
    }

    public void validateTextLength() {
        validateTextLength(chatPanel.getChatMessage());
    }
    
    public void validateTextLength(String msg) {
        if(msg.length()>128) {
            chatPanel.setChatMsgColor(Palette.CHAT_MESSAGE_TOO_LONG);
        } else if(msg.length()>120) {
            chatPanel.setChatMsgColor(Palette.CHAT_MESSAGE_LONG);
        } else {
            chatPanel.resetChatMsgColor();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            displayNextMsg();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            displayPrevMsg();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        }
    }

    public ChatFilterMouseListener getFilterMouseListener() {
        return filterMouseListener;
    }

    public class ChatFilterMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel lSource = (JLabel) e.getSource();
            MpChatPanel parent = (MpChatPanel) lSource.getParent();
            if (lSource.getName().equals("ALL")) {
                setFilter(Filter.FILTER_NONE);
            } else if (lSource.getName().equals("FRQ")) {
                setFilter(Filter.FILTER_FREQUENCY);
            } else if (lSource.getName().equals("RNG")) {
                setFilter(Filter.FILTER_RANGE);
            } else if (lSource.getName().equals("VIS")) {
                setFilter(Filter.FILTER_VISIBLE);
            } else if (lSource.getName().equals("SEL")) {
                setFilter(Filter.FILTER_SELECTED_USER);
            }
            parent.selectFilter(filter);
        }
    }

    public void requestFocusForInput() {
        chatPanel.requestFocusForInput();
    }

    /**
     * Needed to apply changes, if contacts come into visible range...
     */
    private class GuiUpdater extends Thread {

        public void run() {
            setName("OpenRadar - Chat Filter Updater");

            while (updaterisRunning == true) {
                if(filter==Filter.FILTER_VISIBLE) {
                    requestGuiUpdate();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public synchronized void requestGuiUpdate() {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// executed by the Swing Thread
		        dirty = true;
		        chatHistory.invalidate();
		        chatHistory.repaint();
			}
    	});
    }


    public void setAutoAtcMessage(GuiRadarContact contact, AtcMenuChatMessage msg) {
        autoAtcMessage = msg;
        List<String> messages = msg.generateMessages(master, contact, null);
        chatPanel.setChatMessage(messages.get(0)); // English text
        requestFocusForInput();
    }
    // TODO: merge the similar methods above and below
    public void setAtcMessage(GuiRadarContact contact, String msg, Object... args) {
        List<String> messages = messagesManager.getMessage(msg).generateMessagesWithArgs(master, contact, null, args);
        chatPanel.setChatMessage(messages.get(0)); // English text
        requestFocusForInput();
    }

    private void sendChatMessage() {
        String message = (String) chatPanel.getChatMessage();
        boolean chatAliasEnabled = master.getAirportData().isChatAliasesEnabled();
        boolean messageContainsAliases = AtcAliasChatMessage.containsUnresolvedAlias(master.getAirportData().getChatAliasPrefix(), message);

        if(chatAliasEnabled && aliasAtcMessage==null
                && messageContainsAliases) {
            // alias: first step: resolve alias and place result in chat input field
            resolveAliasMessage(message);

        } else if(chatAliasEnabled && aliasAtcMessage!=null) {
            // alias: second step, store arguments and send it.
            aliasAtcMessage.storeArguments(master);
            aliasAtcMessage = null;
            processOutGoingMessage(message, true);
        } else if(autoAtcMessage!=null) {
            // menu atc text: second step: Send it
            sendFollowUpAtcMessages(message);
        } else {
            // normal text
            processOutGoingMessage(message, true);
        }
    }

    public void resolveAliasMessage(String sMessage) {

        aliasAtcMessage = new AtcAliasChatMessage(master,sMessage);

        if(aliasAtcMessage.getResolvedMessage()!=null) {
            // message could be resolved
            chatPanel.setChatMessage(aliasAtcMessage.getResolvedMessage());
        } else {
            // original message
            chatPanel.setChatMessage(sMessage);
            aliasAtcMessage=null;
        }
    }

    private void sendFollowUpAtcMessages(String chatInputText) {
        if(autoAtcMessage!=null) {
            GuiRadarContact c = master.getRadarContactManager().getSelectedContact();
            if(c!=null) {
                List<String> messages = autoAtcMessage.generateMessages(master, c, c.getAtcLanguage());

                String englishText = messages.get(0);

                if(chatInputText.contains(englishText)) {
                    // retrieve appended text
                    String appendix = chatInputText.substring(chatInputText.indexOf(englishText)+englishText.length());
                    // send the message and the messages in other languages
                    processOutGoingMessage(englishText+appendix, true);
                    for(int i=1;i<messages.size();i++) {
                        processOutGoingMessage(messages.get(i)+appendix, true);
                    }

                } else {
                    // atcMessagesText is not contained!!!
                    processOutGoingMessage(chatInputText, true); // send it normally
                }
            } else {
                // to all airmen
                processOutGoingMessage(chatInputText, true); // send it normally
            }

        }
        autoAtcMessage = null;
    }

    private void processOutGoingMessage(String message, boolean resetChatField) {
        String ownFrequency = master.getRadioManager().getModels().size()>0 ? master.getRadioManager().getModels().get("COM0").getSelectedItem().getFrequency() : "";
        message = checkMessage(message);
        master.getRadarProvider().sendChatMessage(ownFrequency, message); // send to MP Server
        // add to own chat history
        ownChatHistory.add(0,message);
        if(ownChatHistory.size()>20) {
            ownChatHistory = ownChatHistory.subList(0, 19);
        }
        addMessage(new GuiChatMessage(master, new Date(), master.getCurrentATCCallSign(), ownFrequency , message));

        if(resetChatField) {
            GuiRadarContact c = master.getRadarContactManager().getSelectedContact();
            if (c != null) {
                setSelectedCallSign(c.getCallSign(), filter == Filter.FILTER_SELECTED_USER);
            }
            resetChatField();
        }
    }

    /**
     * Checks if the text is the same as before and adds a dot.
     * This is necessary because the messages are sent via UDP and the receiver needs to know, if he has got this message already.
     * 
     * @param the original message
     * @return the reworked message
     */
    private String checkMessage(String message) {
        if(ownChatHistory.isEmpty()) return message;
        String lastMsg = ownChatHistory.get(0);
        message = message.trim();
        if(message.equals(lastMsg)) {
            if(message.endsWith(".")) {
                // remove the dot
                message = message.substring(0, message.length()-1);
            } else {
                // add a dot
                message = message + ".";
            }
        }
        return message;
    }

    public void cancelAutoAtcMessage() {
        autoAtcMessage=null;
        aliasAtcMessage=null;
        resetChatField();
    }

    public void resetChatField() {
        currentHistoryIndex=-1;
        GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();
        if(selectedContact!=null) {
            chatPanel.setChatMessage(selectedContact.getCallSign()+": ");
        } else {
            chatPanel.setChatMessage("");
        }
    }

    public ComboBoxModel<String> getAutoAtcLanguages() {

        return null;
    }

    @Override
    public void radarViewChanged(ViewerAdapter v, Change c) {
        requestGuiUpdate();
    }

    public void sendMessages(List<String> textList) {
        for(String txt : textList) {
            processOutGoingMessage(txt, false);
        }
    }

    public void setChatMsgColor(Color color) {
        chatPanel.setChatMsgColor(color);
    }

}
