package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

/* This class bundles a set of rules with the AND operator together to one rule
 * 
 */
public class AndRule extends AbstractRule {

	ArrayList<AbstractRule> rules = null;
	
	public AndRule() {
		rules = new ArrayList<AbstractRule>();
	}

	public AndRule(ArrayList<AbstractRule> rules) {
		this.rules = rules;
	}

	public AndRule(AbstractRule... rules) {
		this.rules = new ArrayList<AbstractRule>();
		for (AbstractRule rule : rules) this.rules.add(rule);
	}

	public void add (AbstractRule rule) {
		rules.add(rule);
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		boolean result = true;
		for (AbstractRule rule : rules) {
			result &= rule.isAppropriate(flightstrip);
			if (!result) break;
		}
		return result;
	}
	
	@Override
	public ArrayList<String> getRuleText () {
		// TODO: insert "AND"
		ArrayList<String> result = new ArrayList<String>();
		for (AbstractRule rule : rules) {
			result.addAll(rule.getRuleText ());
		}
		return result;
	}
	
	@Override
	public ArrayList<AbstractRule> getRules() {
		return rules;
	}
	
}
