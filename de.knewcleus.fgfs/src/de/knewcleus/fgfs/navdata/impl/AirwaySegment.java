package de.knewcleus.fgfs.navdata.impl;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment;
import de.knewcleus.fgfs.navdata.model.IIntersection;

public class AirwaySegment implements IAirwaySegment {
	protected final String identification;
	protected final Category category;
	protected final IIntersection startPoint;
	protected final IIntersection endPoint;
	protected final boolean oneWay;
	protected final float topAltitude;
	protected final float baseAltitude;
	
	public AirwaySegment(String identification, Category category,
			IIntersection startPoint, IIntersection endPoint, boolean oneWay,
			float topAltitude, float baseAltitude) {
		this.identification = identification;
		this.category = category;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.oneWay = oneWay;
		this.topAltitude = topAltitude;
		this.baseAltitude = baseAltitude;
	}

	@Override
	public String getIdentification() {
		return identification;
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public IIntersection getStartPoint() {
		return startPoint;
	}

	@Override
	public IIntersection getEndPoint() {
		return endPoint;
	}

	@Override
	public boolean isOneWay() {
		return oneWay;
	}

	@Override
	public float getTopAltitude() {
		return topAltitude;
	}

	@Override
	public float getBaseAltitude() {
		return baseAltitude;
	}
	
	@Override
	public String toString() {
		return String.format("AWY %s (%s)-(%s) %s FL%03d-FL%03d %s",
				identification,
				startPoint, endPoint,
				category.toString(),
				(int)Math.round(baseAltitude / 100.0f / Units.FT), 
				(int)Math.round(topAltitude / 100.0f / Units.FT),
				(oneWay?"oneway":"twoway")); 
	}
}
