package de.knewcleus.openradar.map.test;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.shapefile.ShapefileLayer;
import de.knewcleus.openradar.map.IMapViewAdapter;
import de.knewcleus.openradar.map.IProjection;
import de.knewcleus.openradar.map.LocalSphericalProjection;
import de.knewcleus.openradar.map.LayeredView;
import de.knewcleus.openradar.map.MapViewer;
import de.knewcleus.openradar.map.MapViewAdapter;
import de.knewcleus.openradar.map.view.GeodataView;
import de.knewcleus.openradar.radardata.SwingRadarDataAdapter;
import de.knewcleus.openradar.radardata.fgatc.FGATCEndpoint;
import de.knewcleus.openradar.tracks.TrackManager;

public class MapTest {
	public static void main(String[] args) throws GeodataException, IOException {
		SwingRadarDataAdapter radarAdapter = new SwingRadarDataAdapter();
		final TrackManager trackManager = new TrackManager();
		radarAdapter.registerRecipient(trackManager);
		
		IMapViewAdapter mapViewAdapter=new MapViewAdapter();
		IProjection projection = new LocalSphericalProjection(new Point2D.Double(-121.5, 38));
		mapViewAdapter.setProjection(projection);
		mapViewAdapter.setLogicalScale(100.0);

		LayeredView rootView=new LayeredView(mapViewAdapter);

		final File file = new File(args[0]);
		final URL shapeURL = file.toURI().toURL();
		ShapefileLayer shapefileLayer = new ShapefileLayer(shapeURL, args[1]);
		
		final GeodataView geodataView = new GeodataView(mapViewAdapter, shapefileLayer);
		geodataView.setColor(Color.GREEN);
		geodataView.setFill(true);
		rootView.pushView(geodataView);
		
		GridView gridView=new GridView(mapViewAdapter, 10.0*Units.KM, 10.0*Units.KM);
		rootView.pushView(gridView);
		
		LayeredView targetView = new LayeredView(mapViewAdapter);
		RadarTargetProvider radarTargetProvider = new RadarTargetProvider(mapViewAdapter, targetView, trackManager);
		rootView.pushView(targetView);

		JFrame frame=new JFrame("Map Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MapViewer mapPanel=new MapViewer(mapViewAdapter, rootView);
		frame.setContentPane(mapPanel);
		
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
}
