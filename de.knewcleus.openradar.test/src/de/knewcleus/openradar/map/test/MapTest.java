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
import de.knewcleus.openradar.map.IProjection;
import de.knewcleus.openradar.map.LocalSphericalProjection;
import de.knewcleus.openradar.map.Map;
import de.knewcleus.openradar.map.MapPanel;
import de.knewcleus.openradar.map.view.GeodataView;

public class MapTest {
	public static void main(String[] args) throws MalformedURLException, GeodataException {
		JFrame frame=new JFrame("Map Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Map map=new Map();
		IProjection projection = new LocalSphericalProjection(new Point2D.Double( 8, 48));
		map.setProjection(projection);
		map.setLogicalScale(100.0);
		
		final File file = new File(args[0]);
		final URL shapeURL = file.toURI().toURL();
		ShapefileLayer shapefileLayer = new ShapefileLayer(shapeURL, args[1]);
		
		final GeodataView geodataView = new GeodataView(map, args[1], shapefileLayer);
		geodataView.setColor(Color.BLUE);
		geodataView.setFill(false);
		map.pushLayer(geodataView);
		
		GridView gridView=new GridView(map, 10.0*Units.KM, 10.0*Units.KM);
		map.pushLayer(gridView);

		MapPanel mapPanel=new MapPanel(map);
		
		frame.setContentPane(mapPanel);
		
		frame.setSize(640, 480);
		
		frame.setVisible(true);
	}

}
