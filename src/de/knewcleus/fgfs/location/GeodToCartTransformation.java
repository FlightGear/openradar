/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.location;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class GeodToCartTransformation implements ICoordinateTransformation {
	protected final Ellipsoid ellipsoid;
	
	public GeodToCartTransformation(Ellipsoid ellipsoid) {
		this.ellipsoid=ellipsoid;
	}
	
	public Position forward(Position pos) {
		double lambda=toRadians(pos.getX());
		double phi=toRadians(pos.getY());
		double h=pos.getZ();
		double a=ellipsoid.getA();
		double e2=ellipsoid.getEsquared();
		
		double sphi=sin(phi);
		double n=a/sqrt(1-e2*sphi*sphi);
		double cphi=cos(phi);
		double slambda=sin(lambda);
		double clambda=cos(lambda);
		
		double x=(h+n)*cphi*clambda;
		double y=(h+n)*cphi*slambda;
		double z=(h+n-e2*n)*sphi;
		
		return new Position(x,y,z);
	}

	public Position backward(Position pos) {
		double x=pos.getX();
		double y=pos.getY();
		double z=pos.getZ();
		double a=ellipsoid.getA();
		double e2=ellipsoid.getEsquared();
		
		double sqrtXXpYY=sqrt(x*x+y*y);
		double p=(x*x+y*y)/(a*a);
		double q=z*z*(1-e2)/(a*a);
		double r=1/6.0*(p+q-e2*e2);
		double s=e2*e2*p*q/(4*r*r*r);
		double t=pow(1+s+sqrt(s*(2+s)),1/3.0);
		double u=r*(1+t+1/t);
		double v=sqrt(u*u+e2*e2*q);
		double w=e2*(u+v-q)/(2*v);
		double k=sqrt(u+v+w*w)-w;
		double D=k*sqrtXXpYY/(k+e2);
		double sqrtDDpZZ=sqrt(D*D+z*z);
		
		double lon=toDegrees(2*atan2(y,x+sqrtXXpYY));
		double lat=toDegrees(2*atan2(z,D+sqrtDDpZZ));
		double elevation=(k+e2-1)*sqrtDDpZZ/k;
		
		return new Position(lon,lat,elevation);
	}
}
