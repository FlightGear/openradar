package de.knewcleus.openradar.adexp.test;

import java.io.IOException;

import de.knewcleus.openradar.adexp.ADEXPParser;
import de.knewcleus.openradar.adexp.IADEXPMessage;
import de.knewcleus.openradar.adexp.ParserException;
import de.knewcleus.openradar.adexp.fields.ADEXPMessageDescriptor;
import de.knewcleus.openradar.adexp.fields.BasicFieldDescriptor;
import de.knewcleus.openradar.adexp.fields.StructuredFieldDescriptor;
import de.knewcleus.openradar.adexp.fields.TextFieldParser;

public class ADEXPParserTest {

	public static void main(String[] args) throws IOException, ParserException {
		final ADEXPMessageDescriptor descriptor = new ADEXPMessageDescriptor();
		final StructuredFieldDescriptor senderDescriptor = new StructuredFieldDescriptor("SENDER");
		final StructuredFieldDescriptor rcvrDescriptor = new StructuredFieldDescriptor("RECVR");
		senderDescriptor.putField(new BasicFieldDescriptor("FAC", new TextFieldParser()));
		rcvrDescriptor.putField(new BasicFieldDescriptor("FAC", new TextFieldParser()));
		final StructuredFieldDescriptor refdataDescriptor = new StructuredFieldDescriptor("REFDATA");
		refdataDescriptor.putField(senderDescriptor);
		refdataDescriptor.putField(rcvrDescriptor);
		refdataDescriptor.putField(new BasicFieldDescriptor("SEQNUM", new TextFieldParser()));
		descriptor.putField(refdataDescriptor);
		descriptor.putField(new BasicFieldDescriptor("ARCID", new TextFieldParser()));
		descriptor.putField(new BasicFieldDescriptor("RELEASE", new TextFieldParser()));
		final ADEXPParser parser = new ADEXPParser(descriptor);
		final IADEXPMessage message = parser.parseMessage("-TITLE RRQ -REFDATA -SENDER -FAC MC -RECVR -FAC E -SEQNUM 762 -ARCID KLM4273 -RELEASE D");
		System.out.println(message);
	}

}
