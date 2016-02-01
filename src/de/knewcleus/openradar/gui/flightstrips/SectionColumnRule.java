package de.knewcleus.openradar.gui.flightstrips;

import java.util.ArrayList;

import de.knewcleus.openradar.gui.flightstrips.rules.AbstractRule;

public class SectionColumnRule extends AbstractRule {

	private final boolean     isIn;
	private final SectionData section; // null : any section
	private final int         column;  // <0   : any column
	
	public SectionColumnRule(boolean isIn, SectionData section, int column) {
		this.isIn    = isIn;
		this.section = section;
		this.column  = column;
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return isIn == (((section == null) || (section.equals(flightstrip.getSection()))) && ((column < 0) || (column == flightstrip.getColumn())));
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("flightstrip is " + (isIn ? "" : "not") + "in " + (section == null ? "any section" : "section '" + section.getTitle() + "'") + 
				   " and in " + (column < 0 ? "any column." : "column " + column + (section == null ? "." : " '" + section.getColumn(column).getTitle() + "'.")));
		return result;
	}

}
