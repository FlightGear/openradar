/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2016 Wolfram Wagner 
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
package de.knewcleus.openradar.radardata.fgmp;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.ISSRData;
import de.knewcleus.openradar.rpvd.contact.ContactShape;

public class RadarDataPacket implements IRadarDataPacket {
	protected final TargetStatus targetStatus;
	protected float timestamp;
	protected final Point2D position;
	protected final float trueCourse;
	protected final float groundSpeed;
	protected ContactShape contactShape = new ContactShape();;

	public RadarDataPacket(TargetStatus targetStatus) {
		this.targetStatus=targetStatus;
		this.timestamp=(float)targetStatus.getPositionTime();
		final Position geodeticPosition=targetStatus.getGeodeticPosition();
		position=new Point2D.Double(geodeticPosition.getX(), geodeticPosition.getY());
		groundSpeed=(float)targetStatus.getLinearVelocity().getLength();
		trueCourse=(float)targetStatus.getTrueCourse();
	}
	
    public TargetStatus getTargetStatus() {
        return targetStatus;
    }

    @Override
	public Object getTrackingIdentifier() {
		return targetStatus;
	}

	@Override
	public float getTimestamp() {
		return timestamp;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

	@Override
	public boolean wasSeenOnLastScan() {
		/* We only send packets on targets we have seen */
		return true;
	}

	@Override
	public ISSRData getSSRData() {
		/* FG Multiplayer does not transmit SSR data */
		return null;
	}

	@Override
	public float getCalculatedTrueCourse() {
		return trueCourse;
	}

	@Override
	public float getCalculatedVelocity() {
		return groundSpeed;
	}
	@Override
	public synchronized ContactShape getContactShape() {
	    return contactShape;
	}
}
