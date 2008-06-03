package de.knewcleus.openradar.ui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class RadarDesktop extends JFrame {
	private static final long serialVersionUID = 8819041738184510314L;
	
	protected final RadarWorkstation workstation;
	
	protected final JDesktopPane desktopPane=new JDesktopPane();
	
	protected final GeneralToolbox generalToolbox;
	
	protected Rectangle lastRPVDBounds;
	protected final Map<WorkstationGlobalFrame, Rectangle> globalFrameBounds=new HashMap<WorkstationGlobalFrame, Rectangle>();
	
	public RadarDesktop(GraphicsConfiguration gc, RadarWorkstation workstation) {
		super("OpenRadar", gc);
		this.workstation=workstation;
		setContentPane(desktopPane);
		
		desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		desktopPane.setPreferredSize(new Dimension(400,400));
		desktopPane.setOpaque(false);
		
		generalToolbox=new GeneralToolbox(this);
		
		generalToolbox.setVisible(true);
		add(generalToolbox, JLayeredPane.PALETTE_LAYER);
		
		pack();
	}
	
	public RadarWorkstation getWorkstation() {
		return workstation;
	}
	
	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}
	
	public GeneralToolbox getGeneralToolbox() {
		return generalToolbox;
	}
	
	public Rectangle getGlobalFrameBounds(WorkstationGlobalFrame frame) {
		return globalFrameBounds.get(frame);
	}
	
	public void setGlobalFrameBounds(WorkstationGlobalFrame frame, Rectangle bounds) {
		globalFrameBounds.put(frame, bounds);
	}
}
