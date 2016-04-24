package de.knewcleus.openradar.gui.flightstrips.config;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.IDomElement;
import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;
import de.knewcleus.openradar.gui.flightstrips.conditions.AbstractCondition;
import de.knewcleus.openradar.gui.setup.AirportData;

public class Rule implements IDomElement {
	
	private String name;
	private AbstractCondition condition;
	private final ArrayList<AbstractAction> actions = new ArrayList<AbstractAction>();
	
	public Rule(String name, AbstractCondition condition, AbstractAction action) {
		this.name = name;
		this.condition = condition;
		if (action != null) this.actions.add(action);
	}

	public Rule(Element element) throws Exception {
		this.name = element.getAttributeValue("name");
		for (Element e : element.getChildren()) {
			AbstractCondition condition = RulesManager.createConditionClass(e);
			if (condition != null) this.condition = condition;
			else {
				AbstractAction action = RulesManager.createActionClass(e, AbstractAction.UseCase.RULE);
				if (action != null) this.actions.add(action); 
			}
		}
	}

	public boolean isAppropriate(FlightStrip flightstrip, AirportData airportData) {
		Boolean value = (condition != null) && (actions.size() > 0); 
		if (value) value = condition.isAppropriate(flightstrip, airportData); 
		return (value == null) ? false : value; 
	}
	
	public ArrayList<String> getText () {
		ArrayList<String> result = new ArrayList<String>();
		result.addAll(condition.getText ());
		for (AbstractAction action : actions) result.addAll(action.getText ());
		return result;
	}

	public String getMenuText() {
		return name;
	}
	
	public void setMenuText(String name) {
		this.name = name;
	}
	
	public AbstractCondition getCondition() {
		return condition;
	}
	
	public void setCondition(AbstractCondition condition) {
		this.condition = condition;
	}
	
	public ArrayList<AbstractAction> getActions() {
		return actions;
	}
	
	public void addAction(AbstractAction action) {
		System.out.println(action == null ? "action=<null>" : action.getSimpleText());
		if (action != null) actions.add(action);
	}
	
	public void removeAction(AbstractAction action) {
		actions.remove(action);
	}
	
	public void ApplyRule(FlightStrip flightstrip, GuiMasterController master) {
		for (AbstractAction action : actions) action.executeAction(flightstrip, master);
	}
	
	// --- IDomElement ---
	
	public static String getClassDomElementName() {
		return "rule";
	}

	@Override
	public String getDomElementName() {
		return getClassDomElementName();
	}

	@Override
	public Element createDomElement() {
		Element element = new Element(getDomElementName());
		putAttributes(element);
		if (condition != null) element.addContent(condition.createDomElement());
		for (AbstractAction action : actions) {
			if (action != null) element.addContent(action.createDomElement());
		}
		return element;
	}

	public void putAttributes(Element element) {
		element.setAttribute("name", name);
	}

}

