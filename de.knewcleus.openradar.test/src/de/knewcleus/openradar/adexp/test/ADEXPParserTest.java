package de.knewcleus.openradar.adexp.test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import de.knewcleus.openradar.adexp.ADEXPParser;
import de.knewcleus.openradar.adexp.ADEXPWriter;
import de.knewcleus.openradar.adexp.IADEXPMessage;
import de.knewcleus.openradar.adexp.ParserException;
import de.knewcleus.openradar.adexp.fields.ADEXPMessageDescriptor;
import de.knewcleus.openradar.adexp.fields.ADEXPMessageType;
import de.knewcleus.openradar.adexp.fields.StructuredFieldDescriptor;
import de.knewcleus.openradar.adexp.fields.TextFieldDescriptor;

public class ADEXPParserTest {

	public static void main(String[] args) throws IOException, ParserException {
		final ADEXPMessageType messageType = new ADEXPMessageType();
		final TextFieldDescriptor facField = new TextFieldDescriptor("FAC");
		final StructuredFieldDescriptor senderDescriptor = new StructuredFieldDescriptor("SENDER");
		final StructuredFieldDescriptor rcvrDescriptor = new StructuredFieldDescriptor("RECVR");
		senderDescriptor.addField(facField);
		rcvrDescriptor.addField(facField);
		final StructuredFieldDescriptor refdataDescriptor = new StructuredFieldDescriptor("REFDATA");
		refdataDescriptor.addField(senderDescriptor);
		refdataDescriptor.addField(rcvrDescriptor);
		refdataDescriptor.addField(new TextFieldDescriptor("SEQNUM"));
		messageType.addField(refdataDescriptor);
		messageType.addField(new TextFieldDescriptor("ARCID"));
		messageType.addField(new TextFieldDescriptor("RELEASE"));
		final ADEXPMessageDescriptor descriptor = new ADEXPMessageDescriptor(messageType);
		final ADEXPParser parser = new ADEXPParser(descriptor);
		final IADEXPMessage message = parser.parseMessage("-TITLE RRQ -REFDATA -SENDER -FAC MC -RECVR -FAC E -SEQNUM 762 -ARCID KLM4273 -RELEASE D");
		final ADEXPWriter adexpwriter = new ADEXPWriter();
		final Writer writer = new OutputStreamWriter(System.out);
		adexpwriter.writeMessage(message, writer);
		writer.write('\n');
		writer.flush();
	}

}
