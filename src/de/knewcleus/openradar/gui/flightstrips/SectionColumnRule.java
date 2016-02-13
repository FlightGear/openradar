package de.knewcleus.openradar.gui.flightstrips;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.rules.AbstractBooleanRule;

public class SectionColumnRule extends AbstractBooleanRule {

	private final SectionData section; // null : any section
	private final int         column;  // <0   : any column
	
	public SectionColumnRule(SectionData section, int column, boolean isIn) {
		super(isIn);
		this.section = section;
		this.column  = column;
	}
	
	public SectionColumnRule(Element element, LogicManager logic) {
		super(element, logic);
		this.section = logic.getSectionByTitle(element.getAttributeValue("section"));
		this.column = Integer.valueOf(element.getAttributeValue("column"));
	}

	@Override
	protected String getTextline() {
		return "flightstrip is " + super.getTextline() + "in " + (section == null ? "any section" : "section '" + section.getTitle() + "'") + 
				   " and in " + (column < 0 ? "any column." : "column " + column + (section == null ? "." : " '" + section.getColumn(column).getTitle() + "'."));
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "is_in";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		return ((section == null) || (section.equals(flightstrip.getSection()))) && ((column < 0) || (column == flightstrip.getColumn()));
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("section", section.getTitle());
		element.setAttribute("column", String.valueOf(column));
	}

}
