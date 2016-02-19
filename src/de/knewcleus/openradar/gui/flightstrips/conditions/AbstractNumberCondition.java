package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

public abstract class AbstractNumberCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public AbstractNumberCondition(boolean booleanValue) {
		super(booleanValue);
	}
	
	public AbstractNumberCondition(Element element) {
		super(element);
	}

	// --- IDomElement ---
	
	protected String getNumberAttribute() {
		return "number_value";
	}
	
	// --- IRuleTextProvider ---
	
	@Override
	protected String getTextline() {
		return super.getTextline() + getFormattedValue() + getUnit();
	}

	public String getUnit() {
		return "";
	}
	
	public abstract String getFormattedValue();

	// --- IEditProvider ---
	
	private int mapIndex(int index) {
		return index < 2 ? index : index - 2;
	}
	
	@Override
	public int getMaxIndex() {
		return super.getMaxIndex() + 2;
	}

	@Override
	public Type getType(int index) {
		switch (index) {
		case 2: return Type.NUMBER;
		case 3: return Type.TEXT;
		}
		return super.getType(mapIndex(index));
	}

	@Override
	public String getStringValue(int index) {
		switch (index) {
		case 2: return getFormattedValue();
		case 3: return getUnit();
		}
		return super.getStringValue(mapIndex(index));
	}
	
	@Override
	public String getToolTipText(int index) {
		switch (index) {
		case 2: return "enter a number";
		}
		return super.getToolTipText(mapIndex(index));
	}
	
}
