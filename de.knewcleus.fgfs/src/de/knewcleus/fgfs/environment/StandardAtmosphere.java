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
package de.knewcleus.fgfs.environment;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

public class StandardAtmosphere {
	/* 0Â°C in absolute temperature */
	public static final double THETA_0=273.15;
	/* gravitational accelleration at 45Â° latitude (m/s^2) */
	public static final double G0=9.80665;
	/* universal gas constant in J/mole/K */
	public static final double R_STAR=8.31432;
	/* mean molecular mass of air (kg/mole) */
	public static final double M=0.0289644;
	/* gas constant for air (J/kg/K) */
	public static final double R=R_STAR/M;
	/* ratio of specific heat capacities of air, cp/cv) */
	public static final double GAMMA=1.4;
	/* Sutherland constant (K) */
	public static final double S=110.4;
	/* beta (kg/s*m*K^0.5) */
	public static final double BETA=1.458E-6;
	
	protected static final int LAYER_COUNT=8;
	
	/* geopotential altitudes in m */
	protected double[] h={0,11000,20000,32000,47000,51000,71000,84852};
	/* lapse rate in K/m */
	protected double[] lambda={-6.5E-3,0,1E-3,2.8E-3,0,-2.8E-3,-2.0E-3};
	/* base temperature for each layer */
	protected double[] theta=new double[LAYER_COUNT];
	/* base pressure for each layer (in Pa or 1/100 hPa) */
	protected double[] p=new double[LAYER_COUNT];
	
	/* hidden constructor */
	protected StandardAtmosphere() {
		/* calculate the base values for the layers */
		theta[0]=THETA_0+15.0;
		p[0]=101325.0;
		
		for (int n=1;n<LAYER_COUNT;n++) {
			theta[n]=modelTemperature(n-1, h[n]);
			p[n]=modelPressure(n-1,h[n]);
		}
	}

	protected static StandardAtmosphere inst=new StandardAtmosphere();
	
	public static StandardAtmosphere getInstance() {
		return inst;
	}
	
	public class AtmosphereLevel implements AtmosphericConditions {
		protected double hgeop;
		protected int idx;
		protected double temperature;
		protected double pressure;
		protected double density;
		protected double viscosity;
		
		public AtmosphereLevel(double hgeop) {
			this.hgeop=hgeop;
			this.idx=findLayer(hgeop);
			temperature=modelTemperature(idx, hgeop);
			pressure=modelPressure(idx, hgeop);
			density=pressure/R/temperature;
			viscosity=BETA*pow(temperature,1.5)/(temperature+S);
		}

		public double getGeometricHeight() {
			return AtmosphereHelper.getGeometricHeight(hgeop);
		}
		
		public double getGeopotentialHeight() {
			return hgeop;
		}

		public int getIdx() {
			return idx;
		}

		public double getTemperature() {
			return temperature;
		}

		public double getPressure() {
			return pressure;
		}

		public double getDensity() {
			return density;
		}

		public double getViscosity() {
			return viscosity;
		}
	}
	
	public AtmosphericConditions getLevel(double hgeop) {
		return new AtmosphereLevel(hgeop);
	}
	
	protected int findLayer(double hgeop) {
		for (int idx=0;idx<LAYER_COUNT-1;idx++) {
			if (hgeop<h[idx+1])
				return idx;
		}
		return LAYER_COUNT;
	}
	
	protected double modelTemperature(int idx, double hgeop) {
		return theta[idx]+(hgeop-h[idx])*lambda[idx];
	}
	
	protected double modelPressure(int idx, double hgeop) {
		if (abs(lambda[idx])<1E-10) {
			// isothermal
			return p[idx]*exp(-(hgeop-h[idx])*G0/(R*theta[idx]));
		} else {
			return p[idx]*pow(1+(hgeop-h[idx])*lambda[idx]/theta[idx],-G0/lambda[idx]/R);
		}
	}
}
