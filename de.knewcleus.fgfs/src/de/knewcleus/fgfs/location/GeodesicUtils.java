package de.knewcleus.fgfs.location;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import de.knewcleus.fgfs.Units;

/**
 * Utilities for geodesic calculations.
 * 
 * Based on
 * 
 *     T. Vicenty, Direct and Inverse Solutions of Geodesics on the
 *                 Ellipsoid with Application of Nested Equations,
 *                 Survey Review Vol. XXIII, No. 176,
 *                 Ministry of Overseas Department, UK, April 1975
 *  
 * @author Ralf Gerlich
 *
 */
public class GeodesicUtils {
	protected final Ellipsoid ellipsoid;
	
	public static class GeodesicInformation {
		public final double startLon;
		public final double startLat;
		public final double startAzimuth;
		public final double endLon;
		public final double endLat;
		public final double endAzimuth;
		public final double length;
		
		public GeodesicInformation(double startLon, double startLat, double startAzimuth,
								   double endLon, double endLat, double endAzimuth,
								   double length)
		{
			this.startLon=startLon;
			this.startLat=startLat;
			this.startAzimuth=startAzimuth;
			this.endLon=endLon;
			this.endLat=endLat;
			this.endAzimuth=endAzimuth;
			this.length=length;
		}
		
		public Position getStartPos() {
			return new Position(startLon,startLat,0);
		}
		
		public Position getEndPos() {
			return new Position(endLon,endLat,0);
		}
		
		public double getStartLon() {
			return startLon;
		}

		public double getStartLat() {
			return startLat;
		}

		public double getEndLon() {
			return endLon;
		}

		public double getEndLat() {
			return endLat;
		}

		public double getStartAzimuth() {
			return startAzimuth;
		}
		
		public double getEndAzimuth() {
			return endAzimuth;
		}
		
		public double getLength() {
			return length;
		}
	}
	
	public GeodesicUtils(Ellipsoid ellipsoid) {
		this.ellipsoid=ellipsoid;
	}
	
	public GeodesicInformation forward(double startLon, double startLat, double startAzimuth, double length) {
		if (length<1E-2*Units.M) {
			/* distance less than a centimeter => congruence */
			double endAzimuth=startAzimuth+180.0*Units.DEG;
			if (endAzimuth>360.0*Units.DEG) {
				endAzimuth-=360.0*Units.DEG;
			}
			return new GeodesicInformation(startLon,startLat,startAzimuth,startLon,startLat,endAzimuth,length);
		}
		final double phi1=startLat/Units.RAD;
		
		final double a=ellipsoid.getA();
		if (abs(cos(phi1))<1E-5) {
			/* Polar origin, calculate from the equator instead */
			final double dM=a*ellipsoid.getM0()-length;
			final double az=(startLat<0?180.0*Units.DEG:0);
			return forward(startLon,0, az, dM);
		}
		
		final double b=ellipsoid.getB();
		final double f=ellipsoid.getF();
		final double az1=startAzimuth/Units.RAD;
		final double cosaz1=cos(az1);
		final double sinaz1=sin(az1);
		final double tanU1=(1-f)*tan(phi1); // SPECIAL CASE: phi=+/-pi/2
		final double cosU1=1.0/(sqrt(1.0+tanU1*tanU1));
		final double sinU1=tanU1*cosU1;
		final double sigma1=atan2(tanU1,cosaz1);
		final double sinaz=cosU1*sin(az1);
		final double cosaz2=1-sinaz*sinaz;
		final double u=sqrt(cosaz2*(a*a/(b*b)-1));
		final double A=1+u*u*(4096+u*u*(-768+u*u*(320-175*u*u)))/16384;
		final double B=u*u*(256+u*u*(-128+u*u*(74-47*u*u)))/1024;
		
		double deltaSigma=0;
		double sigma;
		double sinsigma;
		double cossigmam2;
		
		do {
			sigma=length/(b*A)+deltaSigma;
			final double sigmam2=2.0*sigma1+sigma;
			cossigmam2=cos(sigmam2);
			sinsigma=sin(sigma);
			deltaSigma=B*sinsigma*(cossigmam2+B*(cos(sigma)*(-1+2*cossigmam2*cossigmam2)-B*cossigmam2*(-3+4*sinsigma*sinsigma)*(-3+4*cossigmam2*cossigmam2)/6)/4);
		} while (abs(deltaSigma)>1E-10);
		
		final double cossigma=cos(sigma);
		final double temp=sinU1*sinsigma+cosU1*cossigma*cosaz1;
		final double phi2=atan2(sinU1*cossigma+cosU1*sinsigma*cosaz1,(1-f)*sqrt(sinaz*sinaz+temp*temp));
		final double lambda=atan2(sinsigma*sinaz1,cosU1*cossigma-sinU1*sinsigma*cosaz1);
		final double C=f*cosaz2*(4+f*(4-3*cosaz2))/16;
		final double L=lambda-(1-C)*f*sinaz*(sigma+C*sinsigma*(cossigmam2+C*cossigma*(-12*cossigmam2*cossigmam2)));
		final double alpha2=atan2(sinaz,-temp);
		
		double endLon=startLon+L*Units.RAD;
		double endLat=phi2*Units.RAD;
		double endAzimuth=alpha2*Units.RAD;
		if (endAzimuth<0) {
			endAzimuth+=360.0*Units.DEG;
		}
		return new GeodesicInformation(startLon,startLat,startAzimuth,endLon,endLat,endAzimuth,length);
	}
}
