package de.knewcleus.openradar.map;

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

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;

public class MapViewer extends JComponent implements INotificationListener {
	private static final long serialVersionUID = -3173711704273558768L;
	
	protected final IMapViewAdapter mapViewAdapter;
	protected final IView rootView;

	public MapViewer(IMapViewAdapter mapViewAdapter, IView rootView) {
		this.mapViewAdapter = mapViewAdapter;
		this.rootView = rootView;
		setDoubleBuffered(true);
		setBackground(Color.BLACK);
		mapViewAdapter.registerListener(this);
		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK + AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	public IMapViewAdapter getMapViewAdapter() {
		return mapViewAdapter;
	}
	
	public IView getRootView() {
		return rootView;
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof StructuralNotification) {
			final StructuralNotification structuralNotification;
			structuralNotification=(StructuralNotification)notification;
			final IView element = structuralNotification.getElement();
			
			/* Ensure that the view respectively its extents are redrawn */
			repaintView(element);
		} else if (notification instanceof ViewNotification) {
			/* Initiate a repaint of the concerned region */
			final IView source = ((ViewNotification)notification).getSource();
			repaintView(source);
		}
	}
	
	protected void repaintView(IView view) {
		if (view instanceof IBoundedView) {
			final Rectangle2D extents = ((IBoundedView)view).getDisplayExtents();
			repaint(extents.getBounds());
		} else {
			repaint();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final Graphics2D g2d = (Graphics2D)g;
		if (g2d.getClip()==null) {
			final Dimension size=getSize();
			g2d.clipRect(0, 0, size.width, size.height);
		}
		ViewPaintVisitor viewPaintVisitor=new ViewPaintVisitor(g2d);
		rootView.accept(viewPaintVisitor);
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
			Dimension size = getSize();
			Rectangle viewerExtents = new Rectangle(size);
			mapViewAdapter.setViewerExtents(viewerExtents);
		}
	}
	
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		double scale=mapViewAdapter.getLogicalScale();
		scale*=Math.pow(1.1, e.getWheelRotation());
		mapViewAdapter.setLogicalScale(scale); 
		super.processMouseWheelEvent(e);
	}
}