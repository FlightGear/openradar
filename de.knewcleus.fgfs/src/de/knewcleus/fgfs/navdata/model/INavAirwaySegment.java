package de.knewcleus.fgfs.navdata.model;

public interface INavAirwaySegment extends INavDatum, INavDatumWithID {
	public enum Category {
		Low, High;
	}
	
	public String getIdentification();
	public Category getCategory();
	
	public IIntersection getStartPoint();
	public IIntersection getEndPoint();
	
	public boolean isOneWay();
	
	public float getBaseAltitude();
	public float getTopAltitude();
}
