package de.knewcleus.fgfs.multiplayer.protocol;

public class PropertyDescriptor {
	protected final int propertyID;
	protected final String propertyName;
	protected final PropertyType type;
	
	public PropertyDescriptor(int propertyID, String propertyName, PropertyType type) {
		this.propertyID=propertyID;
		this.propertyName=propertyName;
		this.type=type;
	}
	
	public int getPropertyID() {
		return propertyID;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public PropertyType getType() {
		return type;
	}
}
