package de.knewcleus.openradar.view;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class Viewer extends JComponent {
	private static final long serialVersionUID = -3173711704273558768L;
	protected final SwingUpdateManager updateManager = new SwingUpdateManager(this);
	protected final ComponentCanvas canvas = new ComponentCanvas(this);
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final Graphics2D g2d = (Graphics2D)g;
		g2d.setBackground(getBackground());
		updateManager.paint(g2d);
	}
	
	@Override
	public void validate() {
		super.validate();
		updateManager.validate();
	}
	
	public SwingUpdateManager getUpdateManager() {
		return updateManager;
	}
	
	public ComponentCanvas getCanvas() {
		return canvas;
	}
	
}