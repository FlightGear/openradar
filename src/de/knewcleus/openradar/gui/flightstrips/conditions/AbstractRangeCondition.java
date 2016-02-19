package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

public abstract class AbstractRangeCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public AbstractRangeCondition(boolean isInRange) {
		super(isInRange);
	}

	public AbstractRangeCondition(Element element) {
		super(element);
	}
	
	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "is_in_range";
	}

	protected String getMinAttribute() {
		return "min_value";
	}
	
	protected String getMaxAttribute() {
		return "max_value";
	}

	// --- IRuleTextProvider ---
	
	@Override
	protected String getTextline() {
		return super.getTextline() + "between " + getFormattedMinValue() + " " + getUnit() + " and " + getFormattedMaxValue() + " " + getUnit();
	}

	public String getUnit() {
		return "";
	}
	
	public abstract String getFormattedMinValue();
	
	public abstract String getFormattedMaxValue();
	
	// --- IEditProvider ---
	
	private int mapIndex(int index) {
		return index < 2 ? index : index - 5;
	}
	
	@Override
	public int getMaxIndex() {
		return super.getMaxIndex() + 6;
	}

	@Override
	public Type getType(int index) {
		switch (index) {
		case 2: return Type.TEXT;
		case 3: return Type.NUMBER;
		case 4: return Type.TEXT;
		case 5: return Type.NUMBER;
		case 6: return Type.TEXT;
		}
		return super.getType(mapIndex(index));
	}

	public String getStringValue(int index) {
		switch (index) {
		case 2: return " between ";
		case 3: return getFormattedMinValue();
		case 4: return getUnit() + " and ";
		case 5: return getFormattedMaxValue();
		case 6: return getUnit();
		}
		return super.getStringValue(mapIndex(index));
	}
	
	public String getToolTipText(int index) {
		switch (index) {
		case 3: return "enter a number";
		case 5: return "enter a number";
		}
		return super.getToolTipText(mapIndex(index));
	}
	
}
