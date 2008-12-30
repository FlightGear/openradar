package de.knewcleus.openradar.map.test;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.MalformedURLException;
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

public class MapTest {
	public static void main(String[] args) throws MalformedURLException, GeodataException {
		IMapViewAdapter mapViewAdapter=new MapViewAdapter();
		IProjection projection = new LocalSphericalProjection(new Point2D.Double( 9.5, 47.5));
		mapViewAdapter.setProjection(projection);
		mapViewAdapter.setLogicalScale(100.0);

		LayeredView rootView=new LayeredView(mapViewAdapter);
		

		final File file = new File(args[0]);
		final URL shapeURL = file.toURI().toURL();
		ShapefileLayer shapefileLayer = new ShapefileLayer(shapeURL, args[1]);
		
		final GeodataView geodataView = new GeodataView(mapViewAdapter, shapefileLayer);
		geodataView.setColor(Color.BLUE);
		geodataView.setFill(true);
		rootView.pushView(geodataView);
		
		GridView gridView=new GridView(mapViewAdapter, 10.0*Units.KM, 10.0*Units.KM);
		rootView.pushView(gridView);

		JFrame frame=new JFrame("Map Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MapViewer mapPanel=new MapViewer(mapViewAdapter, rootView);
		frame.setContentPane(mapPanel);
		
		frame.setSize(640, 480);
		
		frame.setVisible(true);
	}

}
