package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class ColumnRule extends AbstractBooleanRule {

	private final int column;
	
	public ColumnRule(int column, boolean isIn) {
		super(isIn);
		this.column = column;
	}
	
	public ColumnRule(Element element, LogicManager logic) {
		super(element, logic);
		this.column = Integer.valueOf(element.getAttributeValue("column"));
	}
	
	@Override
	protected String getTextline() {
		return "flightstrip is " + super.getTextline() + "in column " + String.format("%d", column) + ".";
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "is_in_column";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		return flightstrip.getColumn() == column;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("column", String.valueOf(column));
	}

}
