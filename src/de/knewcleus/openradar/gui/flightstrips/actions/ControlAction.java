package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.EnumSet;
import java.util.Set;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class ControlAction extends AbstractAction {

	public static Set<UseCase> getUseCases() {
		return EnumSet.of(UseCase.COLUMN);
	}
	
	public ControlAction() {
	}
	
	public ControlAction(Element element) {
	}
	
	@Override
	public String getSimpleText() {
		return "take control";
	}

	@Override
	public void executeAction(FlightStrip flightstrip, GuiMasterController master) {
		GuiRadarContact contact = flightstrip.getContact();
		FlightPlanData fpd = contact.getFlightPlan();
		if (fpd.isUncontrolled() || fpd.isOfferedToMe()) {
			contact.getManager().takeUnderControl(contact);
		}
	}

}
