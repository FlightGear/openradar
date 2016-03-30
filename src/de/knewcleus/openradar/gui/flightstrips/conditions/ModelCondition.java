package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class ModelCondition extends AbstractStringCondition {

	// --- constructors ---
	
	public ModelCondition(String model, boolean isModel) {
		super(model, isModel);
	}
	
	public ModelCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected String extractStringValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getModel();
	}

	// --- IDomElement ---
	
	@Override
	protected String getStringAttribute() {
		return "model";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_model";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's model is ";
	}
	
}
