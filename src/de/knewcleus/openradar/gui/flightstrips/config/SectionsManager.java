package de.knewcleus.openradar.gui.flightstrips.config;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.SectionData;

public class SectionsManager implements ListModel<SectionData> {

	private final LogicManager logicManager;
	
	private final ArrayList<SectionData> sections = new ArrayList<SectionData>();

	private SectionColumnDialog dialog = null;
	
	private boolean isDragging = false;
	
	// --- constructor ---
	
	public SectionsManager(LogicManager logicManager) {
		this.logicManager = logicManager;
	}

	// --- link provider ---
	
	public LogicManager getLogicManager() {
		return logicManager;
	}
	
	// --- ArrayList<SectionData> sections ---
	
	public ArrayList<SectionData> getSections() {
		// should only be used read-only
		return sections;
	}
	
	public void clear() {
		int i = sections.size();
		sections.clear();
		notifyClearListeners(i);
	}
	
	public SectionData addSection(String title) {
		SectionData result = new SectionData(this, title); 
		sections.add(result);
		if ((result != null) && (sections.add(result))) notifyAddListeners(sections.indexOf(result));
		return result;
	}

	public SectionData addSection(String title, String... columnTitles) {
		SectionData result = new SectionData(this, title, columnTitles); 
		if ((result != null) && (sections.add(result))) notifyAddListeners(sections.indexOf(result));
		return result;
	}
	
	public void add(SectionData section) {
		if ((section != null) && (sections.add(section))) notifyAddListeners(sections.indexOf(section));
	}
	
	public void setSectionTitle(SectionData section, String title) {
		section.setTitle(title);
		notifyChangedListeners(sections.indexOf(section));
	}

	public void removeSection(SectionData section) {
		if (section != null) {
			int i = sections.indexOf(section);
			if (sections.remove(section)) notifyRemoveListeners(i);
		}
	}
	
	public void moveSectionToIndex (SectionData section, int target_index) {
		int source_index = sections.indexOf(section);
		if (target_index < 0) target_index = 0;
		if (target_index >= sections.size()) target_index = sections.size() - 1;
		if (source_index != target_index) {
			if (sections.remove(section)) {
				notifyRemoveListeners(source_index);
				sections.add(target_index, section);
				notifyAddListeners(target_index);
			}
		}
	}

	public void moveSectionUp (SectionData section) {
		moveSectionToIndex(section, sections.indexOf(section) - 1);
	}
	
	public void moveSectionDown (SectionData section) {
		moveSectionToIndex(section, sections.indexOf(section) + 1);
	}
	
	public SectionData getPreviousSection (SectionData section) {
		int i = sections.indexOf(section) - 1;
		try {
			return sections.get(i); 
		}
		catch (IndexOutOfBoundsException e) {
			return null; 
		}
	}
	
	public SectionData getNextSection (SectionData section) {
		int i = sections.indexOf(section) + 1;
		try {
			return sections.get(i); 
		}
		catch (IndexOutOfBoundsException e) {
			return null; 
		}
	}
	
	public SectionData getSectionByTitle (String title) {
		for (SectionData section : sections) {
			if (title.equalsIgnoreCase(section.getTitle())) return section;
		}
		return null;
	}

	public void updateFlightstrips(List<GuiRadarContact> contacts, RulesManager rulesManager) {
		// collect all flightstrips
		ArrayList<FlightStrip> flightstrips = new ArrayList<FlightStrip>();
		for (SectionData section : sections) {
			flightstrips.addAll(section.getFlightStrips());
		}
		// update existing and remove expired flightstrips
		for (FlightStrip flightstrip : flightstrips) {
			GuiRadarContact contact = flightstrip.getContact();
			if (contacts.contains(contact)) {
				// contact exists
				flightstrip.updateContents();
				rulesManager.ApplyAppropriateRule(flightstrip);
				// remove contact from list, so that new contacts remain in list
				contacts.remove(contact);
			}
			else {
				// contact is expired
				flightstrip.remove();
			}
		}
		// create new flightstrips
		for (GuiRadarContact contact : contacts) {
			FlightStrip flightstrip = new FlightStrip(contact);
			flightstrip.updateContents();
			rulesManager.ApplyAppropriateRule(flightstrip);
		}
		// order flightstrips within the section
		for (SectionData section : sections) {
			section.reorderFlightStrips();
		}
	}
	
	public void showSectionColumnDialog(SectionData section, Point p) {
		if (dialog == null) dialog = new SectionColumnDialog(this);
		dialog.setTopRight(p);
		dialog.setSection(section, false);
		dialog.setVisible(true);
	}
	
	public void setIsDragging (boolean isDragging) {
		this.isDragging = isDragging;
		for (SectionData section : sections) {
			section.getPanel().checkVisible();
		}
	}
	
	public boolean getIsDragging () {
		return isDragging;
	}
	
	// --- ListModel ---
	
	private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	@Override
	public int getSize() {
		return sections.size();
	}

	@Override
	public SectionData getElementAt(int index) {
		return sections.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	public void notifyAddListeners(int index) {
		for (ListDataListener l : listeners) l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
	}
	
	public void notifyRemoveListeners(int index) {
		for (ListDataListener l : listeners) l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED , index, index));
	}
	
	public void notifyClearListeners(int index) {
		for (ListDataListener l : listeners) l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED , 0, index));
	}
	
	public void notifyChangedListeners(int index) {
		for (ListDataListener l : listeners) l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, sections.size() - 1));
	}
	
}
