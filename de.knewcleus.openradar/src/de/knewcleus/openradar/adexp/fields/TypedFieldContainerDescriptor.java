package de.knewcleus.openradar.adexp.fields;

import java.util.Iterator;

import de.knewcleus.openradar.adexp.IFieldContainerDescriptor;
import de.knewcleus.openradar.adexp.IFieldDescriptor;

public class TypedFieldContainerDescriptor implements IFieldContainerDescriptor {
	protected final FieldContainerType type;

	public TypedFieldContainerDescriptor() {
		this.type = new FieldContainerType();
	}
	
	public TypedFieldContainerDescriptor(FieldContainerType type) {
		this.type = type;
	}

	@Override
	public IFieldDescriptor getFieldDescriptor(String name) {
		return type.getFieldDescriptor(name);
	}

	@Override
	public boolean hasField(String name) {
		return type.hasField(name);
	}

	@Override
	public Iterator<IFieldDescriptor> iterator() {
		return type.iterator();
	}

	public void addField(IFieldDescriptor fieldDescriptor) {
		type.addField(fieldDescriptor);
	}
}
