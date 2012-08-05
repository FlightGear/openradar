package de.knewcleus.openradar;

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
import de.knewcleus.fgfs.util.GeometryConversionException;
import de.knewcleus.openradar.aircraft.BuddySquawkAllocator;
import de.knewcleus.openradar.aircraft.CorrelationDatabase;
import de.knewcleus.openradar.aircraft.ICorrelationDatabase;
import de.knewcleus.openradar.aircraft.ISquawkAllocator;
import de.knewcleus.openradar.sector.Sector;
import de.knewcleus.openradar.ui.RadarWorkstation;
import de.knewcleus.openradar.ui.SetupDialog;
import de.knewcleus.openradar.ui.plaf.refghmi.REFGHMILookAndFeel;
import de.knewcleus.openradar.vessels.IPositionDataProvider;
import de.knewcleus.openradar.vessels.fgmp.ATCClient;
import de.knewcleus.openradar.vessels.fgmp.FGMPAircraft;
import de.knewcleus.openradar.vessels.fgmp.FGMPRegistry;

public class OpenRadar {
	public static void main(String[] args) throws DBParserException, IOException, ClassNotFoundException, MultiplayerException, ParserConfigurationException, SAXException, GeometryConversionException {

		Logger rootLogger=Logger.getLogger("de.knewcleus");
		rootLogger.setUseParentHandlers(false);
		rootLogger.setLevel(Level.FINE);
		Handler handler=new ConsoleHandler();
		handler.setLevel(Level.FINE);
		rootLogger.addHandler(handler);

		Logger.getLogger("de.knewcleus.fgfs").setLevel(Level.SEVERE);
		Logger.getLogger("de.knewcleus.openradar").setLevel(Level.SEVERE);

        LookAndFeel refghmiLookAndFeel=new REFGHMILookAndFeel();
        try {
            UIManager.setLookAndFeel(refghmiLookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            Logger.getLogger("de.knewcleus.openradar").log(Level.SEVERE, "Look and Feel could not be initialized", e);
        }
		
		/* Load sector data */
		SetupDialog sectorSelectionDialog=new SetupDialog();
		sectorSelectionDialog.setVisible(true);

		URL sectorURL=sectorSelectionDialog.getSelectedURL();
		if (sectorURL==null)
			return;
		Sector sector=Sector.loadFromURL(sectorURL);

		/* Prepare radar data provider */
		IPositionDataProvider radarDataProvider;
		ISquawkAllocator squawkAllocator=new BuddySquawkAllocator();
		ICorrelationDatabase correlationDatabase=new CorrelationDatabase();
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

		/* Setup the user interface */
		RadarWorkstation radarWorkstation=new RadarWorkstation(sector,radarDataProvider,squawkAllocator,correlationDatabase);
		radarWorkstation.setVisible(true);
	}
}
