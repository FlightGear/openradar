package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.DomAttributes;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;

public class RulesManager {
	
	public class RuleAndAction implements DomAttributes {
		
		private String name;
		private AbstractRule rule;
		private AbstractAction action;
		
		public RuleAndAction(String name, AbstractRule rule, AbstractAction action) {
			this.name = name;
			this.rule = rule;
			this.action = action;
		}

		public boolean isAppropriate(FlightStrip flightstrip) {
			return rule.isAppropriate(flightstrip); 
		}
		
		public ArrayList<String> getRuleText () {
			ArrayList<String> result = new ArrayList<String>();
			result.addAll(rule.getRuleText ());
			result.addAll(action.getActionText ());
			return result;
		}

		public String MenuText() {
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
		
		// --- DomAttributes ---
		
		@Override
		public void putAttributes(Element element) {
			element.setAttribute("name", name);
		}

	}

	// ========================================
	
	ArrayList<RuleAndAction> ruleAndActions = new ArrayList<RuleAndAction>();
	
	public void add(RuleAndAction ColumnRule) {
		ruleAndActions.add(ColumnRule);
	}
	
	public void remove(String name) {
		remove(indexOf(name));
	}
	
	public void remove(int index) {
		ruleAndActions.remove(index);
	}
	
	protected int indexOf(String name) {
		for (int i = 0; i < ruleAndActions.size(); i++) {
			if (ruleAndActions.get(i).MenuText().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public RuleAndAction find(FlightStrip flightstrip) {
		for (RuleAndAction scr : ruleAndActions) {
			if (scr.isAppropriate(flightstrip)) {
				return scr;
			}
		}
		return null;
	}

	public ArrayList<RuleAndAction> getRuleAndActions() {
		return ruleAndActions;
	}

	public void ApplyAppropriateRule(FlightStrip flightstrip) {
		if (!flightstrip.isPending()) {
			RuleAndAction scr = find(flightstrip);
			if (scr != null) scr.ApplyRule(flightstrip);
		}
	}
	
	public ArrayList<Class<? extends AbstractRule>> AvailableRules() {
		ArrayList<Class<? extends AbstractRule>> result = new ArrayList<Class<? extends AbstractRule>>();
		// set of rules
		result.add(AndRule.class);
		// always true
		result.add(AnyRule.class);
		// flight rules
		result.add(IFRRule.class);
		result.add(VFRRule.class);
		// is ATC
		result.add(ATCRule.class);
		// neglect
		result.add(NeglectRule.class);
		// controller
		result.add(AtcSelfRule.class);
		result.add(AtcNoneRule.class);
		result.add(AtcOtherRule.class);
		// airports
		result.add(DestinationRule.class);
		result.add(DestinationHereRule.class);
		result.add(DepartureRule.class);
		result.add(DepartureHereRule.class);
		// squawk
		result.add(SquawkRule.class);
		// aircraft
		result.add(HeadingRule.class);
		result.add(GroundSpeedMinRule.class);
		result.add(GroundSpeedMaxRule.class);
		result.add(AltitudeMinRule.class);
		result.add(AltitudeMaxRule.class);
		// position
		result.add(DistanceMinRule.class);
		result.add(DistanceMaxRule.class);
		result.add(DirectionRule.class);
		// TODO: add new rules
		return result;
	}

}
