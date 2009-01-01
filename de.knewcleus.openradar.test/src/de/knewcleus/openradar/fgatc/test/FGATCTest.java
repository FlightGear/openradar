package de.knewcleus.openradar.fgatc.test;

import java.io.IOException;

import de.knewcleus.openradar.radardata.fgatc.FGATCEndpoint;

public class FGATCTest {
	public static void main(String[] args) throws IOException {
		final FGATCEndpoint atcEndpoint=new FGATCEndpoint(16662);
		final RadarDataRecipient recipient = new RadarDataRecipient();
		atcEndpoint.registerRecipient(recipient);
		atcEndpoint.run();
	}
}
