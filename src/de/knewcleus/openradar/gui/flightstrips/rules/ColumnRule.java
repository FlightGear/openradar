package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class ColumnRule extends AbstractRule {

	private final int column;
	private final boolean isIn;
	
	public ColumnRule(int column, boolean isIn) {
		this.column = column;
		this.isIn = isIn;
	}
	
	public ColumnRule(Element element, LogicManager logic) {
		this.column = Integer.valueOf(element.getAttributeValue("column"));
		this.isIn = Boolean.valueOf(element.getAttributeValue("isin"));
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

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("column", String.valueOf(column));
		element.setAttribute("isin", String.valueOf(isIn));
	}

}
