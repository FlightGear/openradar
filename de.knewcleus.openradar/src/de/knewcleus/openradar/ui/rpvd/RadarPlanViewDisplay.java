package de.knewcleus.openradar.ui.rpvd;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.fgfs.util.GeometryConversionException;
import de.knewcleus.openradar.ui.RadarWorkstation;
import de.knewcleus.openradar.ui.WorkstationGlobalFrame;
import de.knewcleus.openradar.ui.map.RadarMapPanel;
import de.knewcleus.openradar.ui.rpvd.toolbox.RadarToolbox;

public class RadarPlanViewDisplay extends WorkstationGlobalFrame implements ChangeListener, PropertyChangeListener, ComponentListener {
	private static final long serialVersionUID = 5923481231980915972L;
	
	protected final JLayeredPane layeredPane=new JLayeredPane();
	protected final JDesktopPane subDesktopPane=new JDesktopPane();
	protected final RadarMapPanel radarMapPanel;
	protected final RadarToolbox radarToolbox;

	protected final Integer RPVD_LAYER=0;
	protected final Integer DESKTOP_LAYER=1;
	
	public RadarPlanViewDisplay(RadarWorkstation workstation, IMapProjection mapTransformation) throws GeometryConversionException {
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
		
		workstation.getRadarPlanViewSettings().addPropertyChangeListener(this);
		radarMapPanel.addComponentListener(this);
		
		pack();
	}
	
	public JDesktopPane getSubDesktopPane() {
		return subDesktopPane;
	}
	
	public RadarMapPanel getRadarMapPanel() {
		return radarMapPanel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource()==radarMapPanel.getSettings() &&
				evt.getPropertyName().equals(RadarPlanViewSettings.RANGE_PROPERTY)) {
			updateScale();
		}
	}
	
	protected void updateScale() {
		final RadarPlanViewSettings settings=radarMapPanel.getSettings();

		final double xRange=settings.getRange()*Units.NM;
		final int viewWidth=radarMapPanel.getVisibleRect().width;
		radarMapPanel.setScale(viewWidth/xRange);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		updateScale();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		updateScale();
	}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}
	
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
