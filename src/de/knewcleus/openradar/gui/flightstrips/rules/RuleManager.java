package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class RuleManager {
	
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
	
	public static ArrayList<Class<? extends AbstractRule>> getAvailableRules() {
		ArrayList<Class<? extends AbstractRule>> result = new ArrayList<Class<? extends AbstractRule>>();
		// boolean operation with rules
		result.add(AndRule.class);
		result.add(OrRule.class);
		// always true
		result.add(AnyRule.class);
		// flight rules
		result.add(IFRRule.class);
		result.add(VFRRule.class);
		// is ATC
		result.add(ATCRule.class);
		// status
		result.add(NewRule.class);
		result.add(ActiveRule.class);
		result.add(NeglectRule.class);
		result.add(EmergencyRule.class);
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
		result.add(AircraftRule.class);
		result.add(HeadingRule.class);
		result.add(GroundSpeedMinRule.class);
		result.add(GroundSpeedMaxRule.class);
		result.add(AltitudeMinRule.class);
		result.add(AltitudeMaxRule.class);
		// position
		result.add(DistanceMinRule.class);
		result.add(DistanceMaxRule.class);
		result.add(DirectionRule.class);
		// flightstrip bay
		result.add(ColumnRule.class);
		return result;
	}

	public static AbstractRule createClass(Element element, LogicManager logic) throws Exception {
		String classname = element.getName();
		Class<?> parameterTypes[] = new Class[] { Element.class, LogicManager.class };
		for (Class<? extends AbstractRule> orderclass : getAvailableRules()) {
			if (classname.equalsIgnoreCase(orderclass.getSimpleName())) {
				System.out.printf("create rule '%s' end: found\n", classname);
				return orderclass.getConstructor(parameterTypes).newInstance(element, logic);
			}
		}
		return null;
	}
	
}
