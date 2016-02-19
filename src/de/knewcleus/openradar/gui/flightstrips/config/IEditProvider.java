package de.knewcleus.openradar.gui.flightstrips.config;

public interface IEditProvider {
	
	public enum Type { TEXT, STRING, NUMBER, LIST }; 
	
	public int getMaxIndex();
	public Type getType(int index);
	public String getStringValue(int index);
	public void setStringValue(int index, String value);
	public String getRegExp(int index);
	public int getMaxLength(int index);
	public String[] getStringList(int index);
	public int getIndexedValue(int index);
	public void setIndexedValue(int index, int value);
	public String getToolTipText(int index);
}
