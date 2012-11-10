package de.knewcleus.openradar.gui.chat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.knewcleus.fgfs.multiplayer.IChatListener;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.radar.IRadarChangeListener;
import de.knewcleus.openradar.gui.radar.GuiRadarBackend.ZoomLevel;
import de.knewcleus.openradar.radardata.fgmp.FGMPClient;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;

/**
 * This class is a doing the jobs around the MP part of the GUI:
 * 
 * It is the MODEL and CONTROLLER, providing the data for display and it manages the filtering
 * (Filters: Show all, Show messages of selected user, Show messages of users in
 * range and show messages of users which are currently displayed.
 * 
 * @author Wolfram Wagner
 * 
 */

public class MpChatManager implements ListModel<GuiChatMessage>, ListSelectionListener, IRadarChangeListener, IChatListener, ActionListener, KeyListener {

    private GuiMasterController master = null;
    private FGMPClient<TargetStatus> mpBackend = null;
    @SuppressWarnings("unchecked")
    private final static List<GuiChatMessage> emptyList = (List<GuiChatMessage>) Collections.EMPTY_LIST;

    public enum Filter {
        FILTER_NONE, FILTER_SELECTED_USER, FILTER_FREQUENCY, FILTER_RANGE, FILTER_VISIBLE
    };

    private Filter filter = Filter.FILTER_NONE;

    private volatile List<GuiChatMessage> activeMessageList = emptyList;
    private volatile List<GuiChatMessage> filteredMessageList = emptyList;

    private volatile List<GuiChatMessage> globalList = new ArrayList<GuiChatMessage>();
    private volatile Map<String, List<GuiChatMessage>> mapCallSignMsgList = new TreeMap<String, List<GuiChatMessage>>();

    private List<ListDataListener> dataListeners = new ArrayList<ListDataListener>();

    private static int MAX_MSG_COUNT = 1000;

    private JComboBox<String> chatMessageBox = null;
    private JList<GuiChatMessage> chatHistory = null;
    
    private ChatFilterMouseListener filterMouseListener = new ChatFilterMouseListener(); 
    
    public MpChatManager(GuiMasterController master) {
        this.master = master;
        
        master.getRadarManager().addChangeListener(this);
    }

    public FGMPClient<TargetStatus> getMpBackend() {
        return mpBackend;
    }

    public void setMpBackend(FGMPClient<TargetStatus> mpBackend) {
        this.mpBackend = mpBackend;
    }

    public synchronized void setFilter(Filter filter) {
        this.filter = filter;
        applyFilter();
    }

    public void setChatMessageBox(JComboBox<String> chatMessageBox) {
        this.chatMessageBox = chatMessageBox;
    }
    public void setChatHistory(JList<GuiChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public void setSelectedCallSign(String callSign, boolean exclusive) {
        String newPrefix = callSign+":";
        
        setFilter(exclusive?Filter.FILTER_SELECTED_USER:Filter.FILTER_NONE);

        String currentText = (String)chatMessageBox.getEditor().getItem();
        if(currentText.isEmpty()) {
            currentText = newPrefix+" ";
        } else if(currentText.contains(":")) {
            currentText = currentText.replaceFirst(".*:", newPrefix);
        } else {
            currentText = newPrefix+" "+currentText.trim();
        }
        chatMessageBox.setSelectedItem(currentText);
        
        chatHistory.invalidate();
    }
    
    public synchronized  void addMessage(GuiChatMessage msg) {
        
        List<GuiChatMessage> callSignList = getCallSignMsgList(msg.getCallSign());
        if (callSignList == null) {
            callSignList = new ArrayList<GuiChatMessage>();
            mapCallSignMsgList.put(msg.getCallSign(), callSignList);
        }
        // skip duplicates
        if(callSignList.size()>0 && 
                msg.getMessage().equals(callSignList.get(0).getMessage()) /*&&
                msg.getCreated().getTime()-callSignList.get(0).getCreated().getTime()<30000*/) return;
        
     // add msg to lists
        globalList.add(0, msg);
        callSignList.add(0,msg);

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
                    activeMessageList=emptyList;
                }
            }
        }

        applyFilter();
    }

    private synchronized void applyFilter() {
        List<GuiChatMessage> formerList = new ArrayList<GuiChatMessage>(activeMessageList);
        filteredMessageList.clear();
        
        switch (filter) {
        case FILTER_RANGE:
        case FILTER_VISIBLE:
            // filter global list
            Set<String> callSignsInFiler = new HashSet<String>();

            for (String currentCallSign : mapCallSignMsgList.keySet()) {
                if (filter == Filter.FILTER_RANGE) {
                    if (master.getRadarContactManager().isCallSignInRange(currentCallSign)) {
                        callSignsInFiler.add(currentCallSign);
                    }
                } else if (filter == Filter.FILTER_VISIBLE) {
                    if (master.getRadarContactManager().isCallSignVisible(
                            currentCallSign)) {
                        callSignsInFiler.add(currentCallSign);
                    }
                } 
            }
            for (GuiChatMessage m : globalList) {
                if (callSignsInFiler.contains(m.getCallSign())) {
                    filteredMessageList.add(m);
                }
            }
            activeMessageList=filteredMessageList;
            break;
        case FILTER_FREQUENCY:
            String atcFrequencyList = master.getRadioManager().getActiveFrequencyList();
            for (GuiChatMessage m : globalList) {
                if (atcFrequencyList.contains(m.getFrequency())) {
                    filteredMessageList.add(m);
                }
            }
            activeMessageList=filteredMessageList;
            break;
        case FILTER_SELECTED_USER:
            String selectedCallSign = master.getRadarContactManager().getSelectedContact().getCallSign();
            if(selectedCallSign==null) {
                activeMessageList = emptyList;
            } else {
                List<GuiChatMessage> callSignList = getCallSignMsgList(selectedCallSign);
                if (callSignList == null || callSignList.isEmpty()) {
                    activeMessageList = emptyList;
                } else {
                    activeMessageList = callSignList;
                }
            }
            break;
        case FILTER_NONE:
            activeMessageList = globalList;
        }
        
        if(formerList!=activeMessageList) {
            notifyListenersListChange(formerList);
        }
    }

    private List<GuiChatMessage> getCallSignMsgList(String callSign) {
        return mapCallSignMsgList.get(callSign);
    }

    private void notifyListenersListChange(List<GuiChatMessage> formerList) {
        for (ListDataListener dl : dataListeners) {
            dl.intervalRemoved(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_REMOVED, 0, formerList.size()));
            if (!activeMessageList.isEmpty()) {
                dl.intervalAdded(new ListDataEvent(this,
                        ListDataEvent.INTERVAL_ADDED, 0, activeMessageList.size()));
            }
        }
    }

    public synchronized boolean sendMessage(String message) {
        //String ownCallSign = master.getCurrentATCCallSign();
        //String targetCallSign = master.getRadarContactManager().getSelectedContact().getCallSign();
        mpBackend.sendChatMessage(message);
        return true; // extend?
    }

    // List model

    @Override
    public synchronized int getSize() {
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

    // IRadarZoomListener
    
    @Override
    public synchronized void radarZoomLevelChanged(ZoomLevel formerLevel,ZoomLevel newLevel) {
        applyFilter();
    }

    // IChatListeners
    
    @Override
    public synchronized void newChatMessageReceived(String callSign, String frequency, String message) {
        GuiChatMessage msg = new GuiChatMessage(master, new Date(), callSign, frequency, message);
        msg.setKnownRadarContact(master.getRadarContactManager().getContactFor(callSign));
        addMessage(msg);
    }

   @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JComboBox<?>) {
            if(e.getActionCommand()=="comboBoxEdited" && e.getID()==ActionEvent.ACTION_PERFORMED) {
                @SuppressWarnings("unchecked")
                JComboBox<String> chatInput = (JComboBox<String>)e.getSource();
                String message = (String)chatInput.getSelectedItem();
                sendMessage(message);
                addMessage(new GuiChatMessage(master, new Date(), master.getCurrentATCCallSign(), "todo", message));
                chatInput.setSelectedItem("");
                GuiRadarContact c = master.getRadarContactManager().getSelectedContact();
                if(c!=null) setSelectedCallSign(c.getCallSign(), filter==Filter.FILTER_SELECTED_USER);
            } 
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {  }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_ENTER) {
            @SuppressWarnings("unchecked")
            JComboBox<String> chatInput = (JComboBox<String>)e.getSource();
            String message = (String)chatInput.getSelectedItem();
            sendMessage(message);
            addMessage(new GuiChatMessage(master, new Date(), master.getCurrentATCCallSign(), "todo", message));
            chatInput.setSelectedItem("");
            GuiRadarContact c = master.getRadarContactManager().getSelectedContact();
            if(c!=null) setSelectedCallSign(c.getCallSign(), filter==Filter.FILTER_SELECTED_USER);
        }        
    }

    public ChatFilterMouseListener getFilterMouseListener() {
        return filterMouseListener;
    }
    
    public class ChatFilterMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel lSource = (JLabel)e.getSource();
            MpChatPanel parent = (MpChatPanel)lSource.getParent();
            if(lSource.getName().equals("ALL")) {
                setFilter(Filter.FILTER_NONE);
            } else if(lSource.getName().equals("FRQ")) {
                setFilter(Filter.FILTER_FREQUENCY);
            } else if(lSource.getName().equals("RNG")) {
                setFilter(Filter.FILTER_RANGE);
            } else if(lSource.getName().equals("RNG")) {
                setFilter(Filter.FILTER_VISIBLE);
            }
            parent.resetFilters();
            parent.selectFilter(lSource);
        }
    }
}
