package de.knewcleus.openradar.view;

import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class SwingUpdateManager extends AbstractUpdateManager {
	protected final JComponent managedComponent;

	public SwingUpdateManager(JComponent managedComponent) {
		this.managedComponent = managedComponent;
	}

	@Override
	protected void scheduleRevalidation() {
		managedComponent.revalidate();
	}

	@Override
	public void markRegionDirty(Rectangle2D bounds) {
		managedComponent.repaint(bounds.getBounds());
	}

	@Override
	public void markViewportDirty() {
		managedComponent.repaint();
	}
}
