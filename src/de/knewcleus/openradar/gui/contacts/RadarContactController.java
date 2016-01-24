/**
 * Copyright (C) 2012-2016 Wolfram Wagner
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
package de.knewcleus.openradar.gui.contacts;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.GeoUtil;
import de.knewcleus.fgfs.multiplayer.IPlayerListener;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.SoundManager;
import de.knewcleus.openradar.gui.chat.GuiChatMessage;
import de.knewcleus.openradar.gui.chat.auto.AtcMenuChatMessage;
import de.knewcleus.openradar.gui.chat.auto.AtcMessageDialog;
import de.knewcleus.openradar.gui.chat.auto.TextManager;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.Alignment;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.Operation;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.State;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightplan.FpAtc;
import de.knewcleus.openradar.gui.radar.GuiRadarBackend;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.SectorCreator;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;

/**
 * This class manages the radar contacts to be displayed in the front end.
 * Problem here is thread safety: The radar back end updates the contacts
 * frequently and we need to react on users modifications.
 *
 * @author Wolfram Wagner
 */
public class RadarContactController
		implements ListModel<GuiRadarContact>, ListSelectionListener, IPlayerListener<TargetStatus> {

	private GuiMasterController master = null;

	private GuiRadarBackend radarBackend = null;
	private TextManager textManager = new TextManager();
	private AtcMessageDialog atcMessageDialog;
	private FlightPlanDialog flightplanDialog;
	private TransponderSettingsDialog transponderSettingsDialog;

	// private volatile GuiRadarContact.Operation filterOperation = null;

	private volatile GuiRadarContact selectedContact = null;
	private final Object selectedContactLock = new Object();

	private volatile List<FpAtc> activeAtcs = new ArrayList<FpAtc>();
	private final Map<String, FpAtc> mapActiveAtcs = new TreeMap<String, FpAtc>();
	private final Object activeAtcsLock = new Object();

	private volatile List<GuiRadarContact> activeContactList = new ArrayList<GuiRadarContact>();
	private final List<GuiRadarContact> completeContactList = Collections
			.synchronizedList(new ArrayList<GuiRadarContact>());

	private final Map<String, GuiRadarContact> mapCallSignContact = Collections
			.synchronizedMap(new TreeMap<String, GuiRadarContact>());
	private final Map<String, GuiRadarContact> mapExpiredCallSigns = Collections
			.synchronizedMap(new TreeMap<String, GuiRadarContact>());

	private volatile List<GuiRadarContact> modelList = new ArrayList<GuiRadarContact>();

	private List<ListDataListener> dataListeners = new ArrayList<ListDataListener>();

	private boolean updaterisRunning = true;
	private GuiUpdater guiUpdater = new GuiUpdater();
	private JList<GuiRadarContact> guiList = null;
	private ContactMouseListener contactMouseListener = new ContactMouseListener();
	private ContactFilterMouseListener contactFilterMouseListener = new ContactFilterMouseListener();
	private DetailsFocusListener detailsFocusListener = new DetailsFocusListener();

	private enum OrderMode {
		AUTO, MANUAL
	};

	private enum ClickLocation {
		LEFT, ON_STRIP, RIGHT
	};

	private OrderMode orderMode = OrderMode.AUTO;
	private ArrayList<GuiRadarContact> orderList = new ArrayList<GuiRadarContact>(20);

	//    private Map<String, String> mapAtcComments = new TreeMap<String, String>();
	private AtcNotesStore atcNotesStore = new AtcNotesStore();

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	private final static Logger log = LogManager.getLogger(RadarContactController.class);

	public RadarContactController(GuiMasterController master, GuiRadarBackend radarBackend) {
		this.master = master;
		this.radarBackend = radarBackend;
		guiUpdater.setDaemon(true);
		atcNotesStore.loadStore();
		atcMessageDialog = new AtcMessageDialog(master, textManager);
		flightplanDialog = new FlightPlanDialog(master, this);
		transponderSettingsDialog = new TransponderSettingsDialog(master, this);

		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public void start() {
		guiUpdater.start();
	}

	public GuiMasterController getMaster() {
		return master;
	}

	public void reloadTexts() {
		textManager.reloadTexts();
	}

	public void setFilterOperation(Operation filterOperation) {
		// this.filterOperation = filterOperation;
		applyFilter();
	}

	private synchronized void applyFilter() {
		if (true) { // filterOperation == null) {
			activeContactList = completeContactList;
		}
		// else {
		// activeContactList = Collections.synchronizedList(new
		// ArrayList<GuiRadarContact>());
		//
		// for (GuiRadarContact c : completeContactList) {
		// if (filterOperation.equals(c.getOperation())) {
		// activeContactList.add(c);
		// }
		// }
		// }
	}

	public synchronized GuiRadarContact getContactFor(String callSign) {
		GuiRadarContact rc = mapCallSignContact.get(callSign);
		if (rc == null)
			rc = mapExpiredCallSigns.get(callSign);
		if (rc == null) {
			// System.out.println("Callsign " + callSign + " not found!");
		}
		return rc;
	}

	public boolean isCallSignInRange(String callSign) {
		return callSign.equals(master.getCurrentATCCallSign())
				|| radarBackend.isContactInRange(getContactFor(callSign));
	}

	public boolean isCallSignVisible(String callSign) {
		return getContactFor(callSign) != null && getContactFor(callSign).isActive()
				&& radarBackend.isContactVisible(getContactFor(callSign));
	}

	private class GuiUpdater extends Thread {

		public void run() {
			setName("OpenRadar - Gui Updater");

			while (updaterisRunning == true) {
				publishData();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void publishData() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// executed by the Swing Thread
				boolean displayChatAsEnabled = true;
				boolean displayChatAsDisabled = false;
				GuiRadarContact c = getSelectedContact();
				if (null != c) {
					displayChatAsEnabled = c.isActive(); // to avoid lock because of incapsulated synchronizes
					displayChatAsDisabled = !displayChatAsEnabled;
				}
				synchronized (this) {
					checkExpired();
					if (orderMode == OrderMode.AUTO)
						orderContacts();
					int formerSize = modelList.size();
		
					modelList = new ArrayList<GuiRadarContact>(activeContactList);
					notifyListenersListChange(formerSize);
				}
				if (displayChatAsEnabled) {
					master.getMpChatManager().validateTextLength();
				}
				if (displayChatAsDisabled) {
					//master.getMpChatManager().setSelectedCallSign(null, false); // lock
					master.getMpChatManager().setChatMsgColor(Palette.CHAT_GHOST);
				}
			}
		});
	}

	private void checkExpired() {
		for (GuiRadarContact ec : new ArrayList<GuiRadarContact>(mapExpiredCallSigns.values())) {
			if (ec.isActive()) {
				// revive them
				mapExpiredCallSigns.remove(ec.getCallSign());

				addPlayerBeforeUncontrolled(ec);
				ec.reAppeared();
				activeContactList = completeContactList;
				mapCallSignContact.put(ec.getCallSign(), ec);
			}
		}
		for (GuiRadarContact c : new ArrayList<GuiRadarContact>(completeContactList)) {
			if (c.isExpired()) {
				// hide them
				completeContactList.remove(c);
				mapCallSignContact.remove(c.getCallSign());

				mapExpiredCallSigns.put(c.getCallSign(), c);

				activeContactList = completeContactList;
			}
		}
	}

	private void notifyListenersListChange(int formerListSize) {

		for (ListDataListener dl : dataListeners) {
			dl.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, formerListSize));
			if (!activeContactList.isEmpty()) {
				dl.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, modelList.size()));
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

	public boolean isSelected(GuiRadarContact guiRadarContact) {
		synchronized (selectedContactLock) {
			return guiRadarContact == selectedContact;
		}
	}

	public GuiRadarContact getSelectedContact() {
		synchronized (selectedContactLock) {
			return selectedContact;
		}
	}

	public void deselectContact() {
		synchronized (selectedContactLock) {
			// deselect
			selectedContact = null;
		}
		master.getMpChatManager().setSelectedCallSign(null, false);
		master.getStatusManager().setSelectedCallSign(null);
		master.setDetails(null);
		master.getRadarBackend().forceRepaint();
	}

	public void neglectSelectedContact() {
		synchronized (selectedContactLock) {
			// deselect
			if (selectedContact != null) {
				neglectContact(selectedContact, !selectedContact.isNeglect());
			}
		}
	}

	public void neglectContact(GuiRadarContact c, boolean b) {
		c.setNeglect(b);
		setContactsAlignment(c, Alignment.RIGHT);
	}

	public void select(GuiRadarContact guiRadarContact, boolean force, boolean exlcusive) {
		synchronized (selectedContactLock) {
			if (selectedContact != null) {
				selectedContact.setAtcComment(master.getDetails());
			}
			if (selectedContact != null && selectedContact.equals(guiRadarContact) && !force) {
				// deselect
				selectedContact = null;
				master.getMpChatManager().setSelectedCallSign(null, false);
			} else {
				selectedContact = guiRadarContact;
				selectedContact.disableNewContact();
				master.getMpChatManager().setSelectedCallSign(guiRadarContact.getCallSign(), exlcusive);
				master.getStatusManager().setSelectedCallSign(guiRadarContact.getCallSign());
				master.setDetails(guiRadarContact.getAtcComment());
			}
		}
		master.getMpChatManager().requestGuiUpdate();
		master.getRadarBackend().forceRepaint();
	}

	// ListSelectionListener

	@Override
	public synchronized void valueChanged(ListSelectionEvent e) {
		int selectedIndex = e.getFirstIndex();
		select(activeContactList.get(selectedIndex), true, false);
	}

	// Player registry listener

	@Override
	public synchronized void playerAdded(TargetStatus player) {
		GuiRadarContact c = null;
		if (!mapCallSignContact.containsKey(player.getCallsign())
				&& !mapExpiredCallSigns.containsKey(player.getCallsign())) {
			// add new player
			// old code by 20151223        	
			//            String atcNote = mapAtcComments.containsKey(player.getCallsign()) ? mapAtcComments.get(player.getCallsign()) : mapAtcComments.get(player.getCallsign()+".atcNote");
			//            // ^^^^ for backward compatibility: until 20130406 the atc note was saved directly under the callsign, now it is saved under callsign+".atcNote"
			//            c = new GuiRadarContact(master, this, player, atcNote);
			//            c.setFgComSupport("true".equals( mapAtcComments.get(player.getCallsign()+".fgComSupport")));
			c = new GuiRadarContact(master, this, player);
			atcNotesStore.restoreStaticData(c);

			addPlayerBeforeUncontrolled(c);
			mapCallSignContact.put(c.getCallSign(), c);

		} else if (mapExpiredCallSigns.containsKey(player.getCallsign())) {
			// re-activate expired player
			c = mapExpiredCallSigns.remove(player.getCallsign());
			c.setTargetStatus(player); // link to new player
			addPlayerBeforeUncontrolled(c);
			mapCallSignContact.put(c.getCallSign(), c);
		} else {
			// this should never happen
			master.getMpChatManager().addMessage(new GuiChatMessage(master, new Date(), "(System)", "",
					">> Duplicate contact found, this should not happen..."));
		}

		SoundManager.playContactSound();

		master.getAirportData().getSquawkCodeManager().updateUsedSquawkCodes(completeContactList);
		applyFilter();
	}

	private void addPlayerBeforeUncontrolled(GuiRadarContact c) {
		if (orderMode == OrderMode.AUTO) {
			// add it in front of the uncontrolled
			for (int i = 0; i < completeContactList.size(); i++) {
				if (completeContactList.get(i).getState() == State.UNCONTROLLED) {
					completeContactList.add(i, c);
					c = null;
					break;
				}
			}
		}
		if (c != null) {
			// add it at the end
			completeContactList.add(c);
		}

	}

	@Override
	public synchronized void playerRemoved(TargetStatus player) {
		String callSign = player.getCallsign();
		GuiRadarContact c = mapCallSignContact.remove(callSign);
		if (c == null) {
			c = mapExpiredCallSigns.remove(callSign);
		}
		if (c != null) {
			completeContactList.remove(c);
			activeContactList.remove(c);
			mapCallSignContact.remove(c.getCallSign());
			mapExpiredCallSigns.remove(c.getCallSign());
		}
		master.getAirportData().getSquawkCodeManager().updateUsedSquawkCodes(completeContactList);
	}

	@Override
	public synchronized void playerListEmptied(TargetStatus player) {
		mapCallSignContact.clear();
		activeContactList.clear();
		completeContactList.clear();
	}

	public synchronized JList<GuiRadarContact> getGuiList() {
		return guiList;
	}

	public synchronized void setJList(JList<GuiRadarContact> liRadarContacts) {
		this.guiList = liRadarContacts;
	}

	public synchronized void dragAndDrop(int selectedIndex, int insertAtIndex, Alignment alignment) {
		try {
			if (selectedIndex > activeContactList.size() - 1 || selectedIndex < 0) {
				return;
			}
			GuiRadarContact c = activeContactList.get(selectedIndex);
			setContactsAlignment(c, alignment);

			if (selectedIndex == insertAtIndex)
				return;

			activeContactList.remove(selectedIndex);

			if (insertAtIndex < activeContactList.size()) {
				activeContactList.add(insertAtIndex, c);
			} else {
				activeContactList.add(c);
			}
			publishData();
		} catch (Exception e) {
			log.error("Handled problem while drag and drop! " + e.getMessage());
		}
	}

	// mouse listener

	public ContactMouseListener getContactMouseListener() {
		return contactMouseListener;
	}

	private class ContactMouseListener extends MouseAdapter {

		private ClickLocation clickLocation = ClickLocation.ON_STRIP;
		private volatile GuiRadarContact activeDoubleClickContact = null;
		private javax.swing.Timer doubleClickTimer = null;

		@Override
		public void mouseClicked(MouseEvent e) {
			synchronized (RadarContactController.this) {
				int index = guiList.locationToIndex(e.getPoint());

				if (index > -1) {
					GuiRadarContact c = activeContactList.get(index);
					analyseClickPoint(c, guiList, e);

					if (clickLocation != ClickLocation.ON_STRIP && e.getButton() == MouseEvent.BUTTON1) {
						// side click with mouse button 1
						handleSideClick(c, c.getAlignment(), e.getClickCount());
					} else {
						// click on fs row
						if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
							// select
							select(c, true, false);
							if (c.isSelected()) {
								master.getMpChatManager().requestFocusForInput();
							}
						} else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
							// select and move map to it
							select(c, true, false);
							c.setHighlighted();
							master.getRadarBackend().showRadarContact(c, !e.isShiftDown());
							atcMessageDialog.setVisible(false);
							master.getMpChatManager().requestFocusForInput();

						} else if ((e.getButton() == MouseEvent.BUTTON2 && e.getClickCount() == 1)
								|| (e.isAltDown() && e.getButton() == MouseEvent.BUTTON3)) {
							// show contact settings dialog
							selectNShowFlightplanDialog(c, e);

						} else if (e
								.getButton() == MouseEvent.BUTTON3 /*
																	 * && e.
																	 * getClickCount
																	 * () == 1
																	 */) {
							// show ATC message dialog
							selectNShowAtcMsgDialog(c, e);

						}
						publishData();
					}
				}
			}
		}

		private void handleSideClick(GuiRadarContact c, Alignment alignment, int clickCount) {
			if (clickCount == 1) {
				activeDoubleClickContact = c;
				Integer timerinterval = (Integer) Toolkit.getDefaultToolkit()
						.getDesktopProperty("awt.multiClickInterval");
				doubleClickTimer = new Timer(timerinterval.intValue(), new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent evt) {
						if (activeDoubleClickContact != null) {
							executeClick(activeDoubleClickContact, activeDoubleClickContact.getAlignment(), 1);
						}

					}
				});
				doubleClickTimer.setRepeats(false);
				doubleClickTimer.start();
			} else {
				activeDoubleClickContact = null;
				executeClick(c, alignment, clickCount);
			}

		}

		private void executeClick(GuiRadarContact c, Alignment alignment, int clickCount) {

			switch (clickLocation) {
			case LEFT:
				if (alignment == Alignment.CENTER || (alignment == Alignment.RIGHT && clickCount == 2)) {
					setContactsAlignment(c, Alignment.LEFT);
					select(c, true, false);
					c.setHighlighted();
					master.getMpChatManager().requestFocusForInput();
				} else if (alignment == Alignment.RIGHT && clickCount == 1) {
					setContactsAlignment(c, Alignment.CENTER);
				}
				break;
			case RIGHT:
				if (alignment == Alignment.CENTER || alignment == Alignment.LEFT && clickCount == 2) {
					setContactsAlignment(c, Alignment.RIGHT);
				} else if (alignment == Alignment.LEFT && clickCount == 1) {
					setContactsAlignment(c, Alignment.CENTER);
				}
				break;
			default:
				break;
			}
			publishData();
		}

		private void analyseClickPoint(GuiRadarContact c, JList<?> list, MouseEvent e) {
			double totalSize = list.getVisibleRect().getWidth();
			double stripWidth = FlightStripCellRenderer.STRIP_WITDH;

			double sideGap = c.getAlignment() == Alignment.CENTER ? (totalSize - stripWidth) / 2
					: (totalSize - stripWidth);
			double clickX = e.getPoint().getX();

			switch (c.getAlignment()) {
			case LEFT:
				if (clickX > stripWidth) {
					clickLocation = ClickLocation.RIGHT;
				} else {
					clickLocation = ClickLocation.ON_STRIP;
				}
				break;
			case CENTER:
				if (clickX < sideGap) {
					clickLocation = ClickLocation.LEFT;
				} else if (clickX > sideGap + stripWidth) {
					clickLocation = ClickLocation.RIGHT;
				} else {
					clickLocation = ClickLocation.ON_STRIP;
				}
				break;
			case RIGHT:
				if (clickX < sideGap) {
					clickLocation = ClickLocation.LEFT;
				} else {
					clickLocation = ClickLocation.ON_STRIP;
				}
				break;
			}
		}

		@Override
		public synchronized void mouseDragged(MouseEvent e) {
			Rectangle listBounds = guiList.getBounds();
			Rectangle rect = guiList.getVisibleRect();
			Point p = e.getPoint();
			if (p.getY() < rect.getY()) {
				if (rect.getY() > 10) {
					rect.setRect(rect.getX(), rect.getY() - 10, rect.getWidth(), rect.getHeight());
					guiList.scrollRectToVisible(rect);
				}
			}
			if (p.getY() > listBounds.getHeight()) {
				Dimension rectReal = guiList.getPreferredSize();
				if (rect.getY() + rect.getHeight() > rectReal.getHeight() - 10) {
					rect.setRect(rect.getX(), rect.getY() + 10, rect.getWidth(), rect.getHeight());
					guiList.scrollRectToVisible(rect);
				}
			}
		}
	}

	public void displayChatMenu() {
		synchronized (selectedContactLock) {
			if (selectedContact != null) {
				atcMessageDialog.setLocation(selectedContact, null);
			}
		}
	}

	public synchronized void selectNShowAtcMsgDialog(GuiRadarContact c, MouseEvent e) {
		select(c, true, false); // normal select
		master.getMpChatManager().requestFocusForInput();
		if (c.isActive())
			atcMessageDialog.setLocation(c, e);
	}

	public synchronized void selectNShowFlightplanDialog(GuiRadarContact c, MouseEvent e) {
		// show details dialog
		select(c, true, false);
		//        flightplanDialog = new FlightPlanDialog(master, this); // TODO remove
		flightplanDialog.show(c, e);
	}

	public void displayContactDialogForSelectedUser() {
		synchronized (selectedContactLock) {
			if (selectedContact != null) {
				flightplanDialog.show(selectedContact, null);
			}
		}
	}

	public ContactFilterMouseListener getContactFilterMouseListener() {
		return contactFilterMouseListener;
	}

	public class ContactFilterMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel lSource = (JLabel) e.getSource();
			if (lSource.getName().equals("MODE")) {
				if (lSource.getText().equals("AUTO")) {
					orderMode = OrderMode.MANUAL;
					lSource.setText("MANUAL");
				} else {
					orderMode = OrderMode.AUTO;
					lSource.setText("AUTO");
					orderContacts();
				}
			}
			// if (lSource.getName().equals("ALL")) {
			// setFilterOperation(null);
			// } else if (lSource.getName().equals("GROUND")) {
			// setFilterOperation(Operation.GROUND);
			// } else if (lSource.getName().equals("LANDING")) {
			// setFilterOperation(Operation.LANDING);
			// } else if (lSource.getName().equals("STARTING")) {
			// setFilterOperation(Operation.STARTING);
			// } else if (lSource.getName().equals("TRAVEL")) {
			// setFilterOperation(Operation.TRAVEL);
			// } else if (lSource.getName().equals("EMERGENCY")) {
			// setFilterOperation(Operation.EMERGENCY);
			// } else if (lSource.getName().equals("UNKNOWN")) {
			// setFilterOperation(Operation.UNKNOWN);
			// }
			ContactsPanel parent = (ContactsPanel) lSource.getParent();
			parent.resetFilters();
			parent.selectFilter(lSource);
		}
	}

	private synchronized void orderContacts() {
		for (GuiRadarContact c : activeContactList) {
			if (c.getState() == State.IMPORTANT && !c.isNeglect()) {
				orderList.add(c);
			}
		}
		for (GuiRadarContact c : activeContactList) {
			if (c.getState() == State.CONTROLLED && !c.isNeglect()) {
				orderList.add(c);
			}
		}
		for (GuiRadarContact c : activeContactList) {
			if (c.getState() == State.UNCONTROLLED && !c.isNeglect()) {
				orderList.add(c);
			}
		}
		for (GuiRadarContact c : activeContactList) {
			if (c.isNeglect()) {
				orderList.add(c);
			}
		}
		activeContactList = new ArrayList<GuiRadarContact>(orderList);
		orderList.clear();
	}

	public DetailsFocusListener getDetailsFocusListener() {
		return detailsFocusListener;
	}

	public class DetailsFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			setAtcComment(master.getDetails());
		}
	}

	public void setAtcComment(String comment) {
		synchronized (selectedContactLock) {

			if (selectedContact != null) {
				selectedContact.setAtcComment(comment);
			}

		}
		saveAtcNotes(selectedContact);
	}

	/**
	 * Save the ATC comments of all active and expired contacts to a file, to be
	 * able to load them at restart
	 */
	public synchronized void saveAtcNotes(GuiRadarContact c) {
		atcNotesStore.updateData(c);
		atcNotesStore.saveStore();
	}

	public TransponderSettingsDialog getTransponderSettingsDialog() {
		return transponderSettingsDialog; // todo
		//return new TransponderSettingsDialog(master,this);
	}

	public void hideDialogs(boolean save) {
		atcMessageDialog.setVisible(false);
		flightplanDialog.closeDialog(save);
		transponderSettingsDialog.closeDialog();
	}

	public String[] getAutoAtcLanguages() {
		List<String> result = textManager.getLanguages();
		if (result == null)
			result = new ArrayList<String>();
		// todo change codes to texts
		return result.toArray(new String[result.size()]);
	}

	public String getAutoAtcLanguages(int index) {
		return textManager.getLanguages().get(index);
	}

	public void assignSquawkCode() {
		synchronized (selectedContactLock) {
			if (selectedContact != null) {
				try {
					assignSquawkCode(FlightPlanData.FlightType.valueOf(selectedContact.getFlightPlan().getType()));
				} catch (Exception e) {
					// flightstate not found
					log.error("Unsupported flight mode! Supported are 'VFR' and 'IFR' Value: "
							+ selectedContact.getFlightPlan().getType());
				}
			}
		}
	}

	public void assignSquawkCode(FlightPlanData.FlightType flightType) {
		String name = null;
		Integer newSquawkCode = -1;
		synchronized (selectedContactLock) {
			if (selectedContact != null) {
				if (flightType == FlightPlanData.FlightType.VFR) {
					newSquawkCode = master.getAirportData().getSquawkCodeManager()
							.getNextVFRSquawkCode(selectedContact.getAssignedSquawk());
				} else if (flightType == FlightPlanData.FlightType.IFR) {
					newSquawkCode = master.getAirportData().getSquawkCodeManager()
							.getNextIFRSquawkCode(selectedContact.getAssignedSquawk());
				} else {
					log.error("Unsupported flight mode! Supported are 'VFR' and 'IFR' Value: " + flightType);
				}
				selectedContact.getFlightPlan().setType(flightType.toString());
				selectedContact.setAssignedSquawk(newSquawkCode);
				selectedContact.getFlightPlan().setReadyForTransmission();
				master.getFlightPlanExchangeManager().triggerTransmission();
				name = selectedContact.getCallSign();
			}
		}
		if (name != null && newSquawkCode != null) {
			// auto chat message
			AtcMenuChatMessage msg = new AtcMenuChatMessage("Assign squawk");
			msg.addTranslation("en", "%s: squawk " + newSquawkCode);
			msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target");
			master.getMpChatManager().setAutoAtcMessage(selectedContact, msg);
		}
	}

	public void revokeSquawkCode() {
		synchronized (selectedContactLock) {
			if (selectedContact != null) {
				master.getAirportData().getSquawkCodeManager().revokeSquawkCode(selectedContact.getAssignedSquawk());
				selectedContact.setAssignedSquawk(null);
				selectedContact.getFlightPlan().setReadyForTransmission();
				master.getFlightPlanExchangeManager().triggerTransmission();
			}
		}
	}

	public AirportData getAirportData() {
		return master.getAirportData();
	}

	public ComboBoxModel<String> getOtherATCsCbModel() {
		DefaultComboBoxModel<String> cbm = new DefaultComboBoxModel<String>();
		cbm.addElement("");
		synchronized (activeAtcsLock) {
			if (activeAtcs != null) {
				for (FpAtc atc : activeAtcs) {
					cbm.addElement(atc.callSign);
				}
			} else {
				for (GuiRadarContact c : mapCallSignContact.values()) {
					if (c.isAtc() && c.isActive()) {
						cbm.addElement(c.getCallSign());
					}
				}
			}
		}
		return cbm;
	}

	public List<String> getOtherATCsList(boolean addRevoke) {
		List<String> list = new ArrayList<String>();
		list.add("-revoke-");
		synchronized (activeAtcsLock) {
			if (activeAtcs != null) {
				for (FpAtc atc : activeAtcs) {
					list.add(atc.callSign);
				}
			} else {
				for (GuiRadarContact c : mapCallSignContact.values()) {
					if (c.isAtc() && c.isActive()) {
						list.add(c.getCallSign());
					}
				}
			}
		}
		return list;
	}

	public List<GuiRadarContact> getContactListCopy() {
		return new ArrayList<GuiRadarContact>(completeContactList);
	}

	public TextManager getTextManager() {
		return textManager;
	}

	public String getEstimatedArrivalTime(GuiRadarContact contact) {
		try {
			String destAirport = contact.getFlightPlan().getDestinationAirport();
			if (destAirport.isEmpty() || contact.getFlightPlan().getTrueAirspeed().isEmpty()) {
				return "";
			}

			double speed = Double.parseDouble(contact.getFlightPlan().getTrueAirspeed());
			Point2D currentPos = contact.getCenterGeoCoordinates();
			Point2D airportPos = SectorCreator.findLocationOf(destAirport.trim());

			//GeoUtilInfo gi = GeoUtil.getDistanceHaversine(currentPos.getX(), currentPos.getY(), airportPos.getX(), airportPos.getY());
			double distance = GeoUtil.getDistanceHaversine(currentPos.getX(), currentPos.getY(), airportPos.getX(),
					airportPos.getY());
			;

			double time = distance / (speed * Units.KNOTS); // => seconds

			time = time > 15 * 60 ? time + 15 * 60 : time * 2; // buffer for slower flight phases

			long arrivalTime = System.currentTimeMillis() + Math.round(time * 1000);

			return sdf.format(new Date(arrivalTime));

			//            int hours   = (int)Math.floor(time/3600);
			//            int minutes = (int)Math.floor((time-hours*3600)/60);
			//            
			//            return String.format("%02d:%02d",hours,minutes);
		} catch (Exception e) {
			log.info("Problem to calculate ETA : " + e.getMessage());
			return "n/a";
		}
	}

	public boolean isFlightplanDialogVisible() {
		return flightplanDialog.isVisible();
	}

	/**
	 * Reassigns all owned and handover flightplans to the new ATC callsign.
	 * 
	 * @param currentAtcCallSign
	 * @param newAtcCallSign
	 */
	public synchronized void performAtcCallSignChange(String currentAtcCallSign, String newAtcCallSign) {
		if (currentAtcCallSign.equals(newAtcCallSign))
			return;
		boolean transmit = false;
		for (GuiRadarContact c : getContactListCopy()) {
			transmit = false;
			if (currentAtcCallSign.equals(c.getFlightPlan().getOwner())) {
				c.getFlightPlan().setOwner(newAtcCallSign);
				transmit = true;
			}
			if (currentAtcCallSign.equals(c.getFlightPlan().getHandover())) {
				c.getFlightPlan().setHandover(newAtcCallSign);
				transmit = true;
			}
			if (transmit) {
				c.getFlightPlan().setReadyForTransmission();
				master.getFlightPlanExchangeManager().triggerTransmission();
			}
		}

	}

	public void updateFlightPlan(GuiRadarContact c, FlightPlanData newFlightPlan) {
		AirportData airportData = master.getAirportData();

		FlightPlanData existingFp = c.getFlightPlan();

		String myCallSign = airportData.getCallSign();
		String owner = newFlightPlan.getOwner();
		String handover = newFlightPlan.getHandover();
		if (existingFp != null) {
			String formerHandover = existingFp.getHandover();

			existingFp.update(newFlightPlan);
			if (myCallSign.equals(owner)) {
				// OR has been restarted, the contact is still owned by me
				setContactsAlignment(c, Alignment.LEFT);

			} else if (myCallSign.equals(handover)) {
				// Contact has been OFFERED to me
				setContactsAlignment(c, Alignment.CENTER);

				// offered to me => deselect him to show it 
				if (c.isSelected()) {
					deselectContact();
				}

			} else if (!myCallSign.equals(owner) & c.getAlignment() == Alignment.LEFT) {
				// HANDOVER finalization contact is not owned by me anymore
				setContactsAlignment(c, Alignment.RIGHT);
				if (!newFlightPlan.isOwnedBySomeoneElse()) {
					//master.getFlightPlanExchangeManager().sendReleaseMessage(c);
					existingFp.releaseControl();
					existingFp.setReadyForTransmission();
				}
				deselectContact();

			} else if (myCallSign.equals(formerHandover) && ("".equals(handover) || null == handover)
					&& c.getAlignment() == Alignment.CENTER) {
				// I was handover target but the owner revoked it
				setContactsAlignment(c, Alignment.RIGHT);

			}
		} else {

		}

		// update FP Dialog
		if (flightplanDialog.shows(c)) {
			flightplanDialog.extUpdateUI(c);
		}
	}

	/**
	 * Moved to this place to avoid that the logic mixes with synchronizations.
	 */
	public void setContactsAlignment(GuiRadarContact c, Alignment newAlignment) {

		//        Alignment formerAlignment = c.getAlignment();
		FlightPlanData flightPlan = c.getFlightPlan();

		c.setAlignment(newAlignment);

		if (master.getFlightPlanExchangeManager().isFpExchangeEnabledAndActive()) {
			if (newAlignment.equals(Alignment.LEFT)) {
				if (flightPlan.isUncontrolled()) {
					// uncontrolled
					flightPlan.takeControl();
					flightPlan.setReadyForTransmission();
					master.getFlightPlanExchangeManager().triggerTransmission();

				} else if (flightPlan.isOfferedToMe()) {
					// offered to me
					flightPlan.takeControl();
					flightPlan.setReadyForTransmission();
					master.getFlightPlanExchangeManager().triggerTransmission();
				} else if (!flightPlan.isOwnedByMe()) {
					// not allowed
					c.setAlignment(Alignment.RIGHT);

				}

			} else if (newAlignment.equals(Alignment.RIGHT)) {
				deselectContact();
				if (flightPlan.isOfferedToMe()) {
					// reject handover => does not work at server yet TODO
					//                    flightPlan.setHandover(null);
					//                    flightPlan.setReadyForTransmission();
					//                    master.getFlightPlanExchangeManager().triggerTransmission();
				} else if (//formerAlignment.equals(Alignment.LEFT) &&  // was owned by me AND
				!flightPlan.isOwnedBySomeoneElse()) { // no other ATC has taken over

					//master.getFlightPlanExchangeManager().sendReleaseMessage(c);
					flightPlan.releaseControl();
					flightPlan.setReadyForTransmission();
					master.getFlightPlanExchangeManager().triggerTransmission();
				}
			}
		}
	}

	public void takeUnderControl(GuiRadarContact c) {
		setContactsAlignment(c, Alignment.LEFT);
	}

	public void releaseFromControl(GuiRadarContact c) {
		setContactsAlignment(c, Alignment.RIGHT);
	}

	public void setContactHandover(GuiRadarContact contact, String handover) {
		contact.getFlightPlan().setHandover(handover);
		contact.getFlightPlan().setReadyForTransmission();
		master.getFlightPlanExchangeManager().triggerTransmission();
	}

	public void setActiveAtcs(List<FpAtc> activeAtcs) {
		synchronized (activeAtcsLock) {
			this.activeAtcs = activeAtcs;
			mapActiveAtcs.clear();
			for (FpAtc atc : activeAtcs) {
				mapActiveAtcs.put(atc.callSign, atc);
			}
		}
	}

	public List<FpAtc> getActiveAtcs() {
		synchronized (activeAtcsLock) {
			return this.activeAtcs;
		}
	}

	public FpAtc getAtcFor(String callsign) {
		synchronized (activeAtcsLock) {
			return mapActiveAtcs.get(callsign);
		}
	}

	public void assignRoute(String newRouteName) {
		synchronized (selectedContactLock) {
			if (selectedContact != null) {
				String currentRoute = selectedContact.getFlightPlan().getAssignedRoute();
				if (newRouteName != null && newRouteName.equals(currentRoute)) {
					selectedContact.getFlightPlan().setAssignedRoute(null);
				} else {
					selectedContact.getFlightPlan().setAssignedRoute(newRouteName);
				}
			}
			master.getRadarBackend().forceRepaint();
		}
	}

	public boolean isRouteAssigned(String newRouteName) {
		synchronized (selectedContactLock) {
			return selectedContact != null && selectedContact.isActive()
					&& newRouteName.equals(selectedContact.getFlightPlan().getAssignedRoute());
		}
	}
}
