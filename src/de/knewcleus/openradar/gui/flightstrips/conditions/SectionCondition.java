package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class SectionCondition extends AbstractStringCondition {

	// --- constructors ---
	
	public SectionCondition(String section, boolean isIn) {
		super(section, isIn);
	}
	
	public SectionCondition(Element element) {
		super(element);
	}

	// --- compare ---
	
	@Override
	protected String extractStringValue(FlightStrip flightstrip) {
		return (flightstrip.getSection() == null) ? "" : flightstrip.getSection().getTitle();
	}
	
	// --- IDomElement ---
	
	@Override
	protected String getStringAttribute() {
		return "section";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_in";
	}

	@Override
	public String getPrefixText() {
		return "flightstrip is ";
	}
	
	// --- IRuleTextProvider ---
	
	@Override
	protected String getTextline() {
		return (booleanValue ? "" : "not ") + "in a section named like '" + regexp + "'";
	}

	// --- IEditProvider ---
	
	public String getStringValue(int index) {
		switch (index) {
		case 2: return " in a section named like ";
		}
		return super.getStringValue(index);
	}
	
}
