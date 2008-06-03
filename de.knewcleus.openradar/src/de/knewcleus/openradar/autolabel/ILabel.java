package de.knewcleus.openradar.autolabel;

public interface ILabel extends DisplayObject {
	public DisplayObject getLabeledObject();
	public void setCentroidPosition(double x, double y);
	public boolean isAutolabelled();
}
