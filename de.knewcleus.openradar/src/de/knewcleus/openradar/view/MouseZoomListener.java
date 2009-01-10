package de.knewcleus.openradar.view;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseZoomListener implements MouseWheelListener {
	protected final IViewerAdapter viewerAdapter;
	
	public MouseZoomListener(IViewerAdapter viewerAdapter) {
		this.viewerAdapter = viewerAdapter;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double scale=viewerAdapter.getLogicalScale();
		scale*=Math.pow(1.1, e.getWheelRotation());
		viewerAdapter.setLogicalScale(scale); 
	}
}
