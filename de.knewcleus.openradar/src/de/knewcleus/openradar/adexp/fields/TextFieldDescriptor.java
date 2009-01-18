package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IBasicField;
import de.knewcleus.openradar.adexp.ParserException;

public class TextFieldDescriptor extends BasicFieldDescriptor {

	public TextFieldDescriptor(String fieldName) {
		super(fieldName);
	}

	@Override
	public IBasicField parseFieldContent(String content) throws ParserException {
		return new TextField(this, content.trim());
	}
}
