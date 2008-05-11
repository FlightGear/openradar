package de.knewcleus.radar.ui;

import java.io.IOException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.radar.aircraft.BuddySquawkAllocator;
import de.knewcleus.radar.aircraft.CorrelationDatabase;
import de.knewcleus.radar.aircraft.ICorrelationDatabase;
import de.knewcleus.radar.aircraft.ISquawkAllocator;
import de.knewcleus.radar.sector.Sector;
import de.knewcleus.radar.ui.plaf.refghmi.REFGHMILookAndFeel;
import de.knewcleus.radar.vessels.IPositionDataProvider;
import de.knewcleus.radar.vessels.fgatc.FGATCEndpoint;
import de.knewcleus.radar.vessels.fgmp.ATCClient;
import de.knewcleus.radar.vessels.fgmp.FGMPAircraft;
import de.knewcleus.radar.vessels.fgmp.FGMPRegistry;

public class Radar {
	public static void main(String[] args) throws DBParserException, IOException, ClassNotFoundException, MultiplayerException, ParserConfigurationException, SAXException {
		LookAndFeel refghmiLookAndFeel=new REFGHMILookAndFeel();
		try {
			UIManager.setLookAndFeel(refghmiLookAndFeel);
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Logger rootLogger=Logger.getLogger("de.knewcleus");
		rootLogger.setUseParentHandlers(false);
		rootLogger.setLevel(Level.FINE);
		Handler handler=new ConsoleHandler();
		handler.setLevel(Level.FINE);
		rootLogger.addHandler(handler);
		
		Logger.getLogger("de.knewcleus.fgfs").setLevel(Level.SEVERE);
		Logger.getLogger("de.knewcleus.radar").setLevel(Level.SEVERE);
		
		/* Load sector data */
		URL sectorURL=Radar.class.getResource("/sectors/EDDI/sector.xml");
		Sector sector=Sector.loadFromURL(sectorURL);

		/* Prepare radar data provider */
		IPositionDataProvider radarDataProvider;
		ISquawkAllocator squawkAllocator=new BuddySquawkAllocator();
		ICorrelationDatabase correlationDatabase=new CorrelationDatabase();
		if (Boolean.getBoolean("de.knewcleus.radar.useMPProtocol")) {
			/* Use multiplayer data */
			GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
			FGMPRegistry registry=new FGMPRegistry(squawkAllocator,correlationDatabase);
			ATCClient<FGMPAircraft> multiplayerClient=new ATCClient<FGMPAircraft>(registry,"obsKSFO",geodToCartTransformation.backward(sector.getInitialCenter()));
			Thread multiplayerClientThread=new Thread(multiplayerClient,"FlightGear Multiplayer Protocol Handler");
			Updater multiplayerUpdater=new Updater(multiplayerClient,500);
			multiplayerClientThread.setDaemon(true);
			multiplayerClientThread.start();
			multiplayerUpdater.start();
			radarDataProvider=registry;
		} else {
			FGATCEndpoint atcEndpoint=new FGATCEndpoint(16662);
			Thread atcEndpointThread=new Thread(atcEndpoint,"FGATC Protocol Handler");
			atcEndpointThread.setDaemon(true);
			atcEndpointThread.start();
			radarDataProvider=atcEndpoint;
		}
		
		/* Setup the user interface */
		RadarWorkstation radarWorkstation=new RadarWorkstation(sector,radarDataProvider,squawkAllocator,correlationDatabase);
		radarWorkstation.setVisible(true);
	}
}
