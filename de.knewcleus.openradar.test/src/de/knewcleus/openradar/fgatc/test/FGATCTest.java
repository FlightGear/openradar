package de.knewcleus.openradar.fgatc.test;

import java.io.IOException;

import de.knewcleus.openradar.vessels.fgatc.FGATCEndpoint;

public class FGATCTest {
	public static void main(String[] args) throws IOException {
		FGATCEndpoint atcEndpoint=new FGATCEndpoint(16662);
		atcEndpoint.run();
	}

}
