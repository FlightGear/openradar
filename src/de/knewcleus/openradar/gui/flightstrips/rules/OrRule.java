package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

/* This class bundles a set of rules with the OR operator together to one rule
 * 
 */
public class OrRule extends AbstractOperatorRule {

	public OrRule() {
		super();
	}

	public OrRule(ArrayList<AbstractRule> rules) {
		super(rules);
	}

	public OrRule(AbstractRule... rules) {
		super(rules);
	}

	public OrRule(Element element, LogicManager logic) throws Exception {
		super(element, logic);
	}
	
	@Override
	public Boolean isAppropriate(FlightStrip flightstrip) {
		for (AbstractRule rule : rules) {
			Boolean value = rule.isAppropriate(flightstrip);
			if ((value == null) || value) return value; 
		}
		return false;
	}
	
	@Override
	protected String getOperatorText() {
		return "OR";
	}
	
}
