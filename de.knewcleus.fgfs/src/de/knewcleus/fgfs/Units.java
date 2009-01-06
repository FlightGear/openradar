package de.knewcleus.fgfs;

public class Units {
	/* We're working in the SI-system */
	public final static float M=1.0f;
	public final static float SEC=1.0f;
	public final static float KG=1.0f;

	/* Distances */
	public final static float FT=0.3048f*M;
	public final static float KM=1000.0f*M;
	public final static float NM=1852.0f*M;
	
	/* Time */
	public final static float MIN=60.0f*SEC;
	public final static float HOUR=60.0f*MIN;
	
	/* Frequency */
	public final static float HZ=1.0f;
	public final static float KHz=1.0E3f*HZ;
	public final static float MHz=1.0E6f*HZ;
	
	/* Angles */
	public final static float DEG=1.0f;
	public final static float RAD=180.0f*DEG/(float)Math.PI;
	public final static float FULLCIRCLE=360.0f*DEG;

	/* Velocities */
	public final static float MPS=M/SEC;
	public final static float FPM=FT/MIN;
	public final static float KMH=KM/HOUR;
	public final static float KNOTS=NM/HOUR;
	
	/* Forces */
	public final static float NEWTON=KG*M/(SEC*SEC);

	/* Constants */
	public final static float g=9.81f*NEWTON/KG;
}
