package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IBasicFieldDescriptor;
import de.knewcleus.openradar.adexp.IFieldParser;
import de.knewcleus.openradar.adexp.ParserException;

public class TextFieldParser implements IFieldParser {

	@Override
	public TextField parseField(IBasicFieldDescriptor descriptor, String content) throws ParserException {
		return new TextField(descriptor.getFieldName(), content.trim());
	}

}
