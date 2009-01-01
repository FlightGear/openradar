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

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;

public class Viewer extends JComponent implements INotificationListener {
	private static final long serialVersionUID = -3173711704273558768L;
	
	protected final IViewerRepaintManager viewerRepaintManager;
	protected final IViewerAdapter viewAdapter;
	protected final IView rootView;

	public Viewer(IViewerAdapter mapViewAdapter, IView rootView) {
		this.viewerRepaintManager = new DeferredViewerRepaintManager(this);
		this.viewAdapter = mapViewAdapter;
		this.rootView = rootView;
		setDoubleBuffered(true);
		setBackground(Color.BLACK);
		mapViewAdapter.registerListener(this);
		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK + AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	public IViewerAdapter getViewAdapter() {
		return viewAdapter;
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
			switch (structuralNotification.getChangeType()) {
			case ADD:
				viewerRepaintManager.addDirtyView(element);
				break;
			case REMOVE:
				repaintCurrentViewBounds(element);
				break;
			}
		} else if (notification instanceof ViewNotification) {
			final ViewNotification viewNotification=(ViewNotification)notification;
			final IView source = viewNotification.getSource();
			if (viewNotification.invalidateBounds()) {
				repaintCurrentViewBounds(source);
			}
			viewerRepaintManager.addDirtyView(source);
		}
	}
	
	protected void repaintCurrentViewBounds(IView view) {
		if (view instanceof IBoundedView) {
			final Rectangle2D extents = ((IBoundedView)view).getDisplayExtents();
			viewerRepaintManager.addDirtyRegion(extents);
		} else {
			viewerRepaintManager.scheduleFullRepaint();
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