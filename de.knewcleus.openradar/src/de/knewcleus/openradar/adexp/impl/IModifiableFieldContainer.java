package de.knewcleus.openradar.adexp.impl;

import de.knewcleus.openradar.adexp.IField;
import de.knewcleus.openradar.adexp.IFieldContainer;

public interface IModifiableFieldContainer extends IFieldContainer {
	public void addField(IField field);
}
