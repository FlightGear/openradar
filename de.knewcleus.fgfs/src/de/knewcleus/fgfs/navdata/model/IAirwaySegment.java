package de.knewcleus.fgfs.navdata.model;

public interface IAirwaySegment extends INavDatum, INavDatumWithID {
	public enum Category {
		Low, High;
	}
	
	public Category getCategory();
	
	public IIntersection getStartPoint();
	public IIntersection getEndPoint();
	
	public boolean isOneWay();
	
	public float getBaseAltitude();
	public float getTopAltitude();
}
