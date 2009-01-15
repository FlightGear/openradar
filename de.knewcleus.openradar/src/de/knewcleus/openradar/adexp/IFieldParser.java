package de.knewcleus.openradar.adexp;

public interface IFieldParser {
	public IBasicField parseField(IBasicFieldDescriptor descriptor, String content) throws ParserException;
}
