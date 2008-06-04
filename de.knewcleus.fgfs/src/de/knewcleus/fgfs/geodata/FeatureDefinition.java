package de.knewcleus.fgfs.geodata;

public class FeatureDefinition {
	protected final FieldDescriptor[] fieldDescriptors;
	
	public FeatureDefinition(FieldDescriptor[] fieldDescriptors) {
		this.fieldDescriptors=fieldDescriptors;
	}
	
	public FieldDescriptor[] getFieldDescriptors() {
		return fieldDescriptors;
	}
	
	public int getColumnCount() {
		return fieldDescriptors.length;
	}
	
	public int getColumnIndex(String name) {
		for (int i=0;i<fieldDescriptors.length;i++) {
			if (fieldDescriptors[i].getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
}
