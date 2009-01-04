package de.knewcleus.openradar.view;

import de.knewcleus.openradar.notify.INotification;

public class CanvasChangeNotification implements INotification {
	protected final IViewerAdapter source;
	
	public CanvasChangeNotification(IViewerAdapter source) {
		this.source = source;
	}
	
	@Override
	public IViewerAdapter getSource() {
		return source;
	}

}
