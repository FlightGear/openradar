package de.knewcleus.radar.autolabel;

public interface Label extends DisplayObject {
	public LabeledObject getAssociatedObject();
	public void setCentroidPosition(double x, double y);
	public boolean isAutolabelled();
}
