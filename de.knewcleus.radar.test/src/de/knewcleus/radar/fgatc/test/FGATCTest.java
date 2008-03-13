package de.knewcleus.radar.fgatc.test;

import java.io.IOException;

import de.knewcleus.radar.aircraft.fgatc.FGATCEndpoint;

public class FGATCTest {
	public static void main(String[] args) throws IOException {
		FGATCEndpoint atcEndpoint=new FGATCEndpoint(16662);
		atcEndpoint.run();
	}

}
