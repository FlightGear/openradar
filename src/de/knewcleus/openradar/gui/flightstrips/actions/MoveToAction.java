package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.EnumSet;
import java.util.Set;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.SectionData;

public class MoveToAction extends AbstractAction {
	
	public static Set<UseCase> getUseCases() {
		return EnumSet.of(UseCase.RULE);
	}
	
	private String section;
	private int column;
	
	// --- constructors ---
	
	public MoveToAction(String section, int column) {
		// section = "" : Don't change section
		// column  < 0    : Don't change column
		this.section = section;
		this.column = column;
	}

	public MoveToAction(Element element) {
		this.section = (element == null) ? "" : element.getAttributeValue("section");
		this.column = (element == null) ? 0 : Integer.valueOf(element.getAttributeValue("column"));
	}

	// --- execution ---
	
	@Override
	public void executeAction(FlightStrip flightstrip, GuiMasterController master) {
		SectionData targetSection = section.isEmpty() ? null : master.getLogicManager().getSectionsManager().getSectionByTitle(section);  
    	flightstrip.moveToPosition(targetSection == null ? flightstrip.getSection() : targetSection, (column < 0) ? flightstrip.getColumn() : column);
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		element.setAttribute("section", section);
		element.setAttribute("column", String.valueOf(column));
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSimpleText() {
		if ((section.isEmpty()) && (column < 0)) {
			return "Maintain section and column.";
		}
		else if (section.isEmpty()) {
			return "Maintain section, but move flightstrip to column '" + column + "'.";
		}
		else if (column < 0) {
			return "Move flightstrip to section '" + section + "', but maintain column.";
		}
		else {
			return "Move flightstrip to section '" + section + "' column '" + column + "'.";
		}
	}

	// --- IEditProvider ---
	
	@Override
	public int getMaxIndex() {
		return 4;
	}

	@Override
	public Type getType(int index) {
		switch (index) {
		case 1: 
		case 3: return Type.NUMBER;
		}
		return super.getType(index);
	}

	public String getStringValue(int index) {
		switch (index) {
		case 0: return "Move flightstrip to section "; 
		case 1: return section;
		case 2: return " and column "; 
		case 3: return column < 0 ? "" : String.valueOf(column);
		case 4: return "."; 
		}
		return super.getStringValue(index);
	}
	
	public void setStringValue(int index, String value) {
		switch (index) {
		case 1: section = value;
				break;
		case 3: column = value.isEmpty() ? -1 : Integer.parseInt(value);
				break;
		}
		super.setStringValue(index, value);
	}
	
	@Override
	public String getRegExp(int index) {
		int count = getMaxLength(index);
		String length = count < 0 ? "*" : "{0," + count + "}";
		switch (index) {
		case 1: return "[^\"]" + length;
		case 3: return "[0-9]" + length;
		}
		return super.getRegExp(index);
	}
	@Override
	public int getMaxLength(int index) {
		switch (index) {
		case 1: return -1;
		case 3: return 1;
		}
		return super.getMaxLength(index);
	}
	
	public String getToolTipText(int index) {
		switch (index) {
		case 1: return "empty: stay in section";
		case 3: return "empty: stay in column";
		}
		return super.getToolTipText(index);
	}
	
	public String getSection() {
		return section;
	}
	
	public int getColumn() {
		return column;
	}
	
}

