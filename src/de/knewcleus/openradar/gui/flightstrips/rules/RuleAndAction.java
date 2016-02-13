package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.IDomElement;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;
import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;
import de.knewcleus.openradar.gui.flightstrips.actions.ActionManager;

public class RuleAndAction implements IDomElement {
	
	private String name;
	private AbstractRule rule;
	private AbstractAction action;
	
	public RuleAndAction(String name, AbstractRule rule, AbstractAction action) {
		this.name = name;
		this.rule = rule;
		this.action = action;
	}

	public RuleAndAction(Element element, LogicManager logic) throws Exception {
		this.name = element.getAttributeValue("name");
		for (Element e : element.getChildren()) {
			AbstractRule rule = RulesManager.createClass(e, logic);
			if (rule != null) this.rule = rule;
			else {
				AbstractAction action = ActionManager.createClass(e, logic);
				if (action != null) this.action = action; 
			}
		}
	}

	public boolean isAppropriate(FlightStrip flightstrip) {
		Boolean value = rule.isAppropriate(flightstrip); 
		return (value == null) ? false : value; 
	}
	
	public ArrayList<String> getText () {
		ArrayList<String> result = new ArrayList<String>();
		result.addAll(rule.getText ());
		result.addAll(action.getText ());
		return result;
	}

	public String getMenuText() {
		return name;
	}
	
	public AbstractRule getRule() {
		return rule;
	}
	
	public AbstractAction getAction() {
		return action;
	}
	
	public void ApplyRule(FlightStrip flightstrip) {
		action.executeAction(flightstrip);
	}
	
	// --- IDomElement ---
	
	public static String getClassDomElementName() {
		return "ruleandaction";
	}

	@Override
	public String getDomElementName() {
		return getClassDomElementName();
	}

	@Override
	public Element createDomElement() {
		Element element = new Element(getDomElementName());
		putAttributes(element);
		element.addContent(rule.createDomElement());
		element.addContent(action.createDomElement());
		return element;
	}

	public void putAttributes(Element element) {
		element.setAttribute("name", name);
	}

}

