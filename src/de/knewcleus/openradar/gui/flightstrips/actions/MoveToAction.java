package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.SectionData;

public class MoveToAction extends AbstractAction {
	
	private final SectionData section;
	private final int column;
	
	public MoveToAction(SectionData section, int column) {
		// section = null : Don't change section
		// column  < 0    : Don't change column
		this.section = section;
		this.column = column;
	}

	public SectionData getSection() {
		return section;
	}
	
	public int getColumn() {
		return column;
	}
	
	@Override
	public ArrayList<String> getActionText () {
		ArrayList<String> result = new ArrayList<String>();
		if ((section == null) && (column < 0)) {
			result.add("stay in section and column");
		}
		else if (section == null) {
			result.add("stay in section, but move to column '" + column + "'");
		}
		else if (column < 0) {
			result.add("move to section '" + section.getTitle() + "', but stay in column'");
		}
		else {
			result.add("move to section '" + section.getTitle() + "' column '" + section.getColumn(column).getTitle() + "'");
		}
		return result;
	}

	@Override
	public void executeAction(FlightStrip flightstrip) {
    	flightstrip.moveToPosition((section == null) ? flightstrip.getSection() : section, (column < 0) ? flightstrip.getColumn() : column);
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		element.setAttribute("section", section.getTitle());
		element.setAttribute("column", String.valueOf(column));
	}

}

