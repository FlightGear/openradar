package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class ColumnCondition extends AbstractIntegerCondition {

	// --- constructors ---
	
	public ColumnCondition(int column, boolean isIn) {
		super(column, isIn);
	}
	
	public ColumnCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Integer extractIntegerValue(FlightStrip flightstrip) {
		return flightstrip.getColumn();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "is_in_column";
	}

	@Override
	protected String getNumberAttribute() {
		return "column";
	}
	
	// --- IRuleTextProvider ---
	
	protected String getPrefixText() {
		return "flightstrip is ";
	}
	
	@Override
	protected String getTextline() {
		return (booleanValue ? "" : "not ") + " in column " + getFormattedValue();
	}
	
	// --- IEditProvider ---
	
	private int mapIndex(int index) {
		return index < 2 ? index : index - 1;
	}
	
	@Override
	public int getMaxIndex() {
		return super.getMaxIndex() + 1;
	}

	@Override
	public Type getType(int index) {
		switch (index) {
		case 2: return Type.TEXT;
		}
		return super.getType(mapIndex(index));
	}
	
	public String getStringValue(int index) {
		switch (index) {
		case 2: return " in column ";
		}
		return super.getStringValue(mapIndex(index));
	}
	
	@Override
	public void setStringValue(int index, String value) {
		super.setStringValue(mapIndex(index), value);
	}

	@Override
	public String getRegExp(int index) {
		return super.getRegExp(mapIndex(index));
	}

	@Override
	public int getMaxLength(int index) {
		return super.getMaxLength(mapIndex(index));
	}

}
