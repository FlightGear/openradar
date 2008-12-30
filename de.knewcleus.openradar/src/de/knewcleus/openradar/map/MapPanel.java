package de.knewcleus.openradar.map;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;

public class MapPanel extends JComponent implements INotificationListener {
	private static final long serialVersionUID = -3173711704273558768L;
	
	protected final IMapViewAdapter mapViewAdapter;
	protected final IView rootView;

	public MapPanel(IMapViewAdapter mapViewAdapter, IView rootView) {
		this.mapViewAdapter = mapViewAdapter;
		this.rootView = rootView;
		setDoubleBuffered(true);
		setBackground(Color.BLACK);
		mapViewAdapter.registerListener(this);
		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK);
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
			final Dimension dimension=getSize();
			g2d.clipRect(0, 0, dimension.width, dimension.height);
		}
		ViewPaintVisitor viewPaintVisitor=new ViewPaintVisitor(g2d);
		rootView.accept(viewPaintVisitor);
	}
	
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		double scale=mapViewAdapter.getLogicalScale();
		scale*=Math.pow(1.1, e.getWheelRotation());
		mapViewAdapter.setLogicalScale(scale); 
		super.processMouseWheelEvent(e);
	}
}