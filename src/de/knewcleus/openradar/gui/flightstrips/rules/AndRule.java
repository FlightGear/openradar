package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

/* This class bundles a set of rules with the AND operator together to one rule
 * 
 */
public class AndRule extends AbstractOperatorRule {

	public AndRule() {
		super();
	}

	public AndRule(ArrayList<AbstractRule> rules) {
		super(rules);
	}

	public AndRule(AbstractRule... rules) {
		super(rules);
	}

	public AndRule(Element element, LogicManager logic) throws Exception {
		super(element, logic);
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		for (AbstractRule rule : rules) {
			if (!rule.isAppropriate(flightstrip)) return false;
		}
		return true;
	}
	
	@Override
	protected String getOperatorText() {
		return "AND";
	}
	
}
