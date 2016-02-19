package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.EnumSet;
import java.util.Set;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class NoAction extends AbstractAction {

	public static Set<UseCase> getUseCases() {
		return EnumSet.of(UseCase.RULE);
	}
	
	// --- constructors ---
	
	public NoAction() {
	}

	public NoAction(Element element) {
	}

	// --- execution ---
	
	@Override
	public void executeAction(FlightStrip flightstrip, GuiMasterController master) {
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSimpleText() {
		return "no action (Do nothing)";
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
		case 0: return "<html>Use this action to prevent that the selected flightstrips are checked by the rules below</html>";
		}
		return super.getToolTipText(index);
	}
	
}
