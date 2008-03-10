package de.knewcleus.fgfs.environment;

public interface AtmosphericConditions {
	public abstract double getGeometricHeight();
	
	public abstract double getGeopotentialHeight();

	public abstract int getIdx();

	public abstract double getTemperature();

	public abstract double getPressure();

	public abstract double getDensity();

	public abstract double getViscosity();

}