package de.knewcleus.openradar.view.map.test;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.shapefile.ShapefileLayer;
import de.knewcleus.openradar.radardata.SwingRadarDataAdapter;
import de.knewcleus.openradar.radardata.fgatc.FGATCEndpoint;
import de.knewcleus.openradar.tracks.TrackManager;
import de.knewcleus.openradar.view.BufferedCanvas;
import de.knewcleus.openradar.view.ComponentCanvas;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.Viewer;
import de.knewcleus.openradar.view.map.GeodataView;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.LocalSphericalProjection;
import de.knewcleus.openradar.view.map.MapViewerAdapter;

public class MapTest {
	public static void main(String[] args) throws GeodataException, IOException {
		SwingRadarDataAdapter radarAdapter = new SwingRadarDataAdapter();
		final TrackManager trackManager = new TrackManager();
		radarAdapter.registerRecipient(trackManager);
		
		MapViewerAdapter mapViewAdapter=new MapViewerAdapter();
		IProjection projection = new LocalSphericalProjection(new Point2D.Double(-122.37489, 37.61896));
		mapViewAdapter.setProjection(projection);
		mapViewAdapter.setLogicalScale(100.0);

		LayeredView rootView=mapViewAdapter.getRootView();

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
		
		Viewer mapPanel=new Viewer(mapViewAdapter);
		mapViewAdapter.getUpdateManager().setCanvas(new BufferedCanvas(new ComponentCanvas(mapPanel)));
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
