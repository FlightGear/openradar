package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public abstract class AbstractOperatorRule extends AbstractRule {

	ArrayList<AbstractRule> rules = null;
	
	public AbstractOperatorRule() {
		rules = new ArrayList<AbstractRule>();
	}

	public AbstractOperatorRule(ArrayList<AbstractRule> rules) {
		this.rules = rules;
	}

	public AbstractOperatorRule(AbstractRule... rules) {
		this.rules = new ArrayList<AbstractRule>();
		for (AbstractRule rule : rules) this.rules.add(rule);
	}

	public AbstractOperatorRule(Element element, LogicManager logic) throws Exception {
		this.rules = new ArrayList<AbstractRule>();
		for (Element e : element.getChildren()) {
			AbstractRule rule = RuleManager.createClass(e, logic);
			if (rule != null) add(rule);
		}
	}
	
	public void add (AbstractRule rule) {
		rules.add(rule);
	}
	
	@Override
	public ArrayList<String> getRuleText () {
		ArrayList<String> result = new ArrayList<String>();
		result.add(getOperatorText());
		String indent = "   ";
		for (AbstractRule rule : rules) {
			for (String text : rule.getRuleText()) result.add(indent + text);
		}
		return result;
	}
	
	protected abstract String getOperatorText();
	
	// --- IDomElement ---
	
	@Override
	public Element createDomElement() {
		Element element = super.createDomElement();
		for (AbstractRule rule : rules) {
			element.addContent(rule.createDomElement());
		}
		return element;
	}

}
