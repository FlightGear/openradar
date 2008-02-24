package de.knewcleus.radar.ui;

import java.io.IOException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.LocalProjection;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.radar.Scenario;
import de.knewcleus.radar.aircraft.fgmp.ATCClient;
import de.knewcleus.radar.aircraft.fgmp.FGMPRegistry;
import de.knewcleus.radar.sector.Sector;
import de.knewcleus.radar.ui.rpvd.ConsoleFrame;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;

public class Radar {
	public static void main(String[] args) throws DBParserException, IOException, ClassNotFoundException, MultiplayerException, ParserConfigurationException, SAXException {
		Logger rootLogger=Logger.getLogger("de.knewcleus");
		rootLogger.setUseParentHandlers(false);
		rootLogger.setLevel(Level.SEVERE);
		Handler handler=new ConsoleHandler();
		handler.setLevel(Level.SEVERE);
		rootLogger.addHandler(handler);
		Sector sector;
		
		URL sectorURL=Radar.class.getResource("/sectors/KSFO/sector.xml");
		sector=Sector.loadFromURL(sectorURL);

		GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
		
		Scenario scenario=new Scenario(sector);
		FGMPRegistry registry=new FGMPRegistry(scenario);
		ATCClient multiplayerClient=new ATCClient(registry,"obsKSFO",geodToCartTransformation.backward(sector.getInitialCenter()));
		Updater multiplayerUpdater=new Updater(multiplayerClient,500);
		multiplayerClient.start();
		multiplayerUpdater.start();
		
		Updater scenarioUpdater=new Updater(scenario,100);
		scenarioUpdater.start();
		
		RadarPlanViewSettings radarPlanViewSettings=new RadarPlanViewSettings();
		radarPlanViewSettings.setMapTransformation(new LocalProjection(sector.getInitialCenter()));
		ConsoleFrame consoleFrame=new ConsoleFrame("Console",scenario,radarPlanViewSettings);
		consoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		consoleFrame.setVisible(true);
	}
}
