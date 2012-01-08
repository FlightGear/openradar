package cz.muni.fi.openradar;

import cz.muni.fi.openradar.ui.HlaSetupDialog;
import cz.muni.fi.openradar.vessels.hla.HlaRegistry;
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

public class OpenRadarHla {

    public static void main(String[] args) throws DBParserException, IOException, ClassNotFoundException, MultiplayerException, ParserConfigurationException, SAXException, GeometryConversionException {
        LookAndFeel refghmiLookAndFeel = new REFGHMILookAndFeel();
        try {
            UIManager.setLookAndFeel(refghmiLookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Logger rootLogger = Logger.getLogger("de.knewcleus");
        rootLogger.setUseParentHandlers(false);
        rootLogger.setLevel(Level.FINE);
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        rootLogger.addHandler(handler);

        Logger.getLogger("de.knewcleus.fgfs").setLevel(Level.SEVERE);
        Logger.getLogger("de.knewcleus.openradar").setLevel(Level.SEVERE);

        /* Load sector data */
        HlaSetupDialog sectorSelectionDialog = new HlaSetupDialog();
        sectorSelectionDialog.setVisible(true);

        URL sectorURL = sectorSelectionDialog.getSelectedURL();
        if (sectorURL == null) {
            return;
        }
        Sector sector = Sector.loadFromURL(sectorURL);

        /* Prepare radar data provider */
        IPositionDataProvider radarDataProvider;
        ISquawkAllocator squawkAllocator = new BuddySquawkAllocator();
        ICorrelationDatabase correlationDatabase = new CorrelationDatabase();
        /* Use multiplayer data */
        GeodToCartTransformation geodToCartTransformation = new GeodToCartTransformation(Ellipsoid.WGS84);
        //FGMPRegistry registry=new FGMPRegistry(squawkAllocator,correlationDatabase);
        HlaRegistry registry = new HlaRegistry();
        radarDataProvider = registry;

        /* Setup the user interface */
        RadarWorkstation radarWorkstation = new RadarWorkstation(sector, radarDataProvider, squawkAllocator, correlationDatabase);
        radarWorkstation.setVisible(true);
    }
}
