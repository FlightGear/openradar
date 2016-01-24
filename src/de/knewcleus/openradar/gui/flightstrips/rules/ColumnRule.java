package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class ColumnRule extends AbstractRule {

	private final int column;
	private final boolean isIn;
	
	public ColumnRule(int column, boolean isIn) {
		this.column = column;
		this.isIn = isIn;
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return (flightstrip.getColumn() == column) == isIn;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("flightstrip is " + (isIn ? "" : "not") + " in column " + column + ".");
		return result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("column", String.valueOf(column));
		element.setAttribute("isin", String.valueOf(isIn));
	}

}
