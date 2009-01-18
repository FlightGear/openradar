package de.knewcleus.openradar.adexp.fields;

import static java.util.Collections.unmodifiableCollection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.knewcleus.openradar.adexp.IFieldContainerDescriptor;
import de.knewcleus.openradar.adexp.IFieldDescriptor;

public class AbstractFieldContainerDescriptor implements IFieldContainerDescriptor {
	protected final List<IFieldDescriptor> fieldDescriptorList = new Vector<IFieldDescriptor>();
	protected final Map<String, IFieldDescriptor> fieldDescriptorMap = new HashMap<String, IFieldDescriptor>();
	
	public void addField(IFieldDescriptor fieldDescriptor) {
		if (fieldDescriptorMap.containsKey(fieldDescriptor.getFieldName())) {
			return;
		}
		fieldDescriptorMap.put(fieldDescriptor.getFieldName(), fieldDescriptor);
		fieldDescriptorList.add(fieldDescriptor);
	}

	@Override
	public IFieldDescriptor getFieldDescriptor(String name) {
		return fieldDescriptorMap.get(name);
	}

	@Override
	public boolean hasField(String name) {
		return fieldDescriptorMap.containsKey(name);
	}

	@Override
	public Iterator<IFieldDescriptor> iterator() {
		return unmodifiableCollection(fieldDescriptorList).iterator();
	}

}
