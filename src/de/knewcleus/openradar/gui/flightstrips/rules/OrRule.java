package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class OrRule extends AbstractRule {

	ArrayList<AbstractRule> rules = null;
	
	public OrRule() {
		rules = new ArrayList<AbstractRule>();
	}

	public OrRule(ArrayList<AbstractRule> rules) {
		this.rules = rules;
	}

	public OrRule(AbstractRule... rules) {
		this.rules = new ArrayList<AbstractRule>();
		for (AbstractRule rule : rules) this.rules.add(rule);
	}

	public void add (AbstractRule rule) {
		rules.add(rule);
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		boolean result = false;
		for (AbstractRule rule : rules) {
			result |= rule.isAppropriate(flightstrip);
			if (result) break;
		}
		return result;
	}
	
	@Override
	public ArrayList<String> getRuleText () {
		// TODO: insert "OR"
		ArrayList<String> result = new ArrayList<String>();
		for (AbstractRule rule : rules) {
			result.addAll(rule.getRuleText ());
		}
		return result;
	}
	
}
