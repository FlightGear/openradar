package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.EnumSet;
import java.util.Set;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class UncontrolAction extends AbstractAction {

	public static Set<UseCase> getUseCases() {
		return EnumSet.of(UseCase.COLUMN);
	}
	
	public UncontrolAction() {
	}
	
	public UncontrolAction(Element element) {
	}
	
	@Override
	public String getSimpleText() {
		 return "release control";
	}

	@Override
	public void executeAction(FlightStrip flightstrip, GuiMasterController master) {
		GuiRadarContact contact = flightstrip.getContact();
		FlightPlanData fpd = contact.getFlightPlan();
		if (fpd.isOwnedByMe()) {
			contact.getManager().releaseFromControl(contact);
		}		
	}

}
