package de.knewcleus.radar.ui.rpvd;

import java.awt.Graphics2D;

import de.knewcleus.fgfs.location.IDeviceTransformation;

public interface IMapLayer {

	public abstract void draw(Graphics2D g2d, IDeviceTransformation transform);
}