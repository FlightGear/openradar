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
package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IDME;
import de.knewcleus.fgfs.navdata.model.IFrequency;

public class DME extends AbstractNamedTransmitter implements IDME {
	protected final float distanceBias;

	public DME(Point2D geographicPosition, float elevation,
			String identification, String name, IFrequency frequency, float range, float distanceBias) {
		super(geographicPosition, elevation, identification, name, frequency, range);
		this.distanceBias = distanceBias;
	}

	@Override
	public float getDistanceBias() {
		return distanceBias;
	}
	
	@Override
	public String toString() {
		return String.format("DME %3s %+10.6f %+11.6f elev %4fft freq %s bias %+6.1fNM range %3fNM name %s",
				identification,
				geographicPosition.getY(),
				geographicPosition.getX(),
				elevation / Units.FT,
				frequency.toString(),
				distanceBias / Units.NM,
				range / Units.NM,
				name);
	}
}
