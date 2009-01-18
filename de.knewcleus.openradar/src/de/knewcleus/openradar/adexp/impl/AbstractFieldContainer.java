package de.knewcleus.openradar.adexp.impl;

import static java.util.Collections.unmodifiableCollection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.knewcleus.openradar.adexp.IField;
import de.knewcleus.openradar.adexp.IFieldContainer;

public abstract class AbstractFieldContainer implements IFieldContainer, IFieldRecipient {
	protected final Map<String, IField> fieldMap = new HashMap<String, IField>();
	
	public void addField(IField field) {
		fieldMap.put(field.getDescriptor().getFieldName(), field);
	}

	@Override
	public IField getField(String fieldname) {
		return fieldMap.get(fieldname);
	}

	@Override
	public boolean hasField(String fieldname) {
		return fieldMap.containsKey(fieldname);
	}

	@Override
	public Iterator<IField> iterator() {
		return unmodifiableCollection(fieldMap.values()).iterator();
	}

	@Override
	public String toString() {
		String result="";
		boolean first=true;
		for (IField field: fieldMap.values()) {
			if (!first) {
				result+=" ";
			}
			first=false;
			result+=field.toString();
		}
		return result;
	}
}
