package de.knewcleus.openradar.ui.rpvd;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;

import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.util.GeometryConversionException;
import de.knewcleus.openradar.ui.RadarWorkstation;
import de.knewcleus.openradar.ui.WorkstationGlobalFrame;
import de.knewcleus.openradar.ui.rpvd.toolbox.RadarToolbox;

public class RadarPlanViewDisplay extends WorkstationGlobalFrame {
	private static final long serialVersionUID = 5923481231980915972L;
	
	protected final JLayeredPane layeredPane=new JLayeredPane();
	protected final JDesktopPane subDesktopPane=new JDesktopPane();
	protected final RadarPlanViewPanel radarMapPanel;
	protected final RadarToolbox radarToolbox;

	protected final Integer RPVD_LAYER=0;
	protected final Integer DESKTOP_LAYER=1;
	
	public RadarPlanViewDisplay(RadarWorkstation workstation, ICoordinateTransformation mapTransformation) throws GeometryConversionException {
		super(workstation,"RPVD","Radar Plan View Display",true,false,true,false);
		
		radarMapPanel=new RadarPlanViewPanel(workstation, mapTransformation);

		layeredPane.setLayout(new OverlayLayout());
		
		layeredPane.add(radarMapPanel,RPVD_LAYER);
		layeredPane.add(subDesktopPane,DESKTOP_LAYER);
		setContentPane(layeredPane);
		
		radarToolbox=new RadarToolbox(this);
		
		setPreferredSize(new Dimension(400,400));
		
		subDesktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		subDesktopPane.setOpaque(false);
		radarToolbox.setVisible(true);
		subDesktopPane.add(radarToolbox);
		
		try {
			radarToolbox.setIcon(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pack();
	}
	
	public JDesktopPane getSubDesktopPane() {
		return subDesktopPane;
	}
	
	public RadarPlanViewPanel getRadarMapPanel() {
		return radarMapPanel;
	}
	
	class OverlayLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {
		}
		
		@Override
		public void removeLayoutComponent(Component comp) {
		}
		
		@Override
		public void layoutContainer(Container parent) {
			Dimension size=parent.getSize();
			
			for (Component c: parent.getComponents()) {
				c.setBounds(0, 0, size.width, size.height);
			}
		}
		
		@Override
		public Dimension minimumLayoutSize(Container parent) {
			int maxWidth=0,maxHeight=0;
			
			for (Component c: parent.getComponents()) {
				Dimension minSize=c.getMinimumSize();
				maxWidth=Math.max(maxWidth, minSize.width);
				maxHeight=Math.max(maxHeight, minSize.height);
			}
			return new Dimension(maxWidth,maxHeight);
		}
		
		@Override
		public Dimension preferredLayoutSize(Container parent) {
			int maxWidth=0,maxHeight=0;
			
			for (Component c: parent.getComponents()) {
				Dimension minSize=c.getPreferredSize();
				maxWidth=Math.max(maxWidth, minSize.width);
				maxHeight=Math.max(maxHeight, minSize.height);
			}
			return new Dimension(maxWidth,maxHeight);
		}
	};
}
