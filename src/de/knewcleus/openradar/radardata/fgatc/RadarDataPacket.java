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
package de.knewcleus.openradar.radardata.fgatc;

import java.awt.geom.Point2D;

import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.ISSRData;
import de.knewcleus.openradar.rpvd.contact.ContactShape;

public class RadarDataPacket implements IRadarDataPacket {
	protected final Object trackingIdentifier;
	protected final PositionPacket packet;
	protected final Point2D position;
	protected final SSRData ssrData;
	protected final float groundspeed, trueCourse;

	public RadarDataPacket(TargetStatus targetStatus) {
		trackingIdentifier = targetStatus.getTargetIdentifier();
		packet = targetStatus.getLastPacket();
		position = new Point2D.Double( packet.getLongitude(), packet.getLatitude() );
		ssrData = new SSRData(packet);
		trueCourse = targetStatus.getTrueCourse();
		groundspeed = targetStatus.getGroundspeed();
	}

	@Override
	public float getTimestamp() {
		return packet.getPositionTime();
	}

	@Override
	public Object getTrackingIdentifier() {
		return trackingIdentifier;
	}

	@Override
	public boolean wasSeenOnLastScan() {
		/* We only send message of targets we have seen */
		return true;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

	@Override
	public ISSRData getSSRData() {
		if (!packet.isSSRActive() && !packet.isEncoderActive()) {
			return null;
		}
		return ssrData;
	}

	@Override
	public float getCalculatedTrueCourse() {
		return trueCourse;
	}

	@Override
	public float getCalculatedVelocity() {
		return groundspeed;
	}

	@Override
	public ContactShape getContactShape() {
		throw new IllegalStateException("not implemented");
	}

}
