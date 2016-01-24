package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class UncontrolAction extends AbstractAction {

	public UncontrolAction() {
	}
	
	public UncontrolAction(Element element, LogicManager logic) {
	}
	
	@Override
	public ArrayList<String> getActionText () {
		ArrayList<String> result = new ArrayList<String>();
		result.add("release control");
		return result;
	}

	@Override
	public void executeAction(FlightStrip flightstrip) {
		GuiRadarContact contact = flightstrip.getContact();
		FlightPlanData fpd = contact.getFlightPlan();
		if (fpd.isOwnedByMe()) {
			contact.getManager().releaseFromControl(contact);
		}		
	}

}
