package de.knewcleus.openradar.ui.map;

import java.awt.Graphics2D;

import de.knewcleus.fgfs.location.IDeviceTransformation;

public interface IMapLayer {
	public abstract String getName();
	public abstract void setVisible(boolean visible);
	public abstract boolean isVisible();
	
	public abstract void draw(Graphics2D g2d, IDeviceTransformation transform);
}