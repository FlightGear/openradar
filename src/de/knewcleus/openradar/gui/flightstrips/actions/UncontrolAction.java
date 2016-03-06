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
	
	// --- constructors ---
	
	public UncontrolAction() {
	}
	
	public UncontrolAction(Element element) {
	}
	
	// --- execution ---
	
	@Override
	public void executeAction(FlightStrip flightstrip, GuiMasterController master) {
		GuiRadarContact contact = flightstrip.getContact();
		FlightPlanData fpd = contact.getFlightPlan();
		if (fpd.isOwnedByMe()) {
			contact.getManager().releaseFromControl(contact);
		}		
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSimpleText() {
		return "release control";
	}

	// --- IEditProvider ---
	
	@Override
	public int getMaxIndex() {
		return super.getMaxIndex() + 1;
	}

	public String getStringValue(int index) {
		switch (index) {
		case 0: return getSimpleText(); 
		}
		return super.getStringValue(index);
	}
	
	public String getToolTipText(int index) {
		switch (index) {
		case 0: return "<html>Release contact from your control</html>";
		}
		return super.getToolTipText(index);
	}
	
}
