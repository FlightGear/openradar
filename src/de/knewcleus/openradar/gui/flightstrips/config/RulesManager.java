package de.knewcleus.openradar.gui.flightstrips.config;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;
import de.knewcleus.openradar.gui.flightstrips.actions.ControlAction;
import de.knewcleus.openradar.gui.flightstrips.actions.MoveToAction;
import de.knewcleus.openradar.gui.flightstrips.actions.NoAction;
import de.knewcleus.openradar.gui.flightstrips.actions.UncontrolAction;
import de.knewcleus.openradar.gui.flightstrips.conditions.AGLCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.ATCCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AbstractCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.ActiveCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AircraftCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AltitudeCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AndCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AnyCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AtcNoneCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AtcOtherCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AtcSelfCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.CallsignCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.ColumnCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.DepartureCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.DepartureHereCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.DestinationCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.DestinationHereCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.DirectionCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.DistanceCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.EmergencyCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.GroundSpeedCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.HeadingCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.IFRCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.NeglectCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.NewCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.OrCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.RelativeHeadingCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.SectionCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.SquawkCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.VFRCondition;

public class RulesManager implements ListModel<Rule> {
	
	private final ArrayList<Rule> rules = new ArrayList<Rule>();
	private boolean active = false; 
	
	private final GuiMasterController master;
    private RulesDialog dialog = null;
	
    public RulesManager(GuiMasterController master) {
		this.master = master;
	}
    
	public void clear() {
		int i = rules.size();
		rules.clear();
		notifyClearListeners(i);
	}
	
	public void add(Rule rule) {
		if ((rule != null) && (rules.add(rule))) notifyAddListeners(rules.indexOf(rule));
	}
	
	public void renameRule (Rule rule, String name) {
		rule.setMenuText(name);
		notifyChangedListeners(rules.indexOf(rule));
	}

	public void remove(Rule rule) {
		if (rule != null) {
			int i = rules.indexOf(rule);
			if (rules.remove(rule)) notifyRemoveListeners(i);
		}
	}
	
	public void remove(String name) {
		remove(indexOf(name));
	}
	
	public void remove(int index) {
		rules.remove(index);
		notifyRemoveListeners(index);
	}

	public void moveRule (Rule rule, int steps) {
		int source_index = rules.indexOf(rule);
		int target_index = source_index + steps;
		if (target_index < 0) target_index = 0;
		if (target_index >= rules.size()) target_index = rules.size() - 1;
		if (source_index != target_index) {
			if (rules.remove(rule)) {
				notifyRemoveListeners(source_index);
				rules.add(target_index, rule);
				notifyAddListeners(target_index);
			}
		}
	}
	
	protected int indexOf(String name) {
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i).getMenuText().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public Rule find(FlightStrip flightstrip) {
		for (Rule rule : rules) {
			if (rule.isAppropriate(flightstrip, master.getAirportData())) {
				return rule;
			}
		}
		return null;
	}

	public ArrayList<Rule> getRules() {
		return rules;
	}

	public void ApplyAppropriateRule(FlightStrip flightstrip) {
		if (active && !flightstrip.isPending()) {
			Rule rule = find(flightstrip);
			if (rule != null) rule.ApplyRule(flightstrip, master);
		}
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	// --- conditions registration ---
	
	public static ArrayList<Class<? extends AbstractCondition>> getAvailableConditions() {
		ArrayList<Class<? extends AbstractCondition>> result = new ArrayList<Class<? extends AbstractCondition>>();
		// boolean operation with rules
		result.add(AndCondition.class);
		result.add(OrCondition.class);
		// always true
		result.add(AnyCondition.class);
		// flight rules
		result.add(IFRCondition.class);
		result.add(VFRCondition.class);
		// is ATC
		result.add(ATCCondition.class);
		// status
		result.add(NewCondition.class);
		result.add(ActiveCondition.class);
		result.add(NeglectCondition.class);
		result.add(EmergencyCondition.class);
		// controller
		result.add(AtcSelfCondition.class);
		result.add(AtcNoneCondition.class);
		result.add(AtcOtherCondition.class);
		// airports
		result.add(DestinationCondition.class);
		result.add(DestinationHereCondition.class);
		result.add(DepartureCondition.class);
		result.add(DepartureHereCondition.class);
		// squawk
		result.add(SquawkCondition.class);
		// aircraft
		result.add(CallsignCondition.class);
		result.add(AircraftCondition.class);
		result.add(HeadingCondition.class);
		result.add(GroundSpeedCondition.class);
		result.add(AltitudeCondition.class);
		result.add(AGLCondition.class);
		// position
		result.add(DistanceCondition.class);
		result.add(DirectionCondition.class);
		// aircraft heading relative to airport direction
		result.add(RelativeHeadingCondition.class);
		// flightstrip bay
		result.add(ColumnCondition.class);
		result.add(SectionCondition.class);
		return result;
	}

	public static String[] getAvailableConditionsNames() {
		ArrayList<Class<? extends AbstractCondition>> conditionsclasses = getAvailableConditions();
		String[] result = new String[conditionsclasses.size() + 1];
		for (int i = 0; i < conditionsclasses.size(); i++) {
			result[i + 1] = conditionsclasses.get(i).getSimpleName();
		}
		return result;
	}
	
	public static AbstractCondition createConditionClassByName(String classname) throws Exception {
		return doCreateConditionClass(classname, null);
	}

	public static AbstractCondition createConditionClass(Element element) throws Exception {
		return doCreateConditionClass(element.getName(), element);
	}
	protected static AbstractCondition doCreateConditionClass(String classname, Element element) throws Exception {
		Class<?> parameterTypes[] = new Class[] { Element.class };
		for (Class<? extends AbstractCondition> conditionclass : getAvailableConditions()) {
			if (classname.equalsIgnoreCase(conditionclass.getSimpleName())) {
				//System.out.printf("create condition '%s' end: found\n", classname);
				return conditionclass.getConstructor(parameterTypes).newInstance(element);
			}
		}
		return null;
	}

	// --- actions registration ---
	
	public static ArrayList<Class<? extends AbstractAction>> getAvailableActions(AbstractAction.UseCase useCase) {
		ArrayList<Class<? extends AbstractAction>> result = new ArrayList<Class<? extends AbstractAction>>();
		// ATC control
		if (ControlAction.getUseCases().contains(useCase))   result.add(ControlAction.class);
		if (UncontrolAction.getUseCases().contains(useCase)) result.add(UncontrolAction.class);
		// section, column
		if (MoveToAction.getUseCases().contains(useCase))    result.add(MoveToAction.class);
		// other
		if (NoAction.getUseCases().contains(useCase))        result.add(NoAction.class);
		return result;
	}
	
	public static String[] getAvailableActionsNames(AbstractAction.UseCase useCase) {
		ArrayList<Class<? extends AbstractAction>> actionsclasses = getAvailableActions(useCase);
		String[] result = new String[actionsclasses.size() + 1];
		for (int i = 0; i < actionsclasses.size(); i++) {
			result[i + 1] = actionsclasses.get(i).getSimpleName();
		}
		return result;
	}
	
	public static AbstractAction createActionClassByName(String classname, AbstractAction.UseCase useCase) throws Exception {
		return doCreateActionClass(classname, null, useCase);
	}

	public static AbstractAction createActionClass(Element element, AbstractAction.UseCase useCase) throws Exception {
		return doCreateActionClass(element.getName(), element, useCase);
	}
	
	protected static AbstractAction doCreateActionClass(String classname, Element element, AbstractAction.UseCase useCase) throws Exception {
		if (classname != null) {
			Class<?> parameterTypes[] = new Class[] { Element.class };
			for (Class<? extends AbstractAction> orderclass : getAvailableActions(useCase)) {
				if (classname.equalsIgnoreCase(orderclass.getSimpleName())) {
					//System.out.printf("create action '%s' end: found\n", classname);
					return orderclass.getConstructor(parameterTypes).newInstance(element);
				}
			}
		}
		return null;
	}
	
	// --- dialog ---
	
	public void showDialog() {
		if (dialog == null) dialog = new RulesDialog(this);
		dialog.showDialog();
	}

	// --- ListModel ---
	
	private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	@Override
	public int getSize() {
		return rules.size();
	}

	@Override
	public Rule getElementAt(int index) {
		return rules.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	public void notifyAddListeners(int index) {
		for (ListDataListener l : listeners) l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
	}
	
	public void notifyRemoveListeners(int index) {
		for (ListDataListener l : listeners) l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED , index, index));
	}
	
	public void notifyClearListeners(int index) {
		for (ListDataListener l : listeners) l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED , 0, index));
	}
	
	public void notifyChangedListeners(int index) {
		for (ListDataListener l : listeners) l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, rules.size() - 1));
	}
	
}
