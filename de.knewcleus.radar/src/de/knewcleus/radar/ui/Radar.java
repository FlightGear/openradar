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
import de.knewcleus.fgfs.location.LocalProjection;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.radar.aircraft.fgmp.ATCClient;
import de.knewcleus.radar.aircraft.fgmp.FGMPAircraft;
import de.knewcleus.radar.aircraft.fgmp.FGMPRegistry;
import de.knewcleus.radar.sector.Sector;
import de.knewcleus.radar.ui.plaf.refghmi.REFGHMILookAndFeel;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;

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
		rootLogger.setLevel(Level.SEVERE);
		Handler handler=new ConsoleHandler();
		handler.setLevel(Level.SEVERE);
		rootLogger.addHandler(handler);
		
		/* Load sector data */
		URL sectorURL=Radar.class.getResource("/sectors/KSFO/sector.xml");
		Sector sector=Sector.loadFromURL(sectorURL);

		/* Prepare radar data provider */
		GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
		FGMPRegistry registry=new FGMPRegistry();
		ATCClient<FGMPAircraft> multiplayerClient=new ATCClient<FGMPAircraft>(registry,"obsKSFO",geodToCartTransformation.backward(sector.getInitialCenter()));
		Updater multiplayerUpdater=new Updater(multiplayerClient,500);
		multiplayerClient.start();
		multiplayerUpdater.start();
		
		/* Setup the user interface */
		RadarWorkstation radarWorkstation=new RadarWorkstation(sector,registry);
		RadarPlanViewSettings radarPlanViewSettings=radarWorkstation.getRadarPlanViewSettings();
		radarPlanViewSettings.setMapTransformation(new LocalProjection(sector.getInitialCenter()));
		radarWorkstation.setVisible(true);
	}
}
