package de.knewcleus.radar.ui;

import java.awt.GraphicsConfiguration;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

public class RadarDesktop extends JFrame {
	private static final long serialVersionUID = 8819041738184510314L;
	
	protected final RadarWorkstation workstation;
	
	protected final JDesktopPane desktopPane;

	public RadarDesktop(GraphicsConfiguration gc, RadarWorkstation workstation) {
		super(gc);
		this.workstation=workstation;
		this.desktopPane=new JDesktopPane();
		setContentPane(desktopPane);
	}
	
	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}
}
