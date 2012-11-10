package de.knewcleus.openradar.gui.radar;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JComponent;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.shapefile.ZippedShapefileLayer;
import de.knewcleus.fgfs.navdata.FilteredNavDataStream;
import de.knewcleus.fgfs.navdata.INavDatumFilter;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.NavDatumFilterChain;
import de.knewcleus.fgfs.navdata.NavDatumFilterChain.Kind;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavDatum;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.AptDatStream;
import de.knewcleus.fgfs.navdata.xplane.FixDatStream;
import de.knewcleus.fgfs.navdata.xplane.NavDatStream;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.SetupDialog;
import de.knewcleus.openradar.radardata.SwingRadarDataAdapter;
import de.knewcleus.openradar.rpvd.RadarMapViewerAdapter;
import de.knewcleus.openradar.rpvd.RadarTargetProvider;
import de.knewcleus.openradar.rpvd.ScaleMarkerView;
import de.knewcleus.openradar.rpvd.ScaleMarkerView.Side;
import de.knewcleus.openradar.view.ComponentCanvas;
import de.knewcleus.openradar.view.LayeredRadarContactView;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.MouseZoomListener;
import de.knewcleus.openradar.view.SwingUpdateManager;
import de.knewcleus.openradar.view.ViewerCenteringListener;
import de.knewcleus.openradar.view.map.GeodataView;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.LocalSphericalProjection;
import de.knewcleus.openradar.view.mouse.FocusManager;
import de.knewcleus.openradar.view.mouse.IFocusManager;
import de.knewcleus.openradar.view.mouse.MouseFocusManager;
import de.knewcleus.openradar.view.mouse.MouseInteractionManager;
import de.knewcleus.openradar.view.navdata.NavPointProvider;
import de.knewcleus.openradar.view.navdata.SpatialFilter;

/**
 * This class is a prototype for the component showing the radar map.
 * TODO: This class will be reworked. We have front end code and logic in one class...
 * 
 * @author Wolfram Wagner (Copied and adapted)
 */

public class RadarMapPanel extends JComponent {

    private static final long serialVersionUID = -3173711704273558768L;

    private GuiMasterController master = null;

    protected final SwingUpdateManager updateManager = new SwingUpdateManager(this);
    protected final ComponentCanvas canvas = new ComponentCanvas(this);

    private ZipFile zif=null;
    
    /* Set up the initial display range */
    protected double width = 3.0 * Units.DEG;
    protected double height = 3.0 * Units.DEG;
    protected double centerLon;
    protected double centerLat;
    protected Rectangle2D bounds;

    protected Point2D center;

    private final SwingRadarDataAdapter radarAdapter = new SwingRadarDataAdapter();

    protected IProjection projection;
    protected RadarMapViewerAdapter radarMapViewAdapter;

    public RadarMapPanel(GuiMasterController guiInteractionManager) {
        this.master = guiInteractionManager;
    
        AirportData data = guiInteractionManager.getDataRegistry();
        this.centerLon = data.getLon();
        this.centerLat = data.getLat();
        bounds = new Rectangle2D.Double(centerLon - width / 2.0, centerLat - height / 2.0, width, height);
        center = new Point2D.Double(centerLon, centerLat);
        /* Set up the projection */
        projection = new LocalSphericalProjection(center);
        radarMapViewAdapter = new RadarMapViewerAdapter(this.getCanvas(), this.getUpdateManager(), projection, center);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(getBackground());
        updateManager.paint(g2d);
    }

    @Override
    public void validate() {
        super.validate();
        updateManager.validate();
    }

    public SwingUpdateManager getUpdateManager() {
        return updateManager;
    }

    public ComponentCanvas getCanvas() {
        return canvas;
    }

    public void setup(String airportCode, GuiMasterController guiInteractionManager, SetupDialog setupDialog) throws GeodataException, IOException,
            NavDataStreamException {

        try {
            this.master = guiInteractionManager;
            
            AirportData data = guiInteractionManager.getDataRegistry();
            
            radarAdapter.registerRecipient(master.getTrackManager());
    
            radarMapViewAdapter.setLogicalScale(100.0);
    
            /* Load the nav data */
            final INavDatumFilter<INavDatum> spatialFilter = new SpatialFilter(bounds);
            final NavDatumFilterChain<INavDatum> filter = new NavDatumFilterChain<INavDatum>(Kind.CONJUNCT);
            filter.add(spatialFilter);
    
            final INavDataStream<INavPoint> airportStream;
            airportStream = new FilteredNavDataStream<INavPoint>(openXPlaneAptDat(),filter);
    
            final INavDataStream<IIntersection> fixDataStream;
            fixDataStream = new FilteredNavDataStream<IIntersection>(openXPlaneFixDat(),filter);
    
            final INavDataStream<INavPoint> navDataStream;
            navDataStream = new FilteredNavDataStream<INavPoint>(openXPlaneNavDat(),filter);
    
            /* Set up the views */
            LayeredView rootView = new LayeredView(radarMapViewAdapter);
            radarMapViewAdapter.getUpdateManager().setRootView(rootView);
    
            setupDialog.setStatus(5, "Reading landmass layer...");
            ZippedShapefileLayer landmassLayer = new ZippedShapefileLayer(data.getAirportDir(), "v0_landmass");
            final GeodataView landmassView = new GeodataView(radarMapViewAdapter, landmassLayer);
            landmassLayer.closeZipArchive();
            landmassView.setColor(Palette.LANDMASS);
            landmassView.setFill(true);
            rootView.pushView(landmassView);
    
    //       ZippedShapefileLayer lakeLayer = new ZippedShapefileLayer(data.getAirportDir(),  "v0_lake");
    //        final GeodataView lakeView = new GeodataView(radarMapViewAdapter, lakeLayer);
            //lakeLayer.closeZipArchive();
    //        lakeView.setColor(Palette.LAKE);
    //        lakeView.setFill(true);
    //        rootView.pushView(lakeView);
    //        
    //        ZippedShapefileLayer streamLayer = new ZippedShapefileLayer(data.getAirportDir(),  "v0_stream");
    //        final GeodataView streamView = new GeodataView(radarMapViewAdapter, streamLayer);
            //streamLayer.closeZipArchive();
    //        streamView.setColor(Palette.STREAM);
    //        streamView.setFill(false);
    //        rootView.pushView(streamView);
    
            setupDialog.setStatus(10, "Reading tarmac layer...");
            ZippedShapefileLayer tarmacLayer = new ZippedShapefileLayer(data.getAirportDir(), "apt_tarmac");
            final GeodataView tarmacView = new GeodataView(radarMapViewAdapter, tarmacLayer);
            tarmacLayer.closeZipArchive();
            tarmacView.setColor(Palette.TARMAC);
            tarmacView.setFill(true);
            rootView.pushView(tarmacView);
    
            setupDialog.setStatus(20, "Reading runway layer...");
            ZippedShapefileLayer runwayLayer = new ZippedShapefileLayer(data.getAirportDir(), "apt_runway");
            final GeodataView runwayView = new GeodataView(radarMapViewAdapter, runwayLayer);
            runwayLayer.closeZipArchive();
            runwayView.setColor(Palette.RUNWAY);
            runwayView.setFill(true);
            rootView.pushView(runwayView);
    
            // initialize symbol layers
            final LayeredView airportView = new LayeredView(radarMapViewAdapter);
            final NavPointProvider navPointProvider = new NavPointProvider(radarMapViewAdapter, airportView);
            navPointProvider.addNavPointListener(guiInteractionManager.getDataRegistry()); 
            
            rootView.pushView(airportView);
            setupDialog.setStatus(30, "Reading airport data...");
            navPointProvider.addViews(airportStream);
            final LayeredView navSymbolView = new LayeredView(radarMapViewAdapter);
            final NavPointProvider navPointProvider2 = new NavPointProvider(radarMapViewAdapter, navSymbolView);
            navPointProvider2.addNavPointListener(guiInteractionManager.getDataRegistry()); 
            rootView.pushView(navSymbolView);

            setupDialog.setStatus(60, "Reading navaid data...");
            navPointProvider2.addViews(navDataStream);
            
            setupDialog.setStatus(80, "Reading fixes data...");
            navPointProvider2.addViews(fixDataStream);
    
            ScaleMarkerView southMarkerView = new ScaleMarkerView(radarMapViewAdapter, Side.SOUTH, Palette.WINDOW_BLUE);
            rootView.pushView(southMarkerView);
            ScaleMarkerView westMarkerView = new ScaleMarkerView(radarMapViewAdapter, Side.WEST, Palette.WINDOW_BLUE);
            rootView.pushView(westMarkerView);
    
            LayeredView targetView = new LayeredRadarContactView(radarMapViewAdapter);
            //RadarTargetProvider radarTargetProvider = 
            new RadarTargetProvider(radarMapViewAdapter, targetView, master.getTrackManager(), guiInteractionManager);
            rootView.pushView(targetView);
    
            this.addComponentListener(new ViewerCenteringListener(radarMapViewAdapter));
            this.addMouseWheelListener(new MouseZoomListener(radarMapViewAdapter));
            final MouseInteractionManager interactionManager = new MouseInteractionManager(rootView);
            interactionManager.install(this);
            final IFocusManager focusManager = new FocusManager();
            final MouseFocusManager mouseFocusManager = new MouseFocusManager(guiInteractionManager, focusManager, rootView, radarMapViewAdapter);
            mouseFocusManager.install(this);
            this.setBackground(Palette.WATERMASS);
            setupDialog.setStatus(100, "Ready.");
        } finally {
            closeFiles();
        }
        
    }

    public void initRadarData() throws Exception {
//        AirportData data = guiInteractionManager.getDataRegistry();
//        
//        /* Install the radar data provider(s) */
//        final IPlayerRegistry<TargetStatus> playerRegistry = new FGMPRegistry();
//        // register GUI contact list, to be updated, whenever a contact appears
//        // or disappears
//        playerRegistry.registerListener(guiInteractionManager.getRadarContactManager());
//
//        final Position clientPosition = new Position(center.getX(), center.getY(), 0.0);
//        final GeodToCartTransformation geodToCartTransformation = new GeodToCartTransformation(Ellipsoid.WGS84);
//        // TODO replace MP name!
//        FGMPClient<TargetStatus> radarProvider = new FGMPClient<TargetStatus>(playerRegistry, guiInteractionManager.getCurrentATCCallSign(), geodToCartTransformation.forward(clientPosition),
//                                                               data.getMpServer(),data.getMpServerPort(), data.getMpLocalPort() );
//        radarProvider.registerRecipient(radarAdapter);
//        // register MP Chat (send & receive)
//        radarProvider.addChatListener(guiInteractionManager.getMpChatManager());
//        guiInteractionManager.getMpChatManager().setMpBackend(radarProvider);
        
        // new: 
        master.getRadarProvider().registerRecipient(radarAdapter);
        
        // register view adapter with radar backend for Zooming per buttons and
        // visibility check
        GuiRadarBackend guiRadarBackend = master.getRadarBackend();
        guiRadarBackend.setViewerAdapter(radarMapViewAdapter);
    }

    protected INavDataStream<INavPoint> openXPlaneAptDat() throws IOException {
        final File inputFile = new File("sectors/AptNav.zip");
        zif = new ZipFile(inputFile);
        Enumeration<? extends ZipEntry> entries = zif.entries();
        while(entries.hasMoreElements()) {
            ZipEntry zipentry = entries.nextElement();
            if(zipentry.getName().equals("apt.dat")) {
                return new AptDatStream(new InputStreamReader(zif.getInputStream(zipentry)));
            }
        }
        throw new IllegalStateException("apt.dat not found in sectors/AtpNav.zip!");
    }
    protected INavDataStream<IIntersection> openXPlaneFixDat() throws IOException {
        Enumeration<? extends ZipEntry> entries = zif.entries();
        while(entries.hasMoreElements()) {
            ZipEntry zipentry = entries.nextElement();
            if(zipentry.getName().equals("earth_fix.dat")) {
                return new FixDatStream(new InputStreamReader(zif.getInputStream(zipentry)));
            }
        }
        throw new IllegalStateException("apt.dat not found in sectors/AtpNav.zip!");
    }
    protected INavDataStream<INavPoint> openXPlaneNavDat() throws IOException {
        Enumeration<? extends ZipEntry> entries = zif.entries();
        while(entries.hasMoreElements()) {
            ZipEntry zipentry = entries.nextElement();
            if(zipentry.getName().equals("earth_nav.dat")) {
                return new NavDatStream(new InputStreamReader(zif.getInputStream(zipentry)));
            }
        }
        throw new IllegalStateException("apt.dat not found in sectors/AtpNav.zip!");
    }
    
    private void closeFiles() throws IOException {
        if (zif!=null) zif.close();
        
    }

    
}
