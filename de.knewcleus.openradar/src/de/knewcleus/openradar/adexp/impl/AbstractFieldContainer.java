package de.knewcleus.openradar.adexp.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import de.knewcleus.openradar.adexp.IField;

public abstract class AbstractFieldContainer implements IModifiableFieldContainer {

	protected final List<IField> fieldList = new Vector<IField>();

	public AbstractFieldContainer() {
		super();
	}

	@Override
	public int countByName(String fieldname) {
		final Iterator<IField> iterator = fieldIterator(fieldname);
		int count = 0;
		while (iterator.hasNext()) {
			count++;
			iterator.next();
		}
		return count;
	}

	@Override
	public Iterator<IField> fieldIterator(String fieldname) {
		return new FieldIterator(fieldname);
	}

	@Override
	public boolean hasField(String fieldname) {
		return findNextField(fieldname, 0)!=-1;
	}

	@Override
	public int size() {
		return fieldList.size();
	}

	@Override
	public Iterator<IField> iterator() {
		return Collections.unmodifiableList(fieldList).iterator();
	}

	@Override
	public void addField(IField field) {
		fieldList.add(field);
	}

	protected int findNextField(String fieldName, int startIndex) {
		final int size= size();
		for (int index=startIndex; index<size; index++) {
			final IField field = fieldList.get(startIndex);
			if (field.getDescriptor().getFieldName().equalsIgnoreCase(fieldName)) {
				return index;
			}
		}
		return -1;
	}

	protected class FieldIterator implements Iterator<IField> {
		protected final String fieldName;
		protected int nextIndex;
		
		public FieldIterator(String fieldName) {
			this.fieldName = fieldName;
			nextIndex = findNextField(fieldName, 0);
		}
		
		@Override
		public boolean hasNext() {
			return (nextIndex!=-1);
		}
		
		@Override
		public IField next() {
			if (nextIndex==-1) {
				throw new NoSuchElementException();
			}
			final IField field = fieldList.get(nextIndex);
			nextIndex = findNextField(fieldName, nextIndex+1);
			return field;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String toString() {
		boolean first = true;
		String result = "";
		for (IField field: this) {
			if (!first) {
				result+=" ";
			}
			first= false;
			result+=field.toString();
		}
		return result;
	}
}