package de.knewcleus.openradar.gui.flightstrips;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;

public class ColumnData implements DomAttributes {

	private String title = "";
	private final ArrayList<AbstractAction> enterActions = new ArrayList<AbstractAction>(); 
	private final ArrayList<AbstractAction> exitActions = new ArrayList<AbstractAction>(); 
	
	// --- constructors ---
	
	public ColumnData(String title) {
		this.title = title;
	}

	// --- title ---
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		if (this.title != title) {
			this.title = title;
			// TODO: broadcast message: contacts section column title changed
		}
	}
	
	// --- actions ---
	
	public void addAction(boolean enter, AbstractAction action) {
		if (enter) enterActions.add(action);
		else exitActions.add(action);
	}

	public void removeAction(boolean enter, AbstractAction action) {
		if (enter) enterActions.remove(action);
		else exitActions.remove(action);
	}
	
	public ArrayList<AbstractAction> getEnterActions() {
		return enterActions;
	}
	
	public ArrayList<AbstractAction> getExitActions() {
		return exitActions;
	}
	
	public void executeEnterActions(FlightStrip flightstrip) {
		for (AbstractAction action : enterActions) action.executeAction(flightstrip);
	}
	
	public void executeExitActions(FlightStrip flightstrip) {
		for (AbstractAction action : exitActions) action.executeAction(flightstrip);
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		element.setAttribute("title", title);
	}
	
}
