package de.knewcleus.openradar.map.test;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import javax.swing.JFrame;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.shapefile.ShapefileLayer;
import de.knewcleus.fgfs.util.IOutputIterator;
import de.knewcleus.openradar.map.IMapViewAdapter;
import de.knewcleus.openradar.map.IPickable;
import de.knewcleus.openradar.map.IProjection;
import de.knewcleus.openradar.map.IView;
import de.knewcleus.openradar.map.LocalSphericalProjection;
import de.knewcleus.openradar.map.LayeredView;
import de.knewcleus.openradar.map.MapViewer;
import de.knewcleus.openradar.map.MapViewAdapter;
import de.knewcleus.openradar.map.PickVisitor;
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
		
		LayeredView pickables=new LayeredView(mapViewAdapter);
		final Random random =  new Random();
		for (int i=0; i<100; ++i) {
			double x = (random.nextDouble() - 0.5) * 2.0 * 50.0 * Units.NM;
			double y = (random.nextDouble() - 0.5) * 2.0 * 50.0 * Units.NM;
			double w = random.nextDouble() * 5.0 * Units.NM;
			double h = random.nextDouble() * 5.0 * Units.NM;
			PickableView view = new PickableView(mapViewAdapter,
					new Rectangle2D.Double(x,y,w,h),
					Color.RED, Color.GREEN);
			pickables.pushView(view);
		}
		rootView.pushView(pickables);
		
		GridView gridView=new GridView(mapViewAdapter, 10.0*Units.KM, 10.0*Units.KM);
		rootView.pushView(gridView);

		JFrame frame=new JFrame("Map Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		MapViewer mapPanel=new MapViewer(mapViewAdapter, rootView);
		SelectionListener selectionListener = new SelectionListener(rootView);
		frame.setContentPane(mapPanel);
		mapPanel.addMouseMotionListener(selectionListener);
		
		frame.setSize(640, 480);
		
		frame.setVisible(true);
	}
	
	static class SelectionListener extends MouseAdapter {
		protected final IView rootView;
		protected PickableView currentSelection = null;
		
		public SelectionListener(IView rootView) {
			this.rootView = rootView;
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			if (currentSelection != null) {
				if (currentSelection.contains(e.getPoint())) {
					return;
				}
				currentSelection.setSelected(false);
				currentSelection = null;
			}
			PickVisitor pickVisitor = new PickVisitor(e.getPoint(),
					new IOutputIterator<IPickable>() {
				public void next(IPickable v) {
					if (v instanceof PickableView) {
						currentSelection = (PickableView)v;
					}
				}
				public boolean wantsNext() { return true; }
			});
			
			rootView.accept(pickVisitor);
			if (currentSelection != null) {
				currentSelection.setSelected(true);
			}
		}
	}

}
