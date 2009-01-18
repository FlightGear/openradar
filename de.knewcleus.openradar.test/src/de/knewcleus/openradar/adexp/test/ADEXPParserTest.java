package de.knewcleus.openradar.adexp.test;

import java.io.IOException;

import de.knewcleus.openradar.adexp.ADEXPParser;
import de.knewcleus.openradar.adexp.ADEXPWriter;
import de.knewcleus.openradar.adexp.IADEXPMessage;
import de.knewcleus.openradar.adexp.ParserException;
import de.knewcleus.openradar.adexp.fields.ADEXPMessageDescriptor;
import de.knewcleus.openradar.adexp.fields.StructuredFieldDescriptor;
import de.knewcleus.openradar.adexp.fields.TextFieldDescriptor;

public class ADEXPParserTest {

	public static void main(String[] args) throws IOException, ParserException {
		final ADEXPMessageDescriptor descriptor = new ADEXPMessageDescriptor();
		final StructuredFieldDescriptor senderDescriptor = new StructuredFieldDescriptor("SENDER");
		final StructuredFieldDescriptor rcvrDescriptor = new StructuredFieldDescriptor("RECVR");
		final TextFieldDescriptor facField = new TextFieldDescriptor("FAC");
		senderDescriptor.addField(facField);
		rcvrDescriptor.addField(facField);
		final StructuredFieldDescriptor refdataDescriptor = new StructuredFieldDescriptor("REFDATA");
		refdataDescriptor.addField(senderDescriptor);
		refdataDescriptor.addField(rcvrDescriptor);
		refdataDescriptor.addField(new TextFieldDescriptor("SEQNUM"));
		descriptor.addField(refdataDescriptor);
		descriptor.addField(new TextFieldDescriptor("ARCID"));
		descriptor.addField(new TextFieldDescriptor("RELEASE"));
		final ADEXPParser parser = new ADEXPParser(descriptor);
		final IADEXPMessage message = parser.parseMessage("-TITLE RRQ -REFDATA -SENDER -FAC MC -RECVR -FAC E -SEQNUM 762 -ARCID KLM4273 -RELEASE D");
		final ADEXPWriter writer = new ADEXPWriter();
		System.out.println(writer.messageToString(message));
	}

}
