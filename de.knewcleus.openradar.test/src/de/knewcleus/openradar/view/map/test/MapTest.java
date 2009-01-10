package de.knewcleus.openradar.view.map.test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.JFrame;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.IGeodataLayer;
import de.knewcleus.fgfs.geodata.shapefile.ShapefileLayer;
import de.knewcleus.fgfs.navdata.FilteredNavDataStream;
import de.knewcleus.fgfs.navdata.INavDatumFilter;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.NavDatumFilterChain;
import de.knewcleus.fgfs.navdata.NavDatumFilterChain.Kind;
import de.knewcleus.fgfs.navdata.model.IAerodrome;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavDatum;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.AptDatStream;
import de.knewcleus.openradar.radardata.SwingRadarDataAdapter;
import de.knewcleus.openradar.radardata.fgatc.FGATCEndpoint;
import de.knewcleus.openradar.rpvd.RadarMapViewerAdapter;
import de.knewcleus.openradar.rpvd.ScaleMarkerView;
import de.knewcleus.openradar.rpvd.ScaleMarkerView.Side;
import de.knewcleus.openradar.tracks.TrackManager;
import de.knewcleus.openradar.ui.Palette;
import de.knewcleus.openradar.view.BufferedCanvas;
import de.knewcleus.openradar.view.ComponentCanvas;
import de.knewcleus.openradar.view.GridView;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.Viewer;
import de.knewcleus.openradar.view.map.GeodataView;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.LocalSphericalProjection;

public class MapTest {
	public static void main(String[] args) throws GeodataException, IOException, NavDataStreamException {
		final File basedir = new File(args[0]);
		
		SwingRadarDataAdapter radarAdapter = new SwingRadarDataAdapter();
		final TrackManager trackManager = new TrackManager();
		radarAdapter.registerRecipient(trackManager);
		
		final double width = 3.0*Units.DEG;
		final double height = 3.0*Units.DEG;
		final double centerLon = -122.37489 * Units.DEG;
		final double centerLat = 37.61896 * Units.DEG;
		
		final Rectangle2D bounds = new Rectangle2D.Double(
				centerLon-width/2.0, centerLat-height/2.0,
				width, height);
		final Point2D center = new Point2D.Double(centerLon, centerLat);
		final INavDatumFilter<INavDatum> spatialFilter = new SpatialFilter(bounds);
		final INavDatumFilter<INavDatum> typeFilter = new TypeFilter<INavDatum>(IAerodrome.class);
		final NavDatumFilterChain<INavDatum> filter = new NavDatumFilterChain<INavDatum>(Kind.CONJUNCT);
		filter.add(typeFilter);
		filter.add(spatialFilter);
		
		final INavDataStream<INavPoint> airportStream;
		airportStream=new FilteredNavDataStream<INavPoint>(openXPlaneAptDat(basedir),filter);
		
		RadarMapViewerAdapter radarMapViewAdapter=new RadarMapViewerAdapter();
		IProjection projection = new LocalSphericalProjection(center);
		radarMapViewAdapter.setProjection(projection);
		radarMapViewAdapter.setLogicalScale(100.0);

		LayeredView rootView=radarMapViewAdapter.getRootView();

		final File landmassShapeFile = new File(basedir,"v0_landmass");
		final URL landmassShapeURL = landmassShapeFile.toURI().toURL();
		IGeodataLayer landmassLayer = new ShapefileLayer(landmassShapeURL, "v0_landmass");

		final File runwayShapeFile = new File(basedir,"apt_runway");
		final URL runwayShapeURL = runwayShapeFile.toURI().toURL();
		IGeodataLayer runwayLayer = new ShapefileLayer(runwayShapeURL, "apt_runway");

		final File tarmacShapeFile = new File(basedir,"apt_tarmac");
		final URL tarmacShapeURL = tarmacShapeFile.toURI().toURL();
		IGeodataLayer tarmacLayer = new ShapefileLayer(tarmacShapeURL, "apt_tarmac");

		final GeodataView landmassView = new GeodataView(radarMapViewAdapter, landmassLayer);
		landmassView.setColor(Palette.LANDMASS);
		landmassView.setFill(true);
		rootView.pushView(landmassView);
		
		final GeodataView tarmacView = new GeodataView(radarMapViewAdapter, tarmacLayer);
		tarmacView.setColor(Palette.TARMAC);
		tarmacView.setFill(true);
		rootView.pushView(tarmacView);
		
		final GeodataView runwayView = new GeodataView(radarMapViewAdapter, runwayLayer);
		runwayView.setColor(Palette.RUNWAY);
		runwayView.setFill(true);
		rootView.pushView(runwayView);
		
		final LayeredView airportView = new LayeredView(radarMapViewAdapter);
		final NavPointProvider navPointProvider=new NavPointProvider(radarMapViewAdapter, airportView);
		rootView.pushView(airportView);
		navPointProvider.addViews(airportStream);
		
		GridView gridView=new GridView(radarMapViewAdapter, 10.0*Units.NM);
		rootView.pushView(gridView);
		
		ScaleMarkerView southMarkerView = new ScaleMarkerView(radarMapViewAdapter, Side.SOUTH, Palette.WINDOW_BLUE);
		rootView.pushView(southMarkerView);
		ScaleMarkerView westMarkerView = new ScaleMarkerView(radarMapViewAdapter, Side.WEST, Palette.WINDOW_BLUE);
		rootView.pushView(westMarkerView);
		
		LayeredView targetView = new LayeredView(radarMapViewAdapter);
		RadarTargetProvider radarTargetProvider = new RadarTargetProvider(radarMapViewAdapter, targetView, trackManager);
		rootView.pushView(targetView);

		JFrame frame=new JFrame("Map Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Viewer mapPanel=new Viewer(radarMapViewAdapter);
		radarMapViewAdapter.setCanvas(new BufferedCanvas(new ComponentCanvas(mapPanel)));
		frame.setContentPane(mapPanel);
		mapPanel.setBackground(Palette.WATERMASS);
		
		frame.setSize(640, 480);
		
		frame.setVisible(true);
		
		FGATCEndpoint radarProvider = new FGATCEndpoint(16662);
		radarProvider.registerRecipient(radarAdapter);
		Thread atcNetworkThread = new Thread(radarProvider);
		atcNetworkThread.setDaemon(true);
		atcNetworkThread.start();
		Thread lossChecker = new Thread() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					trackManager.checkForLossOrRetirement();
				}
			}
		};
		lossChecker.setDaemon(true);
		lossChecker.start();
	}

	
	protected static INavDataStream<INavPoint> openXPlaneAptDat(File basedir) throws IOException {
		final File inputFile = new File(basedir, "../apt.dat.gz");
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new AptDatStream(new InputStreamReader(uncompressedStream));
	}
}
