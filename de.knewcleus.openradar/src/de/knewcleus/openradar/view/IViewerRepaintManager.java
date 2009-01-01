package de.knewcleus.openradar.view;

import java.awt.Shape;

public interface IViewerRepaintManager {
	public void addDirtyRegion(Shape region);
	public void addDirtyView(IView view);
}
