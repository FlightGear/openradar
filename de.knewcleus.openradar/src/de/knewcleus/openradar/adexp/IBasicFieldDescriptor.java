package de.knewcleus.openradar.adexp;


public interface IBasicFieldDescriptor extends IFieldDescriptor {
	public IBasicField parseFieldContent(String content) throws ParserException;
}
