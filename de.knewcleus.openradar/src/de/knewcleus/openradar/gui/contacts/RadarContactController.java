package de.knewcleus.openradar.gui.contacts;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.knewcleus.fgfs.multiplayer.IPlayerListener;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.Alignment;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.Operation;
import de.knewcleus.openradar.gui.radar.GuiRadarBackend;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;

/**
 * This class manages the radar contacts to be displayed in the front end. 
 * Problem here is thread safety: The radar back end updates the contacts frequently and we need to react on users modifications.
 * 
 * @author Wolfram Wagner
 */
public class RadarContactController implements ListModel<GuiRadarContact>, ListSelectionListener, IPlayerListener<TargetStatus> {

    private GuiMasterController master = null;

    private GuiRadarBackend radarBackend = null;
    
    private GuiRadarContact.Operation filterOperation = null;
    
    private volatile GuiRadarContact selectedContact = null;
    
    private volatile List<GuiRadarContact> activeContactList = new ArrayList<GuiRadarContact>();
    private final List<GuiRadarContact> completeContactList = new ArrayList<GuiRadarContact>();

    private volatile Map<String,GuiRadarContact> mapCallSignCointact =  new TreeMap<String,GuiRadarContact>();

    private volatile List<GuiRadarContact> modelList = new ArrayList<GuiRadarContact>();
    
    private List<ListDataListener> dataListeners =  new ArrayList<ListDataListener>();
  
    private boolean updaterisRunning = true;
    private GuiUpdater guiUpdater = new GuiUpdater();
    private JList<GuiRadarContact> guiList = null;
    private ContactMouseListener contactMouseListener = new ContactMouseListener();
    private ContactFilterMouseListener contactFilterMouseListener = new ContactFilterMouseListener();
    private DetailsFocusListener detailsFocusListener = new DetailsFocusListener();
    
    public RadarContactController(GuiMasterController master, GuiRadarBackend radarBackend) {
        this.master = master;
        this.radarBackend = radarBackend;
        guiUpdater.start();
    }
    
    public GuiMasterController getMaster() {
        return master;
    }
    
    public synchronized void setFilterOperation(Operation filterOperation) {
        this.filterOperation = filterOperation;
        applyFilter();
    }

    private synchronized void applyFilter() {
        if(filterOperation==null) {
            activeContactList = completeContactList;
        } else {
            activeContactList = Collections.synchronizedList(new ArrayList<GuiRadarContact>());
        
            for (GuiRadarContact c : completeContactList) {
                if(filterOperation.equals(c.getOperation())) {
                    activeContactList.add(c);
                }
            }
        }
    }    
    
    public synchronized GuiRadarContact getContactFor(String callSign) {
        return mapCallSignCointact.get(callSign);
    }
    
    public synchronized boolean isCallSignInRange(String callSign) {
        return radarBackend.isContactInRange(getContactFor(callSign));
    }


    public synchronized boolean isCallSignVisible(String callSign) {
        return radarBackend.isContactVisible(getContactFor(callSign));
    }
    
   private class GuiUpdater extends Thread {
        
        public void run() {
            while(updaterisRunning == true) {
                publishData();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
    }
    
    private synchronized void publishData() {
        int formerSize=modelList.size();
        modelList = new ArrayList<GuiRadarContact>(activeContactList);
        notifyListenersListChange(formerSize);
    }
    
    private void notifyListenersListChange(int formerListSize) {
        for (ListDataListener dl : dataListeners) {
            dl.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_REMOVED, 0, formerListSize));
            if (!activeContactList.isEmpty()) {
                dl.contentsChanged(new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED, 0, modelList.size()));
            }
        }
    }

    
    // List model
    
    @Override
    public synchronized int getSize() {
        return modelList.size();
    }

    @Override
    public synchronized GuiRadarContact getElementAt(int index) {
        return modelList.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        dataListeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        dataListeners.remove(l);
    }

    public synchronized boolean isSelected(GuiRadarContact guiRadarContact) {
        return guiRadarContact == selectedContact;
    }

    public synchronized GuiRadarContact getSelectedContact() {
        return selectedContact;
    }

    public synchronized void select(GuiRadarContact guiRadarContact, boolean exlcusive) {
        if(selectedContact!=null) {
            selectedContact.setAtcComment(master.getDetails());
        }
        selectedContact = guiRadarContact;
        master.getMpChatManager().setSelectedCallSign(guiRadarContact.getCallSign(), exlcusive);
        master.getStatusManager().setSelectedCallSign(guiRadarContact.getCallSign());
        master.setDetails(guiRadarContact.getAtcComment());
    }

    // ListSelectionListener
    
    @Override
    public synchronized void valueChanged(ListSelectionEvent e) {
        int selectedIndex = e.getFirstIndex(); 
        select(activeContactList.get(selectedIndex),false);
    }

    // Player registry listener
    
    @Override
    public synchronized void playerAdded(TargetStatus player) {
        GuiRadarContact c = new GuiRadarContact(this,player);
        c.setOperation(GuiRadarContact.Operation.UNKNOWN);
        completeContactList.add(c);
        mapCallSignCointact.put(c.getCallSign(), c);
        applyFilter();
    }


    @Override
    public synchronized void playerRemoved(TargetStatus player) {
        String callSign = player.getCallsign();
        if(mapCallSignCointact.containsKey(player.getCallsign())) {
            GuiRadarContact c = mapCallSignCointact.remove(callSign);
            completeContactList.remove(c);
            activeContactList.remove(c);
            mapCallSignCointact.remove(c.getCallSign());
        }
    }


    @Override
    public synchronized void playerListEmptied(TargetStatus player) {
        mapCallSignCointact.clear();
        activeContactList.clear();
        completeContactList.clear();
    }


    public synchronized void setJList(JList<GuiRadarContact> liRadarContacts) {
        this.guiList = liRadarContacts;
    }


    public synchronized void dragAndDrop(int selectedIndex, int insertAtIndex, Alignment alignment) {
        
        GuiRadarContact c = activeContactList.get(selectedIndex);
        c.setAlignment(alignment);

        if(selectedIndex==insertAtIndex) return;
        
        activeContactList.remove(selectedIndex);
        
        if(insertAtIndex<activeContactList.size()) {
            activeContactList.add(insertAtIndex,c);
        } else {
            activeContactList.add(c);
        }
        publishData();
    }

    // mouse listener
    
    public ContactMouseListener getContactMouseListener() {
        return contactMouseListener;
    }
    
    public class ContactMouseListener extends MouseAdapter {
    
        @Override
        public void mouseClicked(MouseEvent e) {
            synchronized(this) {
                GuiRadarContact c = activeContactList.get(guiList.locationToIndex(e.getPoint()));
                if(c!=null) {
                    select(c,e.getClickCount()==2);
                }
            }
        }
    
    }
    
    public ContactFilterMouseListener getContactFilterMouseListener() {
        return contactFilterMouseListener;
    }
    
    public class ContactFilterMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel lSource = (JLabel)e.getSource();
            if(lSource.getName().equals("ALL")) {
                setFilterOperation(null);
            } else if(lSource.getName().equals("GROUND")) {
                setFilterOperation(Operation.GROUND);
            } else if(lSource.getName().equals("LANDING")) {
                setFilterOperation(Operation.LANDING);
            } else if(lSource.getName().equals("STARTING")) {
                setFilterOperation(Operation.STARTING);
            } else if(lSource.getName().equals("TRAVEL")) {
                setFilterOperation(Operation.TRAVEL);
            } else if(lSource.getName().equals("EMERGENCY")) {
                setFilterOperation(Operation.EMERGENCY);
            } else if(lSource.getName().equals("UNKNOWN")) {
                setFilterOperation(Operation.UNKNOWN);
            }
            ContactsPanel parent = (ContactsPanel)lSource.getParent();
            parent.resetFilters();
            parent.selectFilter(lSource);
        }
    }

    public DetailsFocusListener getDetailsFocusListener() {
        return detailsFocusListener;
    }
    
    public class DetailsFocusListener extends FocusAdapter {
        @Override
        public void focusLost(FocusEvent e) {
            
            if(selectedContact!=null) {
                selectedContact.setAtcComment(master.getDetails());
            }
        }
    }
    
}
