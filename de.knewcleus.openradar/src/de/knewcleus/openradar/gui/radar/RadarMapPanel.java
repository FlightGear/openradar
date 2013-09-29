/**
 * Copyright (C) 2012,2013 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

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
import de.knewcleus.fgfs.navdata.xplane.AptDatStream1000;
import de.knewcleus.fgfs.navdata.xplane.FixDatStream;
import de.knewcleus.fgfs.navdata.xplane.NavDatStream;
import de.knewcleus.fgfs.util.IOutputIterator;
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
import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.LayeredRadarContactView;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.MouseZoomListener;
import de.knewcleus.openradar.view.SwingUpdateManager;
import de.knewcleus.openradar.view.ViewerCenteringListener;
import de.knewcleus.openradar.view.glasspane.StPView;
import de.knewcleus.openradar.view.groundnet.AtcObjectsView;
import de.knewcleus.openradar.view.groundnet.GroundnetReader;
import de.knewcleus.openradar.view.groundnet.GroundnetView;
import de.knewcleus.openradar.view.groundnet.TaxiSign;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.GeodataView;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.LocalSphericalProjection;
import de.knewcleus.openradar.view.mouse.FocusManager;
import de.knewcleus.openradar.view.mouse.IFocusManager;
import de.knewcleus.openradar.view.mouse.ITooltipView;
import de.knewcleus.openradar.view.mouse.MouseFocusManager;
import de.knewcleus.openradar.view.mouse.MouseInteractionManager;
import de.knewcleus.openradar.view.navdata.NavPointProvider;
import de.knewcleus.openradar.view.navdata.SpatialFilter;
import de.knewcleus.openradar.view.stdroutes.StdRoute;
import de.knewcleus.openradar.view.stdroutes.StdRouteReader;
import de.knewcleus.openradar.view.stdroutes.StdRouteView;

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
    protected double width = 6.0 * Units.DEG;
    protected double height = 6.0 * Units.DEG;
    protected double centerLon;
    protected double centerLat;
    protected Rectangle2D bounds;

    protected Point2D center;

    private final SwingRadarDataAdapter radarAdapter = new SwingRadarDataAdapter();


    private volatile LayeredView rootView;
    protected IProjection projection;
    protected RadarMapViewerAdapter radarMapViewAdapter;
    protected LayeredView routeView;
    // this view will contain the Fixes defined inline in the standard routes
    protected LayeredView addNavSymbolView;

    public RadarMapPanel(GuiMasterController guiInteractionManager) {
        this.master = guiInteractionManager;

        AirportData data = guiInteractionManager.getDataRegistry();
        this.centerLon = data.getLon();
        this.centerLat = data.getLat();
        bounds = new Rectangle2D.Double(centerLon - width / 2.0d, centerLat - height / 2.0d, width, height);
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
            this.setBackground(Palette.WATERMASS);

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
            rootView = new LayeredView(radarMapViewAdapter);
            radarMapViewAdapter.getUpdateManager().setRootView(rootView);

            if(master.getDataRegistry().isLayerVisible("landmass")) {
                try {
                    setupDialog.setStatus(5, "Reading landmass layer...");
                    ZippedShapefileLayer landmassLayer = new ZippedShapefileLayer(data.getAirportDir(), "v0_landmass");
                    final GeodataView landmassView = new GeodataView(master, radarMapViewAdapter, landmassLayer,"LANDMASS",bounds);
                    landmassLayer.closeZipArchive();
                    landmassView.setColor(Palette.LANDMASS);
                    //landmassView.setFill(false);
                    rootView.pushView(landmassView);
                } catch(Exception e) {
                    // ignore layer
                    System.err.println("Could not landmass layer data. Hiding it...");
                    // set better background color
                    setBackground(Palette.LANDMASS);
                }
            } else {
                // set better background color
                setBackground(Palette.LANDMASS);
            }

            if(master.getDataRegistry().isLayerVisible("urban")) {
                try {
                    setupDialog.setStatus(5, "Reading urban layer...");
                    ZippedShapefileLayer urbanLayer = new ZippedShapefileLayer(data.getAirportDir(), "v0_urban");
                    final GeodataView urbanView = new GeodataView(master, radarMapViewAdapter, urbanLayer,"URBAN",bounds);
                    urbanLayer.closeZipArchive();
                    urbanView.setColor(new Color(80,80,80));
                    //landmassView.setFill(false);
                    rootView.pushView(urbanView);
                } catch(Exception e) {
                    // ignore layer
                    System.err.println("Could not load urban layer data. Hiding it...");
                }
            }
            if(master.getDataRegistry().isLayerVisible("lake")) {
                try {
                    ZippedShapefileLayer lakeLayer = new ZippedShapefileLayer(data.getAirportDir(),  "v0_lake");
                    final GeodataView lakeView = new GeodataView(master, radarMapViewAdapter, lakeLayer,"LAKE",bounds);
                    lakeLayer.closeZipArchive();
                    lakeView.setColor(Palette.LAKE);
                    lakeView.setFill(true);
                    rootView.pushView(lakeView);
                } catch(Exception e) {
                    // ignore layer
                    System.err.println("Could not load lake layer data. Hiding it...");
                }
            }
            if(master.getDataRegistry().isLayerVisible("stream")) {
                try {
                    ZippedShapefileLayer streamLayer = new ZippedShapefileLayer(data.getAirportDir(),  "v0_stream");
                    final GeodataView streamView = new GeodataView(master, radarMapViewAdapter, streamLayer,"STREAM", bounds);
                    streamLayer.closeZipArchive();
                    streamView.setColor(Palette.STREAM);
                    streamView.setFill(false);
                    rootView.pushView(streamView);
                } catch(Exception e) {
                    // ignore layer
                    System.err.println("Could not load stream layer data. Hiding it...");
                }
            }
            if(master.getDataRegistry().isLayerVisible("tarmac")) {
                setupDialog.setStatus(10, "Reading tarmac layer...");
                ZippedShapefileLayer tarmacLayer = new ZippedShapefileLayer(data.getAirportDir(), "apt_tarmac");
                final GeodataView tarmacView = new GeodataView(master, radarMapViewAdapter, tarmacLayer,"TARMAC", bounds);
                tarmacLayer.closeZipArchive();
                tarmacView.setColor(Palette.TARMAC);
                tarmacView.setFill(true);
                rootView.pushView(tarmacView);
            }

            setupDialog.setStatus(20, "Reading runway layer...");
            ZippedShapefileLayer runwayLayer = new ZippedShapefileLayer(data.getAirportDir(), "apt_runway");
            final GeodataView runwayView = new GeodataView(master, radarMapViewAdapter, runwayLayer, null, bounds);
            runwayLayer.closeZipArchive();
            runwayView.setColor(Palette.RUNWAY);
            runwayView.setFill(true);
            rootView.pushView(runwayView);

            if(master.getDataRegistry().isLayerVisible("groundnet")) {
                final LayeredView layeredGroundnetView = new LayeredView(radarMapViewAdapter);
                GroundnetReader groundnetReader = new GroundnetReader(data.getAirportCode());
                for(TaxiWaySegment seg : groundnetReader.getTaxiWaySegments()) {
                    layeredGroundnetView.pushView(new GroundnetView(radarMapViewAdapter,seg,data));
                }
                for(TaxiSign sign : groundnetReader.getTaxiSigns()) {
                    layeredGroundnetView.pushView(new GroundnetView(radarMapViewAdapter,sign,data));
                }
                rootView.pushView(layeredGroundnetView);
            }
            // initialize symbol layers
            final LayeredView airportView = new LayeredView(radarMapViewAdapter);
            final NavPointProvider navPointProvider = new NavPointProvider(radarMapViewAdapter, airportView,data);
            navPointProvider.addNavPointListener(guiInteractionManager.getDataRegistry());

            rootView.pushView(airportView);
            setupDialog.setStatus(30, "Reading airport data...");
            navPointProvider.addViews(airportStream);

            routeView = new LayeredView(radarMapViewAdapter);
            rootView.pushView(routeView);

            setupDialog.setStatus(60, "Reading navaid data...");
            final LayeredView navSymbolView = new LayeredView(radarMapViewAdapter);
            final NavPointProvider navPointProvider2 = new NavPointProvider(radarMapViewAdapter, navSymbolView, data);
            navPointProvider2.addNavPointListener(guiInteractionManager.getDataRegistry());
            rootView.pushView(navSymbolView);

            navPointProvider2.addViews(navDataStream);

            setupDialog.setStatus(80, "Reading fixes data...");
            navPointProvider2.addViews(fixDataStream);

            // this view will contain the Fixes defined inline in the standard routes
            addNavSymbolView = new LayeredView(radarMapViewAdapter);
            rootView.pushView(addNavSymbolView);

            // read here to have navaid data available
            readStandardRouteData(guiInteractionManager);


            final LayeredView layeredAtcObjectsView = new LayeredView(radarMapViewAdapter);
            layeredAtcObjectsView.pushView(new AtcObjectsView(radarMapViewAdapter,data));
            rootView.pushView(layeredAtcObjectsView);


            ScaleMarkerView southMarkerView = new ScaleMarkerView(radarMapViewAdapter, Side.SOUTH, Palette.WINDOW_BLUE);
            rootView.pushView(southMarkerView);
            ScaleMarkerView westMarkerView = new ScaleMarkerView(radarMapViewAdapter, Side.WEST, Palette.WINDOW_BLUE);
            rootView.pushView(westMarkerView);

            LayeredView targetView = new LayeredRadarContactView(radarMapViewAdapter);
            //RadarTargetProvider radarTargetProvider =
            new RadarTargetProvider(radarMapViewAdapter, targetView, master.getTrackManager(), guiInteractionManager);
            rootView.pushView(targetView);

            LayeredView glassPaneView = new LayeredView(radarMapViewAdapter);
            glassPaneView.pushView(new StPView(radarMapViewAdapter, guiInteractionManager));
            rootView.pushView(glassPaneView);

            this.addComponentListener(new ViewerCenteringListener(radarMapViewAdapter));
            this.addMouseWheelListener(new MouseZoomListener(radarMapViewAdapter));
            final MouseInteractionManager interactionManager = new MouseInteractionManager(rootView);
            interactionManager.install(this);
            final IFocusManager focusManager = new FocusManager();
            final MouseFocusManager mouseFocusManager = new MouseFocusManager(guiInteractionManager, focusManager, rootView, radarMapViewAdapter);
            mouseFocusManager.install(this);

            // parsing of airport data delivered tower position, move center
            this.centerLon = data.getLon();
            this.centerLat = data.getLat();
            center = new Point2D.Double(centerLon, centerLat);
            this.radarMapViewAdapter.setCenter(center);

            // check which airports in range have a metar
            master.getMetarReader().retrieveWeatherStations(master.getDataRegistry().getNavaidDB().getAerodromes());

            setupDialog.setStatus(100, "Ready.");

            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.registerComponent(this);

        } finally {
            closeFiles();
        }

    }

    public void initRadarData() throws Exception {
        master.getRadarProvider().registerRecipient(radarAdapter);

        // register view adapter with radar backend for Zooming per buttons and
        // visibility check
        GuiRadarBackend guiRadarBackend = master.getRadarBackend();
        guiRadarBackend.setViewerAdapter(radarMapViewAdapter);
    }

    protected INavDataStream<INavPoint> openXPlaneAptDat() throws IOException {
        final File inputFile = new File("data/AptNav.zip");
        zif = new ZipFile(inputFile);
        Enumeration<? extends ZipEntry> entries = zif.entries();
        while(entries.hasMoreElements()) {
            ZipEntry zipentry = entries.nextElement();
            if(zipentry.getName().equals("apt.dat")) {
                return new AptDatStream1000(new InputStreamReader(zif.getInputStream(zipentry)));
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

    public void reReadStandardRoutes(GuiMasterController master) {
        routeView.clear();
        addNavSymbolView.clear();
        readStandardRouteData(master);
    }

    private void readStandardRouteData(GuiMasterController master) {
        StdRouteReader reader = new StdRouteReader(master.getDataRegistry(), radarMapViewAdapter);
        for(StdRoute route : reader.getStdRoutes()) {
            routeView.pushView(new StdRouteView(radarMapViewAdapter, route, master));
        }
        final NavPointProvider navPointProvider = new NavPointProvider(radarMapViewAdapter, addNavSymbolView, master.getDataRegistry());
        navPointProvider.addViews(master.getDataRegistry().getNavaidDB().getManualNavpoints());
    }

    @Override
    public String getToolTipText() {
        Point p = getMousePosition();
        if(p==null) return null;
        String text = rootView.getTooltipText(p);
        return text;
    }

    protected class TooltipPickIterator implements IOutputIterator<IPickable> {

        protected ITooltipView topTooltipView = null;

        @Override
        public void next(IPickable v) {
            if (v instanceof ITooltipView) {
                topTooltipView = (ITooltipView)v;
            }
        }

        @Override
        public boolean wantsNext() {
            return true;
        }

        public ITooltipView getTopFocusable() {
            return topTooltipView;
        }
    }
}
