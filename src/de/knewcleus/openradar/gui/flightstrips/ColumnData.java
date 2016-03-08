package de.knewcleus.openradar.gui.flightstrips;

import java.util.ArrayList;
import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;
import de.knewcleus.openradar.gui.flightstrips.config.RulesManager;

public class ColumnData implements IDomElement {

	private String title = "";
	private final ArrayList<AbstractAction> enterActions = new ArrayList<AbstractAction>(); 
	private final ArrayList<AbstractAction> exitActions = new ArrayList<AbstractAction>();
	
	public enum PaintLevel { BOTTOM, LOWER, NORMAL, HIGHER, TOP };
	protected PaintLevel paintLevel = PaintLevel.NORMAL;
	
	// --- constructors ---
	
	public ColumnData(String title) {
		this.title = title;
	}

	public ColumnData(Element element) throws Exception {
		String s;
		s = element.getAttributeValue("title");
		if (s != null) title = s;
		s = element.getAttributeValue("paint_priority");
		if (s != null) paintLevel = PaintLevel.valueOf(s);
		// actions
		Element e;
		e = element.getChild("enter");
		if (e != null) {
			for (Element a : e.getChildren()) {
				AbstractAction action = RulesManager.createActionClass(a, AbstractAction.UseCase.COLUMN); 
				if (action != null) addAction(true, action); 
			}
		}
		e = element.getChild("exit");
		if (e != null) {
			for (Element a : e.getChildren()) {
				AbstractAction action = RulesManager.createActionClass(a, AbstractAction.UseCase.COLUMN); 
				if (action != null) addAction(false, action); 
			}
		}
	}

	// --- title ---
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	// --- paintLevel ---
	
	public PaintLevel getPaintLevel() {
		return paintLevel;
	}

	public void setPaintLevel(PaintLevel paintLevel) {
		this.paintLevel = paintLevel;
	}

	public static PaintLevel[] getPaintLevels() {
		PaintLevel[] result = PaintLevel.values();
		for (int i = 0, j = result.length - 1; i < result.length / 2; i++, j--) {
			PaintLevel t = result [i];
			result [i] = result [j];
			result [j] = t;
		}
		return result;
	}
	
	// --- actions ---
	
	public void addAction(boolean enter, AbstractAction action) {
		if (action != null) {
			if (enter) enterActions.add(action);
			else exitActions.add(action);
		}
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
		for (AbstractAction action : enterActions) action.executeAction(flightstrip, flightstrip.getSection().getSectionsManager().getLogicManager().getGuiMasterController());
	}
	
	public void executeExitActions(FlightStrip flightstrip) {
		for (AbstractAction action : exitActions) action.executeAction(flightstrip, flightstrip.getSection().getSectionsManager().getLogicManager().getGuiMasterController());
	}

	// --- IDomElement ---
	
	public static String getClassDomElementName() {
		return "column";
	}

	@Override
	public String getDomElementName() {
		return getClassDomElementName();
	}

	@Override
	public Element createDomElement() {
		// column
		Element element = new Element(getDomElementName());
		element.setAttribute("title", title);
		element.setAttribute("paint_priority", String.valueOf(paintLevel));
		// actions
		Element e;
		if (enterActions.size() > 0) {
			e = new Element("enter");
			element.addContent(e);
			for (AbstractAction action : enterActions) e.addContent(action.createDomElement());
		}
		if (exitActions.size() > 0) {
			e = new Element("exit");
			element.addContent(e);
			for (AbstractAction action : exitActions) e.addContent(action.createDomElement());
		}
		return element;
	}
	
}
