package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class ActionManager {

	public static ArrayList<Class<? extends AbstractAction>> getAvailableActions() {
		ArrayList<Class<? extends AbstractAction>> result = new ArrayList<Class<? extends AbstractAction>>();
		result.add(ControlAction.class);
		result.add(MoveToAction.class);
		result.add(UncontrolAction.class);
		return result;
	}
	
	public static AbstractAction createClass(Element element, LogicManager logic) throws Exception {
		String classname = element.getName();
		Class<?> parameterTypes[] = new Class[] { Element.class , LogicManager.class };
		for (Class<? extends AbstractAction> orderclass : getAvailableActions()) {
			if (classname.equalsIgnoreCase(orderclass.getSimpleName())) {
				System.out.printf("create action '%s' end: found\n", classname);
				return orderclass.getConstructor(parameterTypes).newInstance(element, logic);
			}
		}
		return null;
	}
	
}
