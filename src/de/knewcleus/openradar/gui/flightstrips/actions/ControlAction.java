package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class ControlAction extends AbstractAction {

	public ControlAction() {
	}
	
	public ControlAction(Element element, LogicManager logic) {
	}
	
	@Override
	public ArrayList<String> getText () {
		ArrayList<String> result = new ArrayList<String>();
		result.add("take control");
		return result;
	}

	@Override
	public void executeAction(FlightStrip flightstrip) {
		GuiRadarContact contact = flightstrip.getContact();
		FlightPlanData fpd = contact.getFlightPlan();
		if (fpd.isUncontrolled() || fpd.isOfferedToMe()) {
			contact.getManager().takeUnderControl(contact);
		}
	}

}
