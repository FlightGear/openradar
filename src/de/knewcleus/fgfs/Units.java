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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
	/**Statute miles*/
	public final static float SM=1609.0f*M;
	
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

    public static double getMphToKnots(double mph) {
        return mph*SM/NM;
    }
}
