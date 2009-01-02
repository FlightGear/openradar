package de.knewcleus.openradar.view;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class Viewer extends JComponent implements ICanvas {
	private static final long serialVersionUID = -3173711704273558768L;
	
	protected final IViewerAdapter viewAdapter;

	public Viewer(IViewerAdapter viewAdapter) {
		this.viewAdapter = viewAdapter;
		viewAdapter.getUpdateManager().setCanvas(this);
		setBackground(Color.BLACK);
		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK + AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	public IViewerAdapter getViewAdapter() {
		return viewAdapter;
	}
	
	@Override
	public Graphics2D getGraphics(Rectangle2D region) {
		return (Graphics2D)getGraphics();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final Graphics2D g2d = (Graphics2D)g;
		ViewPaintVisitor viewPaintVisitor=new ViewPaintVisitor(g2d);
		viewAdapter.getRootView().accept(viewPaintVisitor);
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
			Dimension size = getSize();
			Rectangle viewerExtents = new Rectangle(size);
			viewAdapter.setViewerExtents(viewerExtents);
		}
	}
	
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		double scale=viewAdapter.getLogicalScale();
		scale*=Math.pow(1.1, e.getWheelRotation());
		viewAdapter.setLogicalScale(scale); 
		super.processMouseWheelEvent(e);
	}
}