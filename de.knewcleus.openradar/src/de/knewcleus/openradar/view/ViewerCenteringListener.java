package de.knewcleus.openradar.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ViewerCenteringListener implements ComponentListener {
	protected final IViewerAdapter viewerAdapter;
	
	public ViewerCenteringListener(IViewerAdapter viewerAdapter) {
		this.viewerAdapter = viewerAdapter;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Dimension size = e.getComponent().getSize();
		Rectangle viewerExtents = new Rectangle(size);
		viewerAdapter.setViewerExtents(viewerExtents);
		viewerAdapter.setDeviceOrigin(viewerExtents.getCenterX(), viewerExtents.getCenterY());
	}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}
}
