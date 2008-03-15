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
import de.knewcleus.radar.aircraft.IRadarDataProvider;
import de.knewcleus.radar.aircraft.fgatc.FGATCEndpoint;
import de.knewcleus.radar.aircraft.fgmp.ATCClient;
import de.knewcleus.radar.aircraft.fgmp.FGMPAircraft;
import de.knewcleus.radar.aircraft.fgmp.FGMPRegistry;
import de.knewcleus.radar.sector.Sector;
import de.knewcleus.radar.ui.plaf.refghmi.REFGHMILookAndFeel;

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
		
		Logger.getLogger("de.knewcleus.radar.ui.rpvd").setLevel(Level.SEVERE);
		Logger.getLogger("de.knewcleus.fgfs.multiplayer").setLevel(Level.SEVERE);
		
		/* Load sector data */
		URL sectorURL=Radar.class.getResource("/sectors/KSFO/sector.xml");
		Sector sector=Sector.loadFromURL(sectorURL);

		IRadarDataProvider radarDataProvider;
		/* Prepare radar data provider */
		if (Boolean.getBoolean("de.knewcleus.radar.useMPProtocol")) {
			/* Use multiplayer data */
			GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
			FGMPRegistry registry=new FGMPRegistry();
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
		RadarWorkstation radarWorkstation=new RadarWorkstation(sector,radarDataProvider);
		radarWorkstation.setVisible(true);
	}
}
