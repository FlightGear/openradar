package de.knewcleus.openradar.adexp;

public interface IFieldContainer extends Iterable<IField> {
	public boolean hasField(String fieldname);
	public IField getField(String fieldname);
}
