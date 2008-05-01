package de.knewcleus.radar.autolabel;

public interface ILabel extends DisplayObject {
	public DisplayObject getLabeledObject();
	public void setCentroidPosition(double x, double y);
	public boolean isAutolabelled();
}
