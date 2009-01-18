package de.knewcleus.openradar.adexp;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ADEXPWriter {
	public void writeMessage(IADEXPMessage message, Writer writer)
		throws IOException, ParserException
	{
		boolean first = true;
		for (IField field: message) {
			if (!first) {
				writer.append(' ');
			}
			writeField(field, writer);
			first=false;
		}
	}
	
	public void writeField(IField field, Writer writer) throws IOException, ParserException {
		if (field instanceof IBasicField) {
			writeBasicField((IBasicField)field, writer);
		} else if (field instanceof IStructuredField) {
			writeStructuredField((IStructuredField)field, writer);
		} else if (field instanceof IListField) {
			writeListField((IListField)field, writer);
		} else {
			throw new ParserException("Unknown field type: "+field.getClass());
		}
	}
	
	public void writeStructuredField(IStructuredField structuredField, Writer writer)  throws IOException, ParserException
	{
		writer.append('-');
		writer.append(structuredField.getDescriptor().getFieldName().toUpperCase());
		for (IField field: structuredField) {
			writer.append(' ');
			writeField(field, writer);
		}
	}

	public void writeListField(IListField listField, Writer writer) throws IOException, ParserException
	{
		writer.append("-BEGIN ");
		writer.append(listField.getDescriptor().getFieldName());
		for (IField field: listField) {
			writer.append(' ');
			writeField(field, writer);
		}
		writer.append(" -END ");
		writer.append(listField.getDescriptor().getFieldName());
	}
	
	public void writeBasicField(IBasicField field, Writer writer) throws IOException, ParserException
	{
		writer.append('-');
		writer.append(field.getDescriptor().getFieldName().toUpperCase());
		writer.append(' ');
		writer.append(field.valueToString());
	}
	
	public String messageToString(IADEXPMessage message) throws IOException, ParserException {
		StringWriter writer = new StringWriter();
		writeMessage(message, writer);
		return writer.toString();
	}
}
