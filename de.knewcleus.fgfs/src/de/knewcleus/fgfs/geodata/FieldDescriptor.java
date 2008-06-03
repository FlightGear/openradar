package de.knewcleus.fgfs.geodata;

public class FieldDescriptor {
	protected final String name;
	protected final FieldType type;
	
	public FieldDescriptor(String name, FieldType type) {
		this.name=name;
		this.type=type;
	}
	
	public String getName() {
		return name;
	}
	
	public FieldType getType() {
		return type;
	}
}
