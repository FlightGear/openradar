package de.knewcleus.fgfs;

public class Units {
	/* We're working in the SI-system */
	public final static double M=1.0;
	public final static double SEC=1.0;
	public final static double KG=1.0;

	/* Distances */
	public final static double FT=0.3048*M;
	public final static double KM=1000.0*M;
	public final static double NM=1852.0*M;
	
	/* Time */
	public final static double MIN=60.0*SEC;
	public final static double HOUR=60.0*MIN;
	
	/* Frequency */
	public final static double HZ=1.0;
	public final static double KHZ=1.0E3*HZ;
	public final static double MHZ=1.0E6*HZ;
	
	/* Angles */
	public final static double DEG=1.0;
	public final static double RAD=180.0/Math.PI*DEG;
	public final static double FULLCIRCLE=360.0*DEG;

	/* Velocities */
	public final static double MPS=M/SEC;
	public final static double FPM=FT/MIN;
	public final static double KMH=KM/HOUR;
	public final static double KNOTS=NM/HOUR;
	
	/* Forces */
	public final static double NEWTON=KG*M/(SEC*SEC);

	/* Constants */
	public final static double g=9.81*NEWTON/KG;
}
