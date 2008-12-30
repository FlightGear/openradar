package de.knewcleus.openradar.map.test;

import javax.swing.JFrame;

import de.knewcleus.openradar.map.Map;
import de.knewcleus.openradar.map.MapPanel;

public class MapTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame=new JFrame("Map Test");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Map map=new Map();
		GridView gridView=new GridView(map, 50.0, 50.0);
		map.pushLayer(gridView);
		MapPanel mapPanel=new MapPanel(map);
		
		frame.setContentPane(mapPanel);
		
		frame.setSize(640, 480);
		
		frame.setVisible(true);
	}

}
